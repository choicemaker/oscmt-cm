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
