/*
 * @(#)$RCSfile: BlockingCallArguments.java,v $        $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.BlockingCallArguments.BLOCK_CONFIG;
import static com.choicemaker.cmtblocking.BlockingCallArguments.CONDITION_1;
import static com.choicemaker.cmtblocking.BlockingCallArguments.CONDITION_2;
import static com.choicemaker.cmtblocking.BlockingCallArguments.QUERY;
import static com.choicemaker.cmtblocking.BlockingCallArguments.READ_CONFIG;
import static com.choicemaker.cmtblocking.BlockingCallArguments.SEPARATOR;
import static com.choicemaker.cmtblocking.BlockingCallArguments.fieldNames;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 *
 * @author rphall
 * @version $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class UnionParameterized {

	private static final Logger logger =
		Logger.getLogger(UnionParameterized.class.getName());

	public static final String SELECT_SEPARATOR_REGEX = "\\^";

	public static final String COMPRESSED_WHERE = "`";

	public static final String INSERT_SELECT =
		"INSERT INTO tb_cmt_temp_ids(id) SELECT ";

	public static final String WHERE = " WHERE ";

	// public static final String UNION = "UNION ";
	//
	// public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;
	//
	public static final String SQL_ALTER_SESSION =
		"ALTER SESSION SET nls_date_format = 'YYYY-MM-DD'";

	public static final String SQL_TRUNCATE_TABLE =
		"TRUNCATE TABLE tb_cmt_temp_ids";

	public static final String SQL_SELECT_IDS =
		"Select distinct id from tb_cmt_temp_ids";

	public static String[] computeSql(String s) {
		List<String> literals = new ArrayList<>();
		String[] stmts = s.split(SELECT_SEPARATOR_REGEX);
		for (String compressed : stmts) {
			String partial = compressed.replace(COMPRESSED_WHERE, WHERE);
			StringBuilder sb =
				new StringBuilder().append(INSERT_SELECT).append(partial);
			String literal = sb.toString();
			literals.add(literal);
		}
		parameterize(literals);
		String[] retVal = null; // FIXME sb.toString();
		return retVal;
	}

	public static void doSql(Connection connection,
			BlockingParams blockingParams, String sql) throws SQLException {
		logInfo("prepareCall( '" + sql + "' )");
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.setFetchSize(100);

			ResultSet outer = null;
			try {

				logInfo("execute and retrieve outer");
				outer = stmt.executeQuery(sql);

				outer.setFetchSize(100);
				outer.next();
				ResultSetMetaData metaData = outer.getMetaData();
				int numberOfColumns = metaData.getColumnCount();

				ResultSet[] rs = new ResultSet[numberOfColumns];
				for (int i = 0; i < numberOfColumns; i++) {
					int colNum = i + 1;
					logInfo("Retrieve column '" + colNum
							+ "' of first row of outer blocking result set");
					Object o = outer.getObject(colNum);
					if (o instanceof ResultSet) {
						logInfo("retrieve nested cursor: " + i);
						rs[i] = (ResultSet) outer.getObject(colNum);
						rs[i].setFetchSize(100);
					}
					logInfo("open dbr");
				}
				BlockingCall.retrieveData(connection, rs);

			} finally {
				try {
					if (outer != null) {
						outer.close();
						outer = null;
					}
				} catch (SQLException x2) {
					logException("unable to close outer result set", x2);
				}
			}

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException x2) {
				logException("unable to close prepared SQL", x2);
			}
		}
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException(logger, msg, x);
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(logger, msg);
	}

	private static void parameterize(List<String> literals) {
		// TODO Auto-generated method stub

	}

	public static void prepareConnection(Connection conn,
			BlockingParams blockingParams) throws SQLException {
		Statement st = null;
		boolean returnIsResultSet = false;
		try {
			logInfo("Alter session; set NLS_DATE_FORMAT");
			st = conn.createStatement();
			returnIsResultSet = st.execute(SQL_ALTER_SESSION);
		} finally {
			if (returnIsResultSet) {
				JdbcUtil.closeResultSet(st.getResultSet());
			}
			JdbcUtil.closeStatement(st);
			st = null;
		}
	}

	public static void prepareTempTable(Connection conn,
			BlockingParams blockingParams) throws SQLException {
		Statement st = null;
		boolean returnIsResultSet = false;
		try {
			logInfo("Truncate tb_cmt_temp_ids table");
			st = conn.createStatement();
			returnIsResultSet = st.execute(SQL_TRUNCATE_TABLE);
		} finally {
			if (returnIsResultSet) {
				JdbcUtil.closeResultSet(st.getResultSet());
			}
			JdbcUtil.closeStatement(st);
			st = null;
		}
	}

	private Map<String, String> map = new HashMap<>();

	/**
	 * Expects a line of 5 fields, separated by "|"
	 * 
	 * @param line
	 * @return a new set of blocking call arguments
	 */
	public UnionParameterized(String line) {
		if (line == null || line.trim().length() == 0) {
			throw new IllegalArgumentException("null or blank line");
		}

		int count = 0;
		StringTokenizer st = new StringTokenizer(line, SEPARATOR);
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			if (value != null && value.trim().length() == 0) {
				value = null;
			}
			if (count < fieldNames().length) {
				map.put(fieldNames()[count], value);
				++count;
			} else if (count >= fieldNames().length) {
				throw new IllegalArgumentException(
						"too many fields in '" + line + "'");
			}
		}
		if (count < fieldNames().length) {
			throw new IllegalArgumentException(
					"'" + line + "' missing '" + fieldNames()[count] + "'");
		}
	}

	public String[] computeSql() {
		return computeSql(getQuery());
	}

	public String getBlockConfig() {
		return this.map.get(BLOCK_CONFIG);
	}

	public String getCondition1() {
		return this.map.get(CONDITION_1);
	}

	public String getCondition2() {
		return this.map.get(CONDITION_2);
	}

	public String getQuery() {
		return this.map.get(QUERY);
	}

	public String getReadConfig() {
		return this.map.get(READ_CONFIG);
	}

	void logInfo() {
		logInfo("Blocking configuration: '" + getBlockConfig() + "'");
		logInfo("Query: '" + getQuery() + "'");
		logInfo("Condition 1: '" + getCondition1() + "'");
		logInfo("Condition 2: '" + getCondition2() + "'");
		logInfo("Read configuration: '" + getReadConfig() + "'");
	}

}
