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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Creates and invokes a non-parameterized insert statement (with
 * embedded-literals) into tb_cmt_temp_ids.
 * 
 * @author rphall
 * @version $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class MainUnionParameterized {

	private static final Logger logger =
		Logger.getLogger(MainUnionParameterized.class.getName());

	private static final String SOURCE =
		MainUnionParameterized.class.getSimpleName();

	public static void main(String[] args) {

		LogUtil.logSystemProperties(logger);

		CJBS cjbs = CJBS.parseArgs(SOURCE, logger, args);
		assert cjbs != null;

		if (cjbs.jdbcParams != null && cjbs.blockingParams != null
				&& cjbs.scriptIterator != null) {

			Connection conn = null;
			try {

				logExtendedInfo(logger, "Opening JDBC connection...");
				conn = cjbs.jdbcParams.getConnection();
				logExtendedInfo(logger, "JDBC connection opened");

				logExtendedInfo(logger, "Preparing connection...");
				UnionLiteral.prepareConnection(conn, cjbs.blockingParams);
				logExtendedInfo(logger, "Connection prepared");

				logExtendedInfo(logger, "Starting script");
				while (cjbs.scriptIterator.hasNext()) {

					String line = cjbs.scriptIterator.next();
					UnionLiteral ul = new UnionLiteral(line);
					String sql = ul.computeSql();
					logExtendedInfo(logger, "sql: " + sql);

					try {
						logExtendedInfo(logger, "Starting blocking call...");
						UnionLiteral.doSql(conn, cjbs.blockingParams, sql);
						logExtendedInfo(logger, "SQL call returned");
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
