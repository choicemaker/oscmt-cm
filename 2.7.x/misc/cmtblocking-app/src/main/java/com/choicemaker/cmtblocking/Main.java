/*
 * @(#)$RCSfile: Main.java,v $        $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.LogUtil.logExtendedException;
import static com.choicemaker.cmtblocking.LogUtil.logExtendedInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
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
public class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	private static final String SOURCE = Main.class.getSimpleName();

	public static void main(String[] args) throws IOException, SQLException {

		LogUtil.logSystemProperties(logger);

		CJBS cjbs = CJBS.parseArgs(SOURCE, logger, args);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null && cjbs.sqlIdMap != null) {

			Connection conn = null;
			try {
				conn = prepareConnection(cjbs);
				Map<String, String> sqlQueryIds = processQueries(conn, cjbs);
				writeQueries(cjbs, sqlQueryIds);
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

	private static Map<String, String> processQueries(Connection conn,
			CJBS cjbs) throws SQLException {

		Map<String, String> retVal = new HashMap<>();
		logExtendedInfo(logger, "Starting script");
		while (cjbs.scriptIterator.hasNext()) {

			String line = cjbs.scriptIterator.next();
			BlockingCallArguments bca =
				BlockingCallArguments.parseScriptLine(line);

			try {
				logExtendedInfo(logger, "Starting blocking call...");
				BlockingCall.doBlocking(conn, cjbs.blockingParams, bca,
						cjbs.config.getRepetitionCount(), retVal);
				logExtendedInfo(logger, "Blocking call returned");

				logExtendedInfo(logger, "Committing connection...");
				conn.commit();
				logExtendedInfo(logger, "Connection committed");

			} catch (Exception x2) {
				logExtendedException(logger, "Blocking call failed", x2);
			}

		}
		logExtendedInfo(logger, "Finished script");

		return retVal;
	}

	private static void writeQueries(CJBS cjbs, Map<String, String> sqlQueryIds)
			throws IOException {

		PrintWriter pw = null;
		try {
			String filePath = cjbs.sqlIdMap.getAbsolutePath();
			logExtendedInfo(logger,
					"Writing SqlId map file '" + filePath + "' ...");
			FileWriter fw = new FileWriter(cjbs.sqlIdMap);
			pw = new PrintWriter(fw);
			for (Map.Entry<String, String> entry : sqlQueryIds.entrySet()) {
				String line =
					AppUtils.labelValue(entry.getKey(), entry.getValue());
				pw.println(line);
			}
			logExtendedInfo(logger,
					"SqlId map file '" + filePath + "' written");
		} finally {
			if (pw != null) {
				logExtendedInfo(logger, "Closing SqlId map file ...");
				pw.flush();
				pw.close();
				pw = null;
				logExtendedInfo(logger, "SqlId map file closed");
			}
		}
	}

} // class Main
