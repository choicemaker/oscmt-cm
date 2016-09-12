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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
			final Map<String, Integer> hashedSQL)
			throws SQLException, IOException {

		final String sql = blockingParams.getSQL();
		final String sqlId = getMd5Hash(sql);
		final String SQL_TAG = String.format("(SqlId:%s) ", sqlId);
		logInfo(SQL_TAG + "prepareCall( '" + sql + "' )");
		CallableStatement stmt = null;
		try {
			stmt = connection.prepareCall(sql);
			stmt.setFetchSize(100);
			for (int i = 0; i < repeatCount; i++) {
				blockAndRetrieveData(connection, stmt, args, hashedSQL, sqlId);
			}
		} finally {
			JdbcUtil.closeStatement(stmt);
			stmt = null;
		}

	}

	static void blockAndRetrieveData(final Connection connection,
			final CallableStatement stmt, final BlockingCallArguments args,
			final Map<String, Integer> hashedSQL, final String sqlId)
			throws SQLException {

		final int sqlSequence = getSequnceId(hashedSQL, sqlId);
		final String SEQ_TAG =
			String.format("(SqlId:%s,SequenceId:%d) ", sqlId, sqlSequence);
		logInfo(SEQ_TAG + "set arguments");
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

	public static int getSequnceId(Map<String, Integer> sequenceMap,
			String sequenceKey) {
		Integer sequenceId = sequenceMap.get(sequenceKey);
		if (sequenceId == null) {
			sequenceId = Integer.valueOf(0);
		}
		int retVal = 1 + sequenceId.intValue();
		sequenceMap.put(sequenceKey, Integer.valueOf(retVal));
		return retVal;
	}

	public static final String DIGEST_ALGO = "MD5";

	public static final String ENCODING = "UTF-8";

	public static String getMd5Hash(String sql) {
		String retVal = null;
		try {
			byte[] messageBytes = sql.getBytes(ENCODING);
			MessageDigest md = MessageDigest.getInstance(DIGEST_ALGO);
			byte[] digestBytes = md.digest(messageBytes);
			BigInteger i = new BigInteger(1, digestBytes);
			retVal = String.format("%1$032x", i);
		} catch (NumberFormatException | NoSuchAlgorithmException
				| UnsupportedEncodingException e) {
			throw new Error("Unexpected: " + e.toString());
		}
		assert retVal != null;
		return retVal;
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
