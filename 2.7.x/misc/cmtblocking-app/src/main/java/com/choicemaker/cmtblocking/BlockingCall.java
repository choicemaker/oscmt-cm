/*
 * @(#)$RCSfile: BlockingCall.java,v $        $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;

/**
 *
 * @author rphall
 * @version $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingCall {

	public static final String SOURCE = "Blocking";

	private BlockingCall() {
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(SOURCE, msg);
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException(SOURCE, msg, x);
	}

	public static void doBlocking(Connection connection,
			BlockingParams blockingParams, BlockingCallArguments args)
			throws SQLException, IOException {

		final String sql = blockingParams.getSQL();
		logInfo("prepareCall( '" + sql + "' )");
		// FIXME see if eliminating redundant prepareCall invocations improves
		// performance
		CallableStatement stmt = null;
		try {
			stmt = connection.prepareCall(sql);
			stmt.setFetchSize(100);

			logInfo("set arguments");
			stmt.setString(1, args.getBlockConfig());
			stmt.setString(2, args.getQuery());
			stmt.setString(3, args.getCondition1());
			stmt.setString(4, args.getCondition2());
			stmt.setString(5, args.getReadConfig());
			stmt.registerOutParameter(6, OracleTypes.CURSOR);

			logInfo("execute");
			stmt.execute();

			logInfo("retrieve outer");

			ResultSet outer = null;
			try {

				outer = (ResultSet) stmt.getObject(6);
				outer.setFetchSize(100);
				outer.next();
				ResultSetMetaData metaData = outer.getMetaData();
				int numberOfColumns = metaData.getColumnCount();

				ResultSet[] rs = new ResultSet[numberOfColumns];
				for (int i = 0; i < numberOfColumns; i++) {
					int colNum = i + 1;
					logInfo("Retrieve column '" + colNum
							+ "' of first row of outer blocking result set");
					Object o = outer.getObject(colNum);
					if (o instanceof ResultSet) {
						logInfo("retrieve nested cursor: " + i);
						rs[i] = (ResultSet) outer.getObject(colNum);
						rs[i].setFetchSize(100);
					}
					logInfo("open dbr");
				}
				retrieveData(connection, rs);

			} finally {
				try {
					if (outer != null) {
						outer.close();
						outer = null;
					}
				} catch (SQLException x2) {
					logException("unable to close outer result set", x2);
				}
			}

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException x2) {
				logException("unable to close prepared SQL", x2);
			}
		}

	}

	static void retrieveData(Connection connection, ResultSet[] rs) {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i] != null) {
				logInfo("Retrieving data from result set " + i);
				// try {
				// FIXME retrieve the result set
				// } finally {
				try {
					if (rs[i] != null) {
						rs[i].close();
						rs[i] = null;
					}
				} catch (SQLException x2) {
					logException("unable to close result set " + i, x2);
				}
				// }
				logInfo("Retrieved data from result set " + i);
			}
		}
	}

}
