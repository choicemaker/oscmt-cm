/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle.blocking;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Level;
//import com.choicemaker.cm.core.Accessor;
//import com.choicemaker.cm.core.ImmutableProbabilityModel;
//import com.choicemaker.cm.io.blocking.automated.IBlockingField;
//import com.choicemaker.cm.io.blocking.automated.IBlockingSet;
//import com.choicemaker.cm.io.blocking.automated.IBlockingValue;
//import com.choicemaker.cm.io.blocking.automated.IDbField;
//import com.choicemaker.cm.io.blocking.automated.IGroupTable;
//import com.choicemaker.cm.io.db.base.DbAccessor;
//import com.choicemaker.cm.io.db.oracle.OracleRemoteDebugging;
//import com.choicemaker.util.Precondition;
//import oracle.jdbc.OracleTypes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;

/**
 * Stubbed, alternative version to OraDatabaseAccessor. NOT YET FUNCTIONAL (and
 * may never be)
 * 
 * @author rphall
 */
public class OraDatabaseAccessor2 implements DatabaseAccessor {
	// private static final char BS_SEP = '^';
	// private static final char TB_VAL_SEP = '`';
	// private static final int MAX_LEN = 3950;
	private static Logger logger =
		Logger.getLogger(OraDatabaseAccessor2.class.getName());
	private DataSource ds;
	private Connection connection;
	private DbReaderParallel dbr;
	private ResultSet outer;
	private ResultSet[] rs;
	private CallableStatement stmt;
	private String condition1;
	private String condition2;

	public OraDatabaseAccessor2() {
	}

	public OraDatabaseAccessor2(DataSource ds, String condition1,
			String condition2) {
		this.ds = ds;
		this.condition1 = condition1;
		this.condition2 = condition2;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		ds = dataSource;
	}

	@Override
	public void setCondition(Object condition) {
		if (condition instanceof String[]) {
			String[] cs = (String[]) condition;
			condition1 = cs[0];
			condition2 = cs[1];
		} else if (condition instanceof String) {
			condition2 = (String) condition;
		}
	}

	@Override
	public DatabaseAccessor cloneWithNewConnection()
			throws CloneNotSupportedException {
		DatabaseAccessor retVal =
			new OraDatabaseAccessor2(this.ds, this.condition1, this.condition2);
		return retVal;
	}

	@Override
	public void open(AutomatedBlocker blocker, String databaseConfiguration)
			throws IOException {
		throw new Error("not yet implemented");
		// logger.fine("open");
		// ImmutableProbabilityModel model = blocker.getModel();
		// Accessor acc = model.getAccessor();
		// dbr = ((DbAccessor) acc).getDbReaderParallel(databaseConfiguration);
		//
		// try {
		// connection = ds.getConnection();
		//
		// double bseqId = getBlockingSequenceId(connection);
		//
		// OracleRemoteDebugging.doDebugging(connection);
		//
		// String[] queries = getQueries(bseqId, blocker, dbr);
		//
		// logger.fine("query length: " + queries.length);
		// logger.fine("queries: " + Arrays.toString(queries));
		//
		// for (String query : queries) {
		// logger.fine("query: " + query);
		// stmt = connection
		// .prepareCall(query);
		// logger.fine("execute");
		// stmt.execute();
		// }
		//
		// int len = dbr.getNoCursors();
		// rs = new ResultSet[len];
		// String[] selects = getRowSelections(bseqId, blocker, dbr);
		// for (String select : selects) {
		// rs[i] =
		// }
		//
		// if (logger.isLoggable(Level.FINE)) {
		// logger.fine("call CMTBlocking.Blocking('"
		// + blocker.getBlockingConfiguration().getBlockingConfiguationId() +
		// "', '"
		// + query + "', '" + condition1 + "', '" + condition2
		// + "' ,'" + acc.getSchemaName() + ":r:"
		// + databaseConfiguration + "', '?')");
		// }
		//
		// stmt = connection
		// .prepareCall("call CMTBlocking.Blocking(?, ?, ?, ?, ?, ?)");
		// stmt.setFetchSize(100);
		// stmt.setString(1,
		// blocker.getBlockingConfiguration().getBlockingConfiguationId());
		// stmt.setString(2, query);
		// stmt.setString(3, condition1);
		// stmt.setString(4, condition2);
		// stmt.setString(5,
		// acc.getSchemaName() + ":r:" + databaseConfiguration);
		// stmt.registerOutParameter(6, OracleTypes.CURSOR);
		// logger.fine("execute");
		// stmt.execute();
		// logger.fine("retrieve outer");
		// outer = (ResultSet) stmt.getObject(6);
		// outer.setFetchSize(100);
		// /* int */ len = dbr.getNoCursors();
		// if (len == 1) {
		// rs = new ResultSet[] {
		// outer };
		// outer = null;
		// } else {
		// outer.next();
		// rs = new ResultSet[len];
		// for (int i = 0; i < len; ++i) {
		// logger.fine("retrieve nested cursor: " + i);
		// rs[i] = (ResultSet) outer.getObject(i + 1);
		// rs[i].setFetchSize(100);
		// }
		// }
		// logger.fine("open dbr");
		// dbr.open(rs);
		// } catch (SQLException ex) {
		// logger.severe("call CMTBlocking.Blocking('"
		// + blocker.getBlockingConfiguration().getBlockingConfiguationId() +
		// "', '"
		// + query + "', '" + condition1 + "', '" + condition2 + "' ,'"
		// + acc.getSchemaName() + ":r:" + databaseConfiguration
		// + "', '?'): " + ex.toString());
		// throw new IOException(ex.toString());
		// }
	}

