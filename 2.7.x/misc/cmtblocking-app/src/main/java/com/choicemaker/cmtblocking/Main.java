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
import java.util.Properties;
import java.util.Set;

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

	private static final String SOURCE = "Main";

	public static void main(String[] args) {

		logSystemProperties();

		CJBS cjbs = CJBS.parseArgs(SOURCE, args);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null) {

			Connection conn = null;
			try {

				logExtendedInfo(SOURCE, "Opening JDBC connection...");
				conn = cjbs.jdbcParams.getConnection();
				logExtendedInfo(SOURCE, "JDBC connection opened");

				logExtendedInfo(SOURCE,
						"Configuring Oracle remote debugging...");
				OracleRemoteDebugging.doDebugging(conn);
				logExtendedInfo(SOURCE, "Oracle remote debugging configured");

				logExtendedInfo(SOURCE, "Committing connection...");
				conn.commit();
				logExtendedInfo(SOURCE, "Connection committed");

				logExtendedInfo(SOURCE, "Starting script");
				while (cjbs.scriptIterator.hasNext()) {

					String line = (String) cjbs.scriptIterator.next();
					BlockingCallArguments bca =
						BlockingCallArguments.parseScriptLine(line);

					try {
						logExtendedInfo(SOURCE, "Starting blocking call...");
						bca.logInfo();
						BlockingCall.doBlocking(conn, cjbs.blockingParams, bca);
						logExtendedInfo(SOURCE, "Blocking call returned");

						logExtendedInfo(SOURCE, "Committing connection...");
						conn.commit();
						logExtendedInfo(SOURCE, "Connection committed");

					} catch (Exception x2) {
						logExtendedException(SOURCE, "Blocking call failed",
								x2);
					}

				}
				logExtendedInfo(SOURCE, "Finished script");

			} catch (SQLException x) {
				logExtendedException(SOURCE, "Unable to open JDBC connection",
						x);
			} finally {
				if (conn != null) {
					logExtendedInfo(SOURCE, "Closing JDBC connection...");
					closeConnection(SOURCE, conn);
					conn = null;
					logExtendedInfo(SOURCE, "JDBC connection closed");
				}
			}
		}

	} // main(String[])

	static void logSystemProperties() {
		Properties p = System.getProperties();
		Set<Object> keys = p.keySet();
		for (Object o : keys) {
			if (o instanceof String) {
				String key = (String) o;
				String value = p.getProperty(key);
				String msg = "System property '" + key + "'/'" + value + "'";
				logExtendedInfo(SOURCE, msg);
			}
		}
	}

	static void closeConnection(String source, Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logExtendedException(source, "Unable to close statement: ", e);
			}
		}
	}

} // class Main
