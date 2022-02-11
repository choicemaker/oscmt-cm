/*
 * @(#)$RCSfile: BlockingCall.java,v $        $Revision: 1.5.2.2 $ $Date: 2010, 2016/04/08 16:14:18 $
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
import java.util.HashMap;
import java.util.Map;
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

	public static void doBlocking(final Connection connection,
			final BlockingParams blockingParams,
			final BlockingCallArguments args, final int repeatCount,
			Map<String, String> sqlQueryIds) throws SQLException, IOException {

		// Block and retrieve records using the stored procedure
		final String storedProcedureSQL =
			blockingParams.getStoredProcedureSQL();
		logInfo("prepareCall( '" + storedProcedureSQL + "' )");
		CallableStatement stmt = null;
		try {
			stmt = connection.prepareCall(storedProcedureSQL);
			stmt.setFetchSize(100);
			Map<String, Integer> sqlQuerySequences = new HashMap<>();
			for (int i = 0; i < repeatCount; i++) {
				blockAndRetrieveData(connection, stmt, args, sqlQueryIds,
						sqlQuerySequences);
			}
		} finally {
			JdbcUtil.closeStatement(stmt);
			stmt = null;
		}

	}

	static void blockAndRetrieveData(final Connection connection,
			final CallableStatement stmt, final BlockingCallArguments args,
			final Map<String, String> sqlQueryIds,
			final Map<String, Integer> sqlQuerySequences) throws SQLException {

		// Create an id and sequence for the sqlQuery and a tag for log entries
		final String sqlQuery = args.getQuery();
		final String sqlId = AppUtils.getRegisteredSqlId(sqlQueryIds, sqlQuery);
		final int seqId = AppUtils.getSequenceId(sqlQuerySequences, sqlId);
		final String SEQ_TAG = AppUtils.createSequenceTag(sqlId, seqId);

		logInfo(SEQ_TAG + "set arguments");
		args.logArguments(SEQ_TAG);
		stmt.setString(1, args.getBlockConfig());
		stmt.setString(2, args.getQuery());
		stmt.setString(3, args.getCondition1());
		stmt.setString(4, args.getCondition2());
		stmt.setString(5, args.getReadConfig());
		stmt.registerOutParameter(6, OracleTypes.CURSOR);

		logInfo(SEQ_TAG + "execute");
		stmt.execute();

		logInfo(SEQ_TAG + "retrieve outer");

		ResultSet outer = null;
		try {

			outer = (ResultSet) stmt.getObject(6);
			outer.setFetchSize(100);
			outer.next();
			ResultSetMetaData metaData = outer.getMetaData();
			int numberOfColumns = metaData.getColumnCount();

			ResultSet[] rs = new ResultSet[numberOfColumns];
			for (int i = 0; i < numberOfColumns; i++) {
				final int colNum = i + 1;
				logInfo(SEQ_TAG + "Retrieve column '" + colNum
						+ "' of first row of outer blocking result set");
				Object o = outer.getObject(colNum);
				if (o instanceof ResultSet) {
					logInfo(SEQ_TAG + "retrieve nested cursor: " + i);
					rs[i] = (ResultSet) outer.getObject(colNum);
					rs[i].setFetchSize(100);
				}
				logInfo(SEQ_TAG + "open dbr");
			}
			retrieveData(connection, rs, SEQ_TAG);

		} finally {
			JdbcUtil.closeResultSet(outer);
			outer = null;
		}
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(logger, msg);
	}

	static void retrieveData(final Connection connection, final ResultSet[] rs,
			final String SEQ_TAG) throws SQLException {
		int total = 0;
		int records = 0;
		for (int i = 0; i < rs.length; i++) {
			if (rs[i] != null) {
				logInfo(SEQ_TAG + "Counting rows from result set " + i);
				try {
					int count = 0;
					while (rs[i].next()) {
						++count;
						if (i == 0) {
							++records;
						}
					}
					logInfo(SEQ_TAG + "result set " + i + ": " + count);
					total += count;
				} finally {
					JdbcUtil.closeResultSet(rs[i]);
					rs[i] = null;
				}
			}
		}
		logInfo(SEQ_TAG + "Total records " + records);
		logInfo(SEQ_TAG + "Total rows from all records " + total);
	}

	private BlockingCall() {
	}

}
