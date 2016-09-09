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
import java.util.logging.Logger;

import oracle.jdbc.OracleTypes;

/**
 *
 * @author rphall
 * @version $Revision: 1.5.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingCall {

	private static final Logger logger =
		Logger.getLogger(BlockingCall.class.getName());

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
				JdbcUtil.closeResultSet(outer);
				outer = null;
			}

		} finally {
			JdbcUtil.closeStatement(stmt);
			stmt = null;
		}

	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(logger, msg);
	}

	static void retrieveData(Connection connection, ResultSet[] rs)
			throws SQLException {
		int total = 0;
		for (int i = 0; i < rs.length; i++) {
			if (rs[i] != null) {
				logInfo("Counting rows from result set " + i);
				try {
					int count = 0;
					while (rs[i].next()) {
						++count;
					}
					logInfo("result set " + i + ": " + count);
					total += count;
				} finally {
					JdbcUtil.closeResultSet(rs[i]);
					rs[i] = null;
				}
			}
		}
		logInfo("Total rows from all result sets " + total);
	}

	private BlockingCall() {
	}

}
