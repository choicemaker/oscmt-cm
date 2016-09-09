package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.LogUtil.logExtendedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtil {

	public static final String SOURCE = "JdbcUtil";

//	private static void logInfo(String msg) {
//		LogUtil.logExtendedInfo(SOURCE, msg);
//	}

	static void closeConnection(String source, Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logExtendedException(source, "Unable to close statement: ", e);
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
		LogUtil.logExtendedException(SOURCE, msg, x);
	}

	private JdbcUtil() {
	}

}
