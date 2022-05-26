/*
 * Copyright (c) 2014, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.LogUtil.logExtendedException;
import static com.choicemaker.cmtblocking.LogUtil.logExtendedInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.io.db.oracle.OracleRemoteDebugging;

/**
 * Reads through a script and for each line in the script, invokes the Oracle
 * stored procedure <code>Blocking</code> in the <code>CMTBlocking</code>
 * package.
 * 
 * @author rphall
 * @version $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class MainLoadTableBlockingArgs {

	private static final Logger logger =
		Logger.getLogger(MainLoadTableBlockingArgs.class.getName());

	private static final String SOURCE =
		MainLoadTableBlockingArgs.class.getSimpleName();

	public static final String MAP_FILE_PREFIX = "BlockingArgsIndexMap_";
	public static final String MAP_FILE_SUFFIX = ".txt";

	static final String SQL_GET_MAX_BLOCKING_ARGS_ID =
		"SELECT max(args_id) FROM TEST_BLOCKING_ARGS";

	static final String SQL_INSERT_BLOCKING_ARGS =
		"INSERT INTO TEST_BLOCKING_ARGS( "
				+ "ARGS_ID, BLOCKING_CONF, QUERY, CONDITION_1, CONDITION_2, "
				+ "READ_CONFIG) VALUES( :v0, :v1, :v2, :v3, :v4, :v5 )";

	public static void main(String[] args) throws IOException, SQLException {

		LogUtil.logSystemProperties(logger);

		CJBS cjbs = CJBS.parseArgs(SOURCE, logger, args, MAP_FILE_PREFIX,
				MAP_FILE_SUFFIX);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null && cjbs.sqlIdMap != null) {

			Connection conn = null;
			try {
				conn = prepareConnection(cjbs);
				final int maxBlockingArgsIndex = getMaxBlockingArgsIndex(conn);

				PreparedStatement stmtInsertBlockingArgs =
					conn.prepareStatement(SQL_INSERT_BLOCKING_ARGS);
				Map<Integer, String> blockingQueryIndices = processQueries(
						maxBlockingArgsIndex, stmtInsertBlockingArgs, cjbs);
				writeBlockingQueryIndices(cjbs, blockingQueryIndices);

				logExtendedInfo(logger, "Committing connection...");
				conn.commit();
				logExtendedInfo(logger, "Connection committed");
			} finally {
				if (conn != null) {
					logExtendedInfo(logger, "Closing JDBC connection...");
					JdbcUtil.closeConnection(conn);
					conn = null;
					logExtendedInfo(logger, "JDBC connection closed");
				}
			}
		}

	} // main(String[])

	private static Connection prepareConnection(CJBS cjbs) throws SQLException {

		logExtendedInfo(logger, "Opening JDBC connection...");
		Connection retVal = cjbs.jdbcParams.getConnection();
		logExtendedInfo(logger, "JDBC connection opened");

		logExtendedInfo(logger, "Configuring Oracle remote debugging...");
		OracleRemoteDebugging.doDebugging(retVal);
		logExtendedInfo(logger, "Oracle remote debugging configured");

		logExtendedInfo(logger, "Committing connection...");
		retVal.commit();
		logExtendedInfo(logger, "Connection committed");

		return retVal;
	}

	private static Map<Integer, String> processQueries(
			final int maxBlockingArgsIndex,
			PreparedStatement stmtInsertBlockingArgs, CJBS cjbs)
			throws SQLException {

		int blockingArgsIndex = maxBlockingArgsIndex;
		Map<Integer, String> retVal = new HashMap<>();
		logExtendedInfo(logger, "Starting insertion of blocking args");
		while (cjbs.scriptIterator.hasNext()) {

			String line = cjbs.scriptIterator.next();
			BlockingCallArguments bca =
				BlockingCallArguments.parseScriptLine(line);
			++blockingArgsIndex;

			try {
				logger.fine(
						"Inserting blocking args " + blockingArgsIndex + "...");
				insertBlockingArgs(stmtInsertBlockingArgs, blockingArgsIndex, bca, retVal);
				logger.fine("Blocking args " + blockingArgsIndex + " inserted");

			} catch (Exception x2) {
				logExtendedException(logger, "Blocking call failed", x2);
			}

		}
		logExtendedInfo(logger, "Finished inserting Blocking args");

		return retVal;
	}

	static int getMaxBlockingArgsIndex(Connection conn) throws SQLException {

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(SQL_GET_MAX_BLOCKING_ARGS_ID);

		boolean hasNext = rs.next();
		assert hasNext;
		int retVal = rs.getInt(1);

		hasNext = rs.next();
		assert !hasNext;

		return retVal;
	}

	private static void insertBlockingArgs(PreparedStatement stmt,
			final int blockingArgsIndex, BlockingCallArguments bca,
			Map<Integer, String> retVal) throws SQLException {
		// "INSERT INTO TEST_BLOCKING_ARGS( "
		// + "ARGS_ID, BLOCKING_CONF, QUERY, CONDITION_1, CONDITION_2, "
		// + "READ_CONFIG) VALUES( :v0, :v1, :v2, :v3, :v4, :v5 )";
		stmt.setInt(1, blockingArgsIndex);
		stmt.setString(2, bca.getBlockConfig());
		stmt.setString(3, bca.getQuery());
		stmt.setString(4, bca.getCondition1());
		stmt.setString(5, bca.getCondition2());
		stmt.setString(6, bca.getReadConfig());
		boolean isResultSet = stmt.execute();
		assert !isResultSet;
		assert stmt.getUpdateCount() == 1;
		retVal.put(blockingArgsIndex, bca.getQuery());
	}

	private static void writeBlockingQueryIndices(CJBS cjbs,
			Map<Integer, String> blockingQueryIndices) throws IOException {

		PrintWriter pw = null;
		try {
			String filePath = cjbs.sqlIdMap.getAbsolutePath();
			logExtendedInfo(logger,
					"Writing BlockingQueryIndex file '" + filePath + "' ...");
			FileWriter fw = new FileWriter(cjbs.sqlIdMap);
			pw = new PrintWriter(fw);
			for (Map.Entry<Integer, String> entry : blockingQueryIndices
					.entrySet()) {
				String line = AppUtils.labelValue(entry.getKey().toString(),
						entry.getValue());
				pw.println(line);
			}
			logExtendedInfo(logger,
					"BlockingQueryIndex file '" + filePath + "' written");
		} finally {
			if (pw != null) {
				logExtendedInfo(logger, "Closing BlockingQueryIndex file ...");
				pw.flush();
				pw.close();
				pw = null;
				logExtendedInfo(logger, "BlockingQueryIndex file closed");
			}
		}
	}

} // class Main