	// private String[] getRowSelections(double bseqId, AutomatedBlocker
	// blocker,
	// DbReaderParallel dbr2) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// private double getBlockingSequenceId(Connection connection2) {
	// // TODO Auto-generated method stub
	// return 0;
	// }

	@Override
	public void close() throws IOException {
		logger.fine("close");
		Exception ex = null;
		if (rs != null) {
			for (int i = 0; i < rs.length; ++i) {
				if (rs[i] != null) {
					try {
						rs[i].close();
					} catch (java.sql.SQLException e) {
						ex = e;
						logger.severe("Closing cursors." + e.toString());
					}
				}
			}
		}
		rs = null;
		if (outer != null) {
			try {
				outer.close();
			} catch (java.sql.SQLException e) {
				ex = e;
				logger.severe("Closing cursors." + e.toString());
			}
			outer = null;
		}
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (java.sql.SQLException e) {
			ex = e;
			logger.severe("Closing statement." + e.toString());
		}
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (java.sql.SQLException e) {
				ex = e;
				logger.severe("Closing connection." + e.toString());
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
	public Record getNext() throws IOException {
		try {
			return dbr.getNext();
		} catch (SQLException ex) {
			logger.severe("getNext: " + ex.toString());
			throw new IOException(ex.toString());
		}
	}

	// Public for testing
	public static String[] getQueries(double bseqId, AutomatedBlocker blocker,
			DbReaderParallel dbr) {

		throw new Error("not yet implemented");

		// // Preconditions
		// Precondition.assertNonNullArgument("null blocker", blocker);
		// Precondition.assertNonNullArgument("null DB reader", dbr);
		//
		// Map<Object,Object> blockingVariable = new HashMap<>();
		// Map<String, Object> variableValue = new HashMap<>();
		//
		// StringBuffer b = new StringBuffer(4000);
		// boolean firstBlockingSet = true;
		// String masterId = null;
		// for (IBlockingSet bs : blocker.getBlockingSets()) {
		// if (firstBlockingSet) {
		// masterId = bs.getTable(0).getTable().getUniqueId();
		// firstBlockingSet = false;
		// } else {
		// b.append(BS_SEP);
		// }
		// b.append(" v0.");
		// b.append(masterId);
		// b.append(" FROM ");
		// int numTables = bs.getNumTables();
		// for (int j = 0; j < numTables; ++j) {
		// if (j != 0) {
		// b.append(",");
		// }
		// IGroupTable gt = bs.getTable(j);
		// b.append(gt.getTable().getName()).append(" v")
		// .append(gt.getNumber());
		// }
		// b.append(TB_VAL_SEP);
		// int numValues = bs.numFields();
		// for (int j = 0; j < numValues; ++j) {
		// if (j != 0) {
		// b.append(" AND ");
		// }
		// IBlockingValue bv = bs.getBlockingValue(j);
		// IBlockingField bf = bv.getBlockingField();
		// IDbField dbf = bf.getDbField();
		// String fieldAlias = new StringBuilder().append("v")
		// .append(bs.getGroupTable(bf).getNumber()).append(".")
		// .append(dbf.getName()).toString();
		// // b.append("v").append(bs.getGroupTable(bf).getNumber())
		// // .append(".").append(dbf.getName()).append("=");
		// b.append(fieldAlias).append("=");
		// String fieldValue = bv.getValue();
		// if (mustQuote(bf.getDbField().getType())) {
		// b.append("'" + escape(fieldValue) + "'");
		// } else {
		// b.append(escape(fieldValue));
		// }
		// }
		// }
		// return b.toString();
	}

	public static boolean mustQuote(String type) {
		return !(type == "byte" || type == "short" || type == "int"
				|| type == "long" || type == "float" || type == "double");
	}

	// private static String escape(String s) {
	// int len = s.length();
	// int pos = 0;
	// char ch;
	// while (pos < len && (ch = s.charAt(pos)) != BS_SEP && ch != TB_VAL_SEP
	// && ch != '\'' && ch >= 32) {
	// ++pos;
	// }
	// if (pos == len) {
	// return s;
	// } else {
	// char[] res = new char[len * 2];
	// for (int i = 0; i < pos; ++i) {
	// res[i] = s.charAt(i);
	// }
	// int out = pos;
	// while (pos < len) {
	// ch = s.charAt(pos);
	// if (ch == '\'') {
	// res[out++] = '\'';
	// res[out++] = '\'';
	// } else if (ch >= 32 && ch != BS_SEP && ch != TB_VAL_SEP) {
	// res[out++] = ch;
	// }
	// ++pos;
	// }
	// return new String(res, 0, out);
	// }
	// }

}
