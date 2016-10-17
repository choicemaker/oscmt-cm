package com.choicemaker.cmit.io.db.oracle;

import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_CONNECTION_AUTOCOMMIT;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_DATASOURCE_CLASS;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_POOL_INITIAL_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_POOL_MAX_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_CONNECTION_AUTOCOMMIT;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_DATASOURCE_CLASS;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_PASSWORD;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_POOL_INITIAL_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_POOL_MAX_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_URL;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_USER;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.DEFAULT_JDBC_URL;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

public class OracleTestUtils {

	public static final Logger logger =
		Logger.getLogger(OracleTestUtils.class.getName());

	public static DataSource configureDatasource(Properties p)
			throws SQLException {
		validateProperties(p);
		PoolDataSource retVal = PoolDataSourceFactory.getPoolDataSource();

		String key;
		String value;
		int intValue;

		key = PN_JDBC_DATASOURCE_CLASS;
		value = p.getProperty(key, DEFAULT_JDBC_DATASOURCE_CLASS);
		logProperty(key, value);
		retVal.setConnectionFactoryClassName(value);

		key = PN_JDBC_URL;
		value = p.getProperty(key, DEFAULT_JDBC_URL);
		logProperty(key, value);
		retVal.setURL(value);

		key = PN_JDBC_USER;
		value = p.getProperty(key);
		assert value != null;
		logSecurityCredential(key, value);
		retVal.setUser(value);

		key = PN_JDBC_PASSWORD;
		value = p.getProperty(key);
		assert value != null;
		logSecurityCredential(key, value);
		retVal.setPassword(value);

		key = PN_JDBC_POOL_INITIAL_SIZE;
		intValue = getPropertyIntValue(p, key, DEFAULT_JDBC_POOL_INITIAL_SIZE);
		retVal.setInitialPoolSize(intValue);

		key = PN_JDBC_POOL_MAX_SIZE;
		intValue = getPropertyIntValue(p, key, DEFAULT_JDBC_POOL_MAX_SIZE);
		retVal.setMaxPoolSize(intValue);
		
		key = PN_CONNECTION_AUTOCOMMIT;
		value = p.getProperty(key, DEFAULT_CONNECTION_AUTOCOMMIT);
		retVal.setConnectionProperty(OracleConnection.CONNECTION_PROPERTY_AUTOCOMMIT, value);

		return retVal;
	}

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

	private OracleTestUtils() {
	}

}
