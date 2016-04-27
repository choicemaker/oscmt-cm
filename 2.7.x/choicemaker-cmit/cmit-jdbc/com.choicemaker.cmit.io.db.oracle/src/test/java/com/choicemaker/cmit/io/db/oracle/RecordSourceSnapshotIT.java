package com.choicemaker.cmit.io.db.oracle;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
/*
		EmbeddedPlatform.install();
		CMExtension[] exts = CMPlatformUtils.getPluginExtensions(SPM_PLUGIN_ID);
		logger.info("DEBUG simple person matching extension count: " + exts.length);
		int count = PMManager.loadModelPlugins();
		if (count == 0) {
			logger.warning("No probability models loaded");
		}
*/
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
			retVal =
				password.substring(0, PASSWORD_HINT_LENGTH) + ELLIPSIS;
		}
		return retVal;
	}

//	private String jdbcUrl;
//	private String user;
//	private String password;
//	private DataSource dataSource;

	private static int getPropertyIntValue(Properties p, String key, String defaultValue) {
		assert p != null ;
		assert key != null ;
		assert defaultValue != null;
		String s = p.getProperty(key, defaultValue);
		int retVal;
		try {
			retVal = Integer.valueOf(s);
		} catch (NumberFormatException x) {
			String msg = "Invalid value ('" + s + "') for property '" + key + "': " + x.toString();
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		return retVal;
	}

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

	public static Properties loadProperties(Reader r) throws IOException  {
		Properties p = new Properties();
		p.load(r);
		return p;
	}

	private static void logProperty(String key, String value) {
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

	public static DataSource configureDatasource(Properties p) throws SQLException {
		validateProperties(p);
		PoolDataSource  retVal = PoolDataSourceFactory.getPoolDataSource();

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
		logProperty(key, value);
		retVal.setUser(value);

		key = PN_JDBC_PASSWORD;
		value = p.getProperty(key);
		assert value != null;
		logProperty(key,value);
		retVal.setPassword(value);

		key = PN_JDBC_POOL_INITIAL_SIZE;
		intValue = getPropertyIntValue(p,key,DEFAULT_JDBC_POOL_INITIAL_SIZE);
		retVal.setInitialPoolSize(intValue);

		key = PN_JDBC_POOL_MAX_SIZE;
		intValue = getPropertyIntValue(p,key,DEFAULT_JDBC_POOL_MAX_SIZE);
		retVal.setMaxPoolSize(intValue);

		return retVal;
	}

	@Before
	public void setUp() throws Exception {

		/*
		jdbcUrl = System.getProperty(PN_SQLSERVER_DB_JDBCURL);
		user = System.getProperty(PN_SQLSERVER_DB_USER);
		password = System.getProperty(PN_SQLSERVER_DB_PASSWORD);

		final String hint0 = createPasswordHint(password);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("JDBC parameters");
		pw.println("    jdbcUrl: " + jdbcUrl);
		pw.println("       user: " + user);
		pw.println("   password: " + hint0);
		pw.println();
		logger.info(sw.toString());

		Document d = null;
		try {
			d = ConfigurationUtils.readConfigurationFromResource(SQLSERVER_CONFIG_FILE);
		} catch (XmlConfException | JDOMException | IOException e) {
			logger.severe(e.toString());
			// fail(e.toString());
			throw e;
		}
		ConnectionPoolDataSourceXmlConf.init(d);

		int count = 0;
		for (Object o : DataSources.getDataSourceNames()) {
			String dsName = (String) o;
			logger.info("DataSource name: " + dsName);
			if (TARGET_NAME.equals(dsName)) {
				++count;
				dataSource = DataSources.getDataSource(dsName);
				assertTrue(dataSource instanceof ComboPooledDataSource);
				ComboPooledDataSource cpds = (ComboPooledDataSource) dataSource;

				// Update the data source parameters from the System properties
				if (user != null) {
					cpds.setUser(user);
				}
				if (password != null) {
					cpds.setPassword(password);
				}
				if (jdbcUrl != null) {
					cpds.setJdbcUrl(jdbcUrl);
				}

				String hint = createPasswordHint(cpds.getPassword());
				sw = new StringWriter();
				pw = new PrintWriter(sw);
				pw.println("DataSource parameters");
				pw.println("    jdbcUrl: " + cpds.getJdbcUrl());
				pw.println("       user: " + cpds.getUser());
				pw.println("   password: " + hint);
				logger.info(sw.toString());
			}
		}
		if (count == 0) {
			logger.warning("DataSource '" + TARGET_NAME + "' not found");
		} else if (count > 1) {
			String msg = "Multiple data sources named '" + TARGET_NAME + "'";
			logger.severe(msg);
			throw new Error(msg);
		} else {
			logger.info("Updated DataSource '" + TARGET_NAME + "'");
		}
*/
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testRecordSourceSnapshot() {
	}

}
