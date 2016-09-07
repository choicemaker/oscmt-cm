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

import static com.choicemaker.cmtblocking.LogUtil.*;

import java.sql.Connection;
import java.sql.SQLException;

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

		CJBS cjbs = CJBS.parseArgs(SOURCE, args);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null) {

			Connection conn = null;
			try {

				logExtendedInfo(SOURCE, "Opening JDBC connection...");
				conn = cjbs.jdbcParams.getConnection();
				logExtendedInfo(SOURCE, "JDBC connection opened");

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
					try {
						logExtendedInfo(SOURCE, "Closing JDBC connection...");
						conn.close();
						logExtendedInfo(SOURCE, "JDBC connection closed");
					} catch (SQLException x) {
						logExtendedException(SOURCE, x.getMessage(), x);
					} finally {
						conn = null;
					}
				}
			}
		}

	} // main(String[])

} // class Main
