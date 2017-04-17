/*
 * Created on Feb 15, 2005
 *
 */
package com.choicemaker.cm.io.db.jdbc.blocking;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.util.StringUtils;
import com.choicemaker.cm.io.blocking.automated.base.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.base.BlockingField;
import com.choicemaker.cm.io.blocking.automated.base.BlockingSet;
import com.choicemaker.cm.io.blocking.automated.base.BlockingValue;
import com.choicemaker.cm.io.blocking.automated.base.DatabaseAccessor;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.jdbc.dbom.JdbcDbObjectMaker;

/**
 * This is the accessor for Jdbc.
 * 
 * @author pcheung
 *
 */
public class JdbcDatabaseAccessor implements DatabaseAccessor {

	private static Logger logger = Logger.getLogger(JdbcDatabaseAccessor.class);

	private DataSource ds;
	private Connection connection;
	private DbReaderSequential dbr;
	private Statement stmt;
	private String condition;

	public void setDataSource(DataSource dataSource) {
		this.ds = dataSource;
	}

	public void setCondition(Object condition) {
		this.condition = (String)condition;
	}

	public DatabaseAccessor cloneWithNewConnection()
		throws CloneNotSupportedException {
		throw new CloneNotSupportedException("not yet implemented");
	}

	public void open(AutomatedBlocker blocker) throws IOException {
		Accessor acc = blocker.getModel().getAccessor();
		String dbrName = (String) blocker.getModel().properties().get("dbConfiguration");
		dbr = ((DbAccessor) acc).getDbReaderSequential(dbrName);
		try {
			connection = ds.getConnection();
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			
			//first create the temp table
			stmt.executeUpdate(getCreateTemp (dbr));
			connection.commit();
			stmt.close();
			
			//second insert into the temp table
			stmt = connection.createStatement();
			stmt.executeUpdate(insertIntoTemp (blocker, dbr));
			connection.commit();
			stmt.close();
			
			//third, get the data
			stmt = connection.createStatement();
			stmt.setFetchSize(100);
			ResultSet rs = stmt.executeQuery(JdbcDbObjectMaker.getMultiQuery(blocker.getModel(), dbrName));
			rs.setFetchSize(100);
			dbr.open(rs, stmt);
		} catch (SQLException ex) {
			logger.error(ex.toString());
			throw new IOException(ex.toString());
		}
	}


	public static String getCreateTemp (DbReaderSequential dbr) {
		StringBuffer b = new StringBuffer(4000);
		b.append("create temporary table ids (id ");
		b.append(dbr.getMasterIdType());
		b.append(" );");
		return b.toString();
	}
	
	
	public static String getCreateTempIndex () {
		String str = "create index temp_idx on ids (id);";
		return str;
	}


	private String insertIntoTemp (AutomatedBlocker blocker, DbReaderSequential dbr) {
		StringBuffer b = new StringBuffer(16000);
		String id = dbr.getMasterId();
		b.append("INSERT INTO ids");
		if (StringUtils.nonEmptyString(condition)) {
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
			// error.  If blocking fields are on different tables, and each table
			// has an ID column, then things don't work...
			b.append("v0." + id);
			//b.append(id);
			b.append(" FROM ");
			BlockingSet bs = (BlockingSet) blocker.getBlockingSets().get(i);
			int numViews = bs.getNumTables();
			for (int j = 0; j < numViews; ++j) {
				if (j > 0) {
					b.append(",");
				}
				BlockingSet.GroupTable gt = bs.getTable(j);
				b.append(gt.table.name).append(" v").append(gt.number);
			}
			b.append(" WHERE ");
			int numValues = bs.numFields();
			for (int j = 0; j < numValues; ++j) {
				if (j > 0) {
					b.append(" AND ");
				}
				BlockingValue bv = bs.getBlockingValue(j);
				BlockingField bf = bv.blockingField;
				com.choicemaker.cm.io.blocking.automated.base.DbField dbf = bf.dbField;
				b.append("v").append(bs.getGroupTable(bf).number).append(".").append(dbf.name).append("=");
				if (mustQuote(bf.dbField.type)) {
					b.append("'" + escape(bv.value) + "'");
				} else {
					b.append(escape(bv.value));
				}
			}
			if (numViews > 1) {
				BlockingSet.GroupTable gt0 = bs.getTable(0);
				String g0 = " AND v" + gt0.number + "." + id + "=";
				for (int j = 1; j < numViews; ++j) {
					BlockingSet.GroupTable gt = bs.getTable(j);
					b.append(g0);
					b.append("v" + gt.number + "." + id);
				}
			}
		}
		if (StringUtils.nonEmptyString(condition)) {
			b.append(") b,");
			b.append(condition);
		}
		b.append(";" + Constants.LINE_SEPARATOR);
		logger.debug(b.toString());
		
		return b.toString();
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


	private boolean mustQuote(String type) {
		return !(
			type == "byte"
				|| type == "short"
				|| type == "int"
				|| type == "long"
				|| type == "float"
				|| type == "double");
	}


	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.base.DatabaseAccessor#close()
	 */
	public void close() throws IOException {
		Exception ex = null;
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (java.sql.SQLException e) {
			ex = e;
			logger.error("Closing statement.", e);
		}
		if (connection != null) {
			try {
				connection.commit();
			} catch (java.sql.SQLException e) {
				ex = e;
				logger.error("Commiting.", e);
			}
			try {
				connection.close();
				connection = null;
			} catch (java.sql.SQLException e) {
				ex = e;
				logger.error("Closing connection.", e);
			}
		}
		if (ex != null) {
			throw new IOException(ex.toString());
		}
	}

	public boolean hasNext() {
		return dbr.hasNext();
	}

	public Record getNext() throws IOException {
		return dbr.getNext();
	}

}
