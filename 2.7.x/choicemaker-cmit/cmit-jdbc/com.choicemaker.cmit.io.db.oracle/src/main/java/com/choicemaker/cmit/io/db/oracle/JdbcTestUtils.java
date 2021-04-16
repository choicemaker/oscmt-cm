package com.choicemaker.cmit.io.db.oracle;

import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_PASSWORD;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_USER;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Logger;

public class JdbcTestUtils {

	public static final Logger logger =
		Logger.getLogger(JdbcTestUtils.class.getName());

	public static String createPasswordHint(String password) {
		final int PASSWORD_HINT_LENGTH = 3;
		final String ELLIPSIS = "...";

		final String retVal;
		if (password == null) {
			retVal = "null";
		} else if (password.length() < PASSWORD_HINT_LENGTH) {
			retVal = ELLIPSIS;
		} else {
			retVal = password.substring(0, PASSWORD_HINT_LENGTH) + ELLIPSIS;
		}
		return retVal;
	}

	public static int getPropertyIntValue(Properties p, String key,
			String defaultValue) {
		assert p != null;
		assert key != null;
		assert defaultValue != null;
		String s = p.getProperty(key, defaultValue);
		int retVal;
		try {
			retVal = Integer.valueOf(s);
		} catch (NumberFormatException x) {
			String msg = "Invalid value ('" + s + "') for property '" + key
					+ "': " + x.toString();
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		return retVal;
	}

	public static Properties loadProperties(Reader r) throws IOException {
		Properties p = new Properties();
		p.load(r);
		return p;
	}

	public static void logProperty(String key, String value) {
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

	public static void logSecurityCredential(String key, String value) {
		value = createPasswordHint(value);
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

	/**
	 * Checks that the specified properties are not null or empty and that they
	 * contain values for user name and password.
	 */
	public static void validateProperties(Properties p) {
		if (p == null || p.isEmpty()) {
			throw new IllegalArgumentException("null or empty properties");
		}
		if (null == p.getProperty(PN_JDBC_USER)) {
			throw new IllegalArgumentException("null user name");
		}
		if (null == p.getProperty(PN_JDBC_PASSWORD)) {
			throw new IllegalArgumentException("null password");
		}
	}

	private JdbcTestUtils() {
	}

}
