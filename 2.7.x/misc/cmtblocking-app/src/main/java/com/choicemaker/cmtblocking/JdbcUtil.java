/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.LogUtil.logExtendedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class JdbcUtil {

	private static final Logger logger =
		Logger.getLogger(JdbcUtil.class.getName());

	// private static void logInfo(String msg) {
	// LogUtil.logExtendedInfo(SOURCE, msg);
	// }

	static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logExtendedException(logger, "Unable to close statement: ", e);
			}
		}
	}

	static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logException("Unable to close result set: ", e);
			}
		}
	}

	static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				logException("Unable to close statement: ", e);
			}
		}
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException(logger, msg, x);
	}

	private JdbcUtil() {
	}

}
