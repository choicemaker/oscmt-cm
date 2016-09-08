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

/**
 * Creates and invokes a non-parameterized insert statement (with
 * embedded-literals) into tb_cmt_temp_ids.
 * 
 * @author rphall
 * @version $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class MainUnionLiteral {

	private static final String SOURCE = "MainUnionLiteral";

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
				
				logExtendedInfo(SOURCE, "Preparing connection...");
				UnionLiteral.prepareConnection(conn, cjbs.blockingParams);
				logExtendedInfo(SOURCE, "Connection prepared");

				logExtendedInfo(SOURCE, "Starting script");
				while (cjbs.scriptIterator.hasNext()) {

					String line = (String) cjbs.scriptIterator.next();
					UnionLiteral ul = new UnionLiteral(line);
					String sql = ul.computeSql();
					logExtendedInfo(SOURCE, "sql: " + sql);

					try {
						logExtendedInfo(SOURCE, "Starting blocking call...");
						UnionLiteral.doSql(conn, cjbs.blockingParams, sql);
						logExtendedInfo(SOURCE, "SQL call returned");
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
					Main.closeConnection(SOURCE, conn);
					conn = null;
					logExtendedInfo(SOURCE, "JDBC connection closed");
				}
			}
		}

	} // main(String[])

} // class Main
