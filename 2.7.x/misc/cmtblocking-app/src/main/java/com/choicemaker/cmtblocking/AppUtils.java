package com.choicemaker.cmtblocking;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AppUtils {

	public static final String DIGEST_ALGO = "MD5";
	public static final String ENCODING = "UTF-8";

	public static String computeMd5Hash(String s) {
		String retVal = null;
		try {
			byte[] messageBytes = s.getBytes(ENCODING);
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

	public static String createSequenceTag(
			String sqlId, int sqlSequence) {
		String retVal =
			String.format("(SqlId:%s,SequenceId:%d) ", sqlId, sqlSequence);
		return retVal;
	}

	public static String createSqlIdTag(String sqlId) {
		String retVal = String.format("(SqlId:%s) ", sqlId);
		return retVal;
	}

	public static int getSequenceId(Map<String, Integer> sequenceMap,
			String sequenceKey) {
		Integer sequenceId = sequenceMap.get(sequenceKey);
		if (sequenceId == null) {
			sequenceId = Integer.valueOf(0);
		}
		int retVal = 1 + sequenceId.intValue();
		sequenceMap.put(sequenceKey, Integer.valueOf(retVal));
		return retVal;
	}

	private AppUtils() {
	}

	public static String labelValue(String label, String value) {
		String retVal = String.format("%s = '%s'", label, value);
		return retVal;
	}

	public static String getRegisteredSqlId(Map<String, String> hashedSqlQueries,
			String sqlQuery) {
		String retVal = hashedSqlQueries.get(sqlQuery);
		if (retVal == null) {
			retVal = computeMd5Hash(sqlQuery);
			hashedSqlQueries.put(sqlQuery, retVal);
		}
		assert retVal != null;
		assert retVal.equals(computeMd5Hash(sqlQuery));
		assert hashedSqlQueries.keySet().contains(sqlQuery);
		assert hashedSqlQueries.values().contains(retVal);
		return retVal;
	}

}
