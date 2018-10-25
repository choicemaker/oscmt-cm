package com.choicemaker.cmit.io.db.sqlserver;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RecordSourceSnapshotIT {

	private static final Logger logger = Logger
			.getLogger(RecordSourceSnapshotIT.class.getName());

	public static final String PN_PROPERTY_FILE =
			"propertyFile";

	public static final String DEFAULT_PROPERTY_FILE =
			"oracle_jdbc_test_local.properties";

	public static final String PN_POOL_NAME =
		"poolName";

	public static final String PN_JDBC_DATASOURCE_CLASS =
			"jdbcDatasourceClass";

	public static final String DEFAULT_JDBC_DATASOURCE_CLASS =
			"oracle.jdbc.pool.OracleDataSource";

	public static final String PN_JDBC_DRIVER =
		"jdbcDriver";

	public static final String DEFAULT_JDBC_DRIVE_CLASS =
			"oracle.jdbc.OracleDriver";

	public static final String PN_JDBC_URL =
		"jdbcUrl";

	public static final String DEFAULT_JDBC_URL =
			"jdbc:oracle:thin:@localhost:1521/XE";

	public static final String PN_JDBC_USER =
			"jdbcUser";

	public static final String PN_JDBC_PASSWORD =
			"jdbcPassword";

	public static final String PN_JDBC_POOL_INITIAL_SIZE =
			"jdbcPoolInitialSize";

	public static final String DEFAULT_JDBC_POOL_INITIAL_SIZE =
			"2";

	public static final String PN_JDBC_POOL_MAX_SIZE =
			"jdbcPoolMaxSize";

	public static final String DEFAULT_JDBC_POOL_MAX_SIZE =
			"20";

	public static final String SIMPLE_PERSON_MODEL = "Model1";

	@Test
	public void testRecordSourceSnapshot() {
	}

}
