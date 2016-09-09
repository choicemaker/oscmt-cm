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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.choicemaker.util.Precondition;
import com.choicemaker.util.SystemPropertyUtils;

/**
 *
 * @author rphall
 * @version $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class UnionLiteral {

	private static final String SOURCE = "UnionLiteral";

	public static final String SELECT_SEPARATOR_REGEX = "\\^";

	public static final String COMPRESSED_WHERE = "`";

	public static final String PREFIX = "SELECT ";

	public static final String WHERE_IDX_0 = " WHERE ";

	public static final String WHERE_IDX_1 =
		" WHERE v0.mci_id = v1.mci_id AND ";

	public static final String UNION = "UNION ";

	public static final String SUFFIX = SystemPropertyUtils.PV_LINE_SEPARATOR;

	public static final String REGEX_WORD_BOUNDARY = "\\b";

	public static final String TABLE_ALIAS_BASE = "v";

	public static String regexTableAlias(int idx) {
		Precondition.assertBoolean("negative index", idx > -1);
		StringBuilder sb = new StringBuilder().append(REGEX_WORD_BOUNDARY)
				.append(TABLE_ALIAS_BASE).append(idx)
				.append(REGEX_WORD_BOUNDARY);
		String retVal = sb.toString();
		return retVal;
	}

	private static AtomicReference<Map<Integer, Pattern>> tableAliasPatternsRef =
		new AtomicReference<>(null);
	
	private static final int LARGEST_IDX_EVER_EXPECTED_AND_THEN_SOME = 10;

	private static Map<Integer, Pattern> createTableAliasPatterns() {
		Map<Integer, Pattern> map = new LinkedHashMap<>();
		for (int idx = 0; idx < LARGEST_IDX_EVER_EXPECTED_AND_THEN_SOME; idx++) {
			String regex = regexTableAlias(idx);
			Pattern p = Pattern.compile(regex);
			map.put(idx, p);
		}
		return Collections.unmodifiableMap(map);
	}

	static Map<Integer, Pattern> tableAliasPatterns() {
		Map<Integer, Pattern> retVal = tableAliasPatternsRef.get();
		if (retVal == null) {
			Map<Integer, Pattern> update = createTableAliasPatterns();
			boolean updated = tableAliasPatternsRef.compareAndSet(null, update);
			retVal = tableAliasPatternsRef.get();
			assert updated || !update.equals(retVal);
		}
		assert retVal != null;
		return retVal;
	}

	/**
	 * Returns the index <code>N</code> of the largest table alias
	 * <code>vN</code> found in the specified String, assuming that all tables
	 * indices up to <code>N</code> are also present.
	 * 
	 * @param s
	 *            possibly null.
	 * @return -1 if s is null or s does not contain a table alias
	 */
	public static int largestTableAliasIndex(String s) {
		final Map<Integer, Pattern> patterns = tableAliasPatterns();
		int retVal = -1;
		for (int i : patterns.keySet()) {
			// This method requires keys in ascending order
			assert i == retVal + 1;
			Pattern p = patterns.get(i);
			Matcher m = p.matcher(s);
			if (!m.find()) {
				break;
			}
			retVal = i;
		}

		// Sanity checks if assertions are enabled
		boolean assertOn = false;
		assert assertOn = true;
		if (assertOn && retVal > -1) {
			int i = 0;
			for (; i <= retVal; i++) {
				Pattern p = patterns.get(i);
				Matcher m = p.matcher(s);
				assert m.find();
			}
			for (i=retVal+1 ; i< LARGEST_IDX_EVER_EXPECTED_AND_THEN_SOME; i++) {
				Pattern p = patterns.get(i);
				Matcher m = p.matcher(s);
				assert !m.find();
			}
		}
		
		return retVal;
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(SOURCE, msg);
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException(SOURCE, msg, x);
	}

	private Map<String, String> map = new HashMap<>();

	public String getBlockConfig() {
		return (String) this.map.get(BLOCK_CONFIG);
	}

	public String getQuery() {
		return (String) this.map.get(QUERY);
	}

	public String getCondition1() {
		return (String) this.map.get(CONDITION_1);
	}

	public String getCondition2() {
		return (String) this.map.get(CONDITION_2);
	}

	public String getReadConfig() {
		return (String) this.map.get(READ_CONFIG);
	}

	/**
	 * Expects a line of 5 fields, separated by "|"
	 * 
	 * @param line
	 * @return a new set of blocking call arguments
	 */
	public UnionLiteral(String line) {
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

	void logInfo() {
		logInfo("Blocking configuration: '" + getBlockConfig() + "'");
		logInfo("Query: '" + getQuery() + "'");
		logInfo("Condition 1: '" + getCondition1() + "'");
		logInfo("Condition 2: '" + getCondition2() + "'");
		logInfo("Read configuration: '" + getReadConfig() + "'");
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

				logInfo("Counting ids from result set...");
				outer.setFetchSize(100);
				int idCount = 0;
				SortedSet<String> distinctIds = new TreeSet<>();
				while (outer.next()) {
					++idCount;
					if (idCount == 1) {
						ResultSetMetaData metaData = outer.getMetaData();
						int numberOfColumns = metaData.getColumnCount();
						assert numberOfColumns == 1;
					}
					String id = outer.getString(1);
					distinctIds.add(id);
				}
				logInfo("Total number of ids: " + idCount);
				logInfo("Number of distinct ids: " + distinctIds.size());

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

	public String computeSql() {
		return computeSql(getQuery());
	}

	public static String computeSql(String s) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		String[] stmts = s.split(SELECT_SEPARATOR_REGEX);
		for (String compressed : stmts) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(UNION);
			}
	
			String partial = null;
			int largestAliasIdx = largestTableAliasIndex(compressed);
			switch (largestAliasIdx) {
			case -1:
				partial = compressed;
				break;
			case 0:
				partial = compressed.replace(COMPRESSED_WHERE, WHERE_IDX_0);
				break;
			case 1:
				partial = compressed.replace(COMPRESSED_WHERE, WHERE_IDX_1);
				break;
			default:
				String msg = "Not yet implemented for alias index " + largestAliasIdx;
				throw new Error(msg);
			}
			assert partial != null;
			sb.append(PREFIX).append(partial).append(SUFFIX);
		}
		String retVal = sb.toString();
		return retVal;
	}

	public static void prepareConnection(Connection conn,
			BlockingParams blockingParams) throws SQLException {
		Statement st = null;
		boolean returnIsResultSet = false;
		try {
			logInfo("Alter session; set NLS_DATE_FORMAT");
			st = conn.createStatement();
			returnIsResultSet =
				st.execute("ALTER SESSION SET nls_date_format = 'YYYY-MM-DD'");

			// logInfo("Configuring Oracle remote debugging...");
			// OracleRemoteDebugging.doDebugging(conn);
			// logInfo("Oracle remote debugging configured");

		} finally {
			if (returnIsResultSet) {
				BlockingCall.closeResultSet(st.getResultSet());
			}
			BlockingCall.closeStatement(st);
			st = null;
		}
	}

}
