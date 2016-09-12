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

	public static void main(String[] args) {

		LogUtil.logSystemProperties(logger);

		CJBS cjbs = CJBS.parseArgs(SOURCE, logger, args);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null) {

			Map<String, Integer> hashedSqlSeq = new HashMap<>();
			Connection conn = null;
			try {

				logExtendedInfo(logger, "Opening JDBC connection...");
				conn = cjbs.jdbcParams.getConnection();
				logExtendedInfo(logger, "JDBC connection opened");

				logExtendedInfo(logger,
						"Configuring Oracle remote debugging...");
				OracleRemoteDebugging.doDebugging(conn);
				logExtendedInfo(logger, "Oracle remote debugging configured");

				logExtendedInfo(logger, "Committing connection...");
				conn.commit();
				logExtendedInfo(logger, "Connection committed");

				logExtendedInfo(logger, "Starting script");
				while (cjbs.scriptIterator.hasNext()) {

					String line = cjbs.scriptIterator.next();
					BlockingCallArguments bca =
						BlockingCallArguments.parseScriptLine(line);

					try {
						logExtendedInfo(logger, "Starting blocking call...");
						bca.logInfo();
						BlockingCall.doBlocking(conn, cjbs.blockingParams, bca,
								cjbs.config.getRepetitionCount(), hashedSqlSeq);
						logExtendedInfo(logger, "Blocking call returned");

						logExtendedInfo(logger, "Committing connection...");
						conn.commit();
						logExtendedInfo(logger, "Connection committed");

					} catch (Exception x2) {
						logExtendedException(logger, "Blocking call failed",
								x2);
					}

				}
				logExtendedInfo(logger, "Finished script");

			} catch (SQLException x) {
				logExtendedException(logger, "Unable to open JDBC connection",
						x);
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

} // class Main
