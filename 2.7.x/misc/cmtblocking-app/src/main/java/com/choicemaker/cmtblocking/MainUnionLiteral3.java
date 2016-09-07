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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Reads through a script and for each line in the script, creates and invokes a
 * non-parameterized insert statement (with embedded-literals) into
 * tb_cmt_temp_ids.
 * 
 * @author rphall
 * @version $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class MainUnionLiteral3 {

	private static final String SOURCE = "MainUnionLiteral";

	public static String printUsage() {
		return CJBS.printUsage(MainUnionLiteral3.class.getName());
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(SOURCE, msg);
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException(SOURCE, msg, x);
	}

	public static void main(String[] args) {

		CJBS cjbs = CJBS.parseArgs(SOURCE, args);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null) {

			Connection conn = null;
			try {

				logInfo("Opening JDBC connection...");
				conn = cjbs.jdbcParams.getConnection();
				logInfo("JDBC connection opened");

				logInfo("Starting script");
				while (cjbs.scriptIterator.hasNext()) {

					String line = (String) cjbs.scriptIterator.next();
					BlockingCallArguments bca =
						BlockingCallArguments.parseScriptLine(line);

					try {
						logInfo("Starting blocking call...");
						bca.logInfo();
						BlockingCall.doBlocking(conn, cjbs.blockingParams, bca);
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
