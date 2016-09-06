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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author   rphall 
 * @version   $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class Main {

	/** Prints the following usage message:<pre>
		* Usage: java com.choicemaker.cmtblocking.Main [<configFile>]
		*   where [<configFile>] is an optional properties file
		*   that specifies the name of a jdbcProperties file
		*   and the name of a blockingScript file
	 * </pre>
	 * @see Configuration#PN_JDBC_PROPERTIES
	 * @see Configuration#PN_SCRIPT_FILE
	 * @return a usage message
	 */
	public static String printUsage() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("Usage: java " + Main.class.getName() + " [<configFile>]");
		pw.println("  where [<configFile>] is an optional properties file");
		pw.println("  that specifies the name of a jdbcProperties file");
		pw.println("  and the name of a blockingScript file");
		return sw.toString();
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo("Main", msg);
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException("Main", msg, x);
	}

	public static void main(String[] args) {

		if (args == null || args.length > 1) {
			String msg = printUsage();
			throw new IllegalArgumentException(msg);
		}

		Configuration config = null;
		try {
			String configFileName = args.length == 1 ? args[0] : null;
			config = new Configuration(configFileName);
			config.logInfo();
		} catch (Exception x) {
			logException("Unable to construct configuration", x);
		}

		JdbcParams jdbcParams = null;
		if (config != null) {
			try {
				jdbcParams = config.getJdbcParams();
				jdbcParams.logInfo();
			} catch (Exception x) {
				logException("Unable to get JDBC parameters", x);
			}
		}

		BlockingParams blockingParams = null;
		if (config != null) {
			try {
				blockingParams = config.getBlockingParams();
				blockingParams.logInfo();
			} catch (Exception x) {
				logException("Unable to get Blocking parameters", x);
			}
		}

		Iterator scriptIterator = null;
		if (config != null) {
			try {
				BlockingScript script = config.getBlockingScript();
				script.logInfo();
				scriptIterator = script.getIterator();
			} catch (FileNotFoundException x) {
				logException("Unable to get blocking script", x);
			}
		}

		if (jdbcParams != null && blockingParams != null && scriptIterator != null) {

			Connection conn = null;
			try {

				logInfo("Opening JDBC connection...");
				conn = jdbcParams.getConnection();
				logInfo("JDBC connection opened");

				logInfo("Starting script");
				while (scriptIterator.hasNext()) {

					String line = (String) scriptIterator.next();
					BlockingCallArguments bca =
						BlockingCallArguments.parseScriptLine(line);

					try {
						logInfo("Starting blocking call...");
						bca.logInfo();
						BlockingCall.doBlocking(conn, blockingParams,bca);
						logInfo("Blocking call returned");
					} catch (Exception x2) {
						logException("Blocking call failed", x2);
					}

				}
				logInfo("Finished script");

			} catch (SQLException x) {
				logException("Unable to open JDBC connection", x);
			} finally {
				if (conn != null) {
					try {
						logInfo("Closing JDBC connection...");
						conn.close();
						logInfo("JDBC connection closed");
					} catch (SQLException x) {
						logException(x.getMessage(), x);
					} finally {
						conn = null;
					}
				}
			}
		}

	} // main(String[])

} // class Main
