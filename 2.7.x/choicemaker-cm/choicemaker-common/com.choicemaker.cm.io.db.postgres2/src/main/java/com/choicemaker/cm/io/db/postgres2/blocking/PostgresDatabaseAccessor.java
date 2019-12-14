/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.cm.io.db.postgres2.blocking;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IBlockingSet;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.aba.IDbField;
import com.choicemaker.cm.aba.IGroupTable;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.postgres2.dbom.PostgresDbObjectMaker;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;
import com.choicemaker.util.SystemPropertyUtils;

public class PostgresDatabaseAccessor<T extends Comparable<T>>
		implements DatabaseAccessor<T> {

	private static Logger logger =
		Logger.getLogger(PostgresDatabaseAccessor.class.getName());

	private static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	// These two objects have the same life span -- see isConsistent()
	private DataSource ds;
	private Properties p;

	private Connection connection;
	private DbReaderSequential<T> dbr;
	private Statement stmt;
	private String condition1;
	private String condition2;

	/**
	 * An invariant for this class
	 */
	protected boolean isConsistent() {
		boolean isConsistent =
			(ds == null && p == null) || (ds != null && p != null);
		return isConsistent;
	}

	public PostgresDatabaseAccessor() {
		assert isConsistent();
	}

	public PostgresDatabaseAccessor(DataSource ds, String[] conditions) {
		setDataSource(ds);
		setConditions(conditions);
	}

	@Deprecated
	public PostgresDatabaseAccessor(DataSource ds, Object condition) {
		setDataSource(ds);
		setCondition(condition);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		assert isConsistent();
		this.ds = dataSource;
		this.p = ds == null ? null : new Properties();
		assert isConsistent();
	}

	private DataSource getDataSource() {
		assert isConsistent();
		return ds;
	}

	private Properties getProperties() {
		assert isConsistent();
		return p;
	}

	@Override
	@Deprecated
	public void setCondition(Object condition) {
		setConditions((String[]) condition);
	}

	public void setConditions(String[] conditions) {
		if (conditions == null || conditions.length == 0) {
			condition1 = null;
			condition2 = null;
		} else if (conditions.length == 1) {
			condition1 = null;
			condition2 = conditions[0];
		} else {
			assert conditions.length >= 2;
			condition1 = conditions[0];
			condition2 = conditions[1];
		}
		logger.finer("Condition 1 on database accessor: " + condition1);
		logger.finer("Condition 2 on database accessor: " + condition2);
		if (conditions != null && conditions.length > 2) {
			assert conditions.length > 2;
			condition1 = conditions[0];
			condition2 = conditions[1];
			final int extra = conditions.length - 2;
			StringBuilder sb =
				new StringBuilder("Ignoring " + extra + "conditions: ");
			for (int i = extra; i < conditions.length; i++) {
				sb.append(conditions[i]);
				if (i < conditions.length - 1) {
					sb.append(" | ");
				}
			}
			logger.finer(sb.toString());
		}
	}

	@Override
	public DatabaseAccessor<T> cloneWithNewConnection()
			throws CloneNotSupportedException {
		throw new CloneNotSupportedException("not yet implemented");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void open(AutomatedBlocker blocker, String databaseConfiguration)
			throws IOException {
		Accessor acc = blocker.getModel().getAccessor();
		DbAccessor dba = (DbAccessor) acc;
		dbr = dba.getDbReaderSequential(databaseConfiguration);
		String query = null;
		try {
			query = getQuery(getProperties(), blocker, dbr);
			connection = getDataSource().getConnection();
			stmt = connection.createStatement();
			stmt.setFetchSize(100);
			logger.fine(query);
			// See: How to Retrieve Multiple Result Sets from a Stored
			// Procedure in JDBC (http://links.rph.cx/m9jJav)
			ResultSet rs = null;
			boolean isResultSet = stmt.execute(query);
			int count = 0;
			do {
				if (isResultSet) {
					rs = stmt.getResultSet();
					break;
				} else {
					count = stmt.getUpdateCount();
					if (count >= 0) {
						String msg =
							"Query returned update count == '" + count + "'";
						logger.fine(msg);
					} else {
						String msg =
							"Query '" + query + "' did not return a result set";
						logger.severe(msg);
						throw new SQLException(msg);
					}
				}
				isResultSet = stmt.getMoreResults();
			} while (isResultSet || count != -1);
			rs.setFetchSize(100);
			dbr.open(rs, stmt);
		} catch (SQLException ex) {
			logger.severe(
					"Opening blocking data: " + query + ": " + ex.toString());
			throw new IOException(ex.toString());
		}
	}

	@Override
	public void close() throws IOException {
		Exception ex = null;
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (java.sql.SQLException e) {
			ex = e;
			logger.severe("Closing statement: " + e.toString());
		}
		if (connection != null) {
			// Database accessors are used only when blocking against a SQL
			// database using EJB3 managed connections. They should not be
			// explicitly committed, but rather rely on the EJB3 container to
			// do so.
			try {
				connection.close();
				connection = null;
			} catch (java.sql.SQLException e) {
				ex = e;
				logger.severe("Closing connection: " + e.toString());
			}
		}
		if (ex != null) {
			throw new IOException(ex.toString());
		}
	}

	@Override
	public boolean hasNext() {
		return dbr.hasNext();
	}

	@Override
	public Record<T> getNext() throws IOException {
		return dbr.getNext();
	}

	// Public access for testing only
	public String getQuery(final Properties unused, AutomatedBlocker blocker,
			DbReaderSequential<T> dbr) {

		Precondition.assertNonNullArgument("null blocker", blocker);
		Precondition.assertNonNullArgument("null database result-set reader",
				dbr);

		if (unused != null && unused.size() > 0) {
			logger.fine("FIXME not using properties");
		}
		if (StringUtils.nonEmptyString(condition1)) {
			logger.fine("FIXME not using condition1");
		}

		String id = dbr.getMasterId();

		StringBuilder b = new StringBuilder(16000);
		b.append("DO $$").append(EOL).append("BEGIN").append(EOL);
		b.append("DROP TABLE IF EXISTS ids;").append(EOL);
		b.append("CREATE TEMP TABLE ids(id ").append(dbr.getMasterIdType())
				.append(", PRIMARY KEY(id));").append(EOL);
		b.append("INSERT INTO ids ");

		if (StringUtils.nonEmptyString(condition2)) {
			b.append(" SELECT b.");
			b.append(id);
			b.append(" FROM (");
		}

		int numBlockingSets = blocker.getBlockingSets().size();
		for (int i = 0; i < numBlockingSets; ++i) {
			if (i == 0) {
				if (numBlockingSets > 1) {
					b.append(" SELECT ");
				} else {
					b.append(" SELECT DISTINCT ");
				}
			} else {
				b.append(" UNION SELECT ");
			}
			// AJW 2/26/04: to make stuff work for Phoenix.
			// This doesn't fix the problem, it just gets rid of a horrible
			// severe. If blocking fields are on different tables, and each
			// table has an ID column, then things don't work...
			b.append("v0." + id);
			b.append(" FROM ");
			IBlockingSet bs = blocker.getBlockingSets().get(i);
			int numViews = bs.getNumTables();
			for (int j = 0; j < numViews; ++j) {
				if (j > 0) {
					b.append(",");
				}
				IGroupTable gt = bs.getTable(j);
				b.append(gt.getTable().getName()).append(" v")
						.append(gt.getNumber());
			}
			b.append(" WHERE ");
			int numValues = bs.numFields();
			for (int j = 0; j < numValues; ++j) {
				if (j > 0) {
					b.append(" AND ");
				}
				IBlockingValue bv = bs.getBlockingValue(j);
				IBlockingField bf = bv.getBlockingField();
				IDbField dbf = bf.getDbField();
				b.append("v").append(bs.getGroupTable(bf).getNumber())
						.append(".").append(dbf.getName()).append("=");
				if (mustQuote(bf.getDbField().getType())) {
					b.append("'" + escape(bv.getValue()) + "'");
				} else {
					b.append(escape(bv.getValue()));
				}
			}
			if (numViews > 1) {
				IGroupTable gt0 = bs.getTable(0);
				String g0 = " AND v" + gt0.getNumber() + "." + id + "=";
				for (int j = 1; j < numViews; ++j) {
					IGroupTable gt = bs.getTable(j);
					b.append(g0);
					b.append("v" + gt.getNumber() + "." + id);
				}
			}
		}

		if (StringUtils.nonEmptyString(condition2)) {
			 b.append(") b,");
			 b.append(condition2);
		}
		b.append(";").append(EOL).append("END $$;").append(EOL);
		b.append(getMultiQuery(p, blocker, dbr));
		logger.fine(b.toString());

		return b.toString();
	}

	private String getMultiQuery(Properties p, AutomatedBlocker blocker,
			DbReaderSequential<T> dbr) {
		String key = dbr.getName() + ":Postgres";
		if (!p.containsKey(key)) {
			try {
				// NOTE: this loads the multi string into the properties
				PostgresDbObjectMaker.getAllModels(p);
			} catch (IOException ex) {
				logger.severe(ex.toString());
			}
		}

		return p.getProperty(key);
	}

	private boolean mustQuote(String type) {
		return !(type == "byte" || type == "short" || type == "int"
				|| type == "long" || type == "float" || type == "double");
	}

	private String escape(String s) {
		int len = s.length();
		int pos = 0;
		char ch;
		while (pos < len && (ch = s.charAt(pos)) != '\'' && ch >= 32) {
			++pos;
		}
		if (pos == len) {
			return s;
		} else {
			char[] res = new char[len * 2];
			for (int i = 0; i < pos; ++i) {
				res[i] = s.charAt(i);
			}
			int out = pos;
			while (pos < len) {
				ch = s.charAt(pos);
				if (ch == '\'') {
					res[out++] = '\'';
					res[out++] = '\'';
				} else if (ch >= 32) {
					res[out++] = ch;
				}
				++pos;
			}
			return new String(res, 0, out);
		}
	}
}
