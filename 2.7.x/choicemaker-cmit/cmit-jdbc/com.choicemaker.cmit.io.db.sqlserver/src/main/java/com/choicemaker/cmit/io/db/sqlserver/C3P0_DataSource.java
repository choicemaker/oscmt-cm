/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cmit.io.db.sqlserver;

import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_POOL_INITIAL_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_POOL_MAX_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_DRIVER;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_PASSWORD;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_POOL_INITIAL_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_POOL_MAX_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_URL;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_USER;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.getPropertyIntValue;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.logProperty;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.logSecurityCredential;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.validateProperties;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.DEFAULT_JDBC_URL;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.io.db.base.DataSources;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Data source implemented with C3P0.
 * 
 * See www.mchange.com/projects/c3p0/
 */
public class C3P0_DataSource {

	/** Default limit to retry attempts */
	public static final int DEFAULT_ACQUIRE_RETRY_ATTEMPTS = 10;

	/** Default delay before retrying a connection (msec) */
	public static final int DEFAULT_AQUIRE_RETRY_DELAY = 1500;

	/** Default overall limit to connection checkout (msec) */
	public static final int DEFAULT_CHECKOUT_TIME =
		DEFAULT_ACQUIRE_RETRY_ATTEMPTS * DEFAULT_AQUIRE_RETRY_DELAY;

	public static final String DEFAULT_JDBC_DRIVER =
			"com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DEFAULT_JDBC_POOLINITIALSIZE = "1";
	public static final String DEFAULT_JDBC_POOLMAXSIZE = "1";
	public static final String DEFAULT_JDBC_POOLGROWBLOCK = "1";
	public static final String DEFAULT_JDBC_CREATEWAITTIME =
		String.valueOf(DEFAULT_CHECKOUT_TIME);
	public static final String DEFAULT_CONNECTION_AUTOCOMMIT = "false";

	public static final String PN_JDBC_CREATEWAITTIME = "jdbcCreateWaitTime";
	public static final String PN_JDBC_DRIVER = "jdbcDriver";
	public static final String PN_JDBC_POOLGROWBLOCK = "growBlock";
	public static final String PN_POOLNAME = "poolName";
	public static final String PN_MODELNAME = "modelName";
	public static final String PN_DATABASECONFIGURATION =
		"databaseConfiguration";
	public static final String PN_SQLSELECTION = "sqlSelection";

	private static Logger logger =
		Logger.getLogger(C3P0_DataSource.class.getName());

	public static DataSource configureDatasource(Properties p)
			throws SQLException {

		String key;
		String value;
		int intValue;

		ComboPooledDataSource retVal = null;
		try {
			validateProperties(p);
			retVal = new ComboPooledDataSource();

			key = PN_POOLNAME;
			final String name = p.getProperty(key);
			assert name != null;

			key = PN_JDBC_DRIVER;
			value = p.getProperty(key, DEFAULT_JDBC_URL);
			logProperty(key, value);
			retVal.setDriverClass(value);

			key = PN_JDBC_URL;
			value = p.getProperty(key, DEFAULT_JDBC_URL);
			logProperty(key, value);
			retVal.setJdbcUrl(value);

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
			intValue =
				getPropertyIntValue(p, key, DEFAULT_JDBC_POOL_INITIAL_SIZE);
			logProperty(key, value);
			retVal.setInitialPoolSize(intValue);
			retVal.setMinPoolSize(intValue);

			key = PN_JDBC_POOL_MAX_SIZE;
			intValue = getPropertyIntValue(p, key, DEFAULT_JDBC_POOL_MAX_SIZE);
			retVal.setMaxPoolSize(intValue);

			key = PN_JDBC_POOLGROWBLOCK;
			intValue = getPropertyIntValue(p, key, DEFAULT_JDBC_POOLGROWBLOCK);
			retVal.setAcquireIncrement(intValue);

			retVal.setAcquireRetryAttempts(DEFAULT_ACQUIRE_RETRY_ATTEMPTS);
			retVal.setAcquireRetryDelay(DEFAULT_AQUIRE_RETRY_DELAY);
			retVal.setCheckoutTimeout(DEFAULT_CHECKOUT_TIME);

			DataSources.addDataSource(name, retVal);
		} catch (Exception ex) {
			String msg =
				String.format("Error creating connection pool: %s", ex);
			logger.severe(msg);
			throw new SQLException(msg);
		}

		return retVal;
	}

}
