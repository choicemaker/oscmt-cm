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
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.*;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

public class Oracle_DataSource {

	public static final Logger logger =
		Logger.getLogger(Oracle_DataSource.class.getName());

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

	private Oracle_DataSource() {
	}

}
