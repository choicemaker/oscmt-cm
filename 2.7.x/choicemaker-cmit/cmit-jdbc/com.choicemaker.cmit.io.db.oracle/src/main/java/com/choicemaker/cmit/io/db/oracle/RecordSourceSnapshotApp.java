package com.choicemaker.cmit.io.db.oracle;

import static com.choicemaker.e2.platform.InstallablePlatform.INSTALLABLE_PLATFORM;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.db.oracle.OracleRemoteDebugging;
import com.choicemaker.cm.io.db.oracle.OracleSerialMRPSource;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.SystemPropertyUtils;

public class RecordSourceSnapshotApp {

	private static final Logger logger = Logger
			.getLogger(RecordSourceSnapshotApp.class.getName());

	public static final String PN_PROPERTY_FILE = "propertyFile";

	public static final String DEFAULT_PROPERTY_FILE =
		"oracle_jdbc_test_local.properties";

	public static final String PN_POOL_NAME = "poolName";

	public static final String PN_JDBC_DATASOURCE_CLASS = "jdbcDatasourceClass";

	public static final String DEFAULT_JDBC_DATASOURCE_CLASS =
		"oracle.jdbc.pool.OracleDataSource";

	public static final String PN_JDBC_DRIVER = "jdbcDriver";

	public static final String DEFAULT_JDBC_DRIVE_CLASS =
		"oracle.jdbc.OracleDriver";

	public static final String PN_JDBC_URL = "jdbcUrl";

	public static final String DEFAULT_JDBC_URL =
		"jdbc:oracle:thin:@localhost:1521/XE";

	public static final String PN_JDBC_USER = "jdbcUser";

	public static final String PN_JDBC_PASSWORD = "jdbcPassword";

	public static final String PN_JDBC_POOL_INITIAL_SIZE =
		"jdbcPoolInitialSize";

	public static final String DEFAULT_JDBC_POOL_INITIAL_SIZE = "2";

	public static final String PN_JDBC_POOL_MAX_SIZE = "jdbcPoolMaxSize";

	public static final String DEFAULT_JDBC_POOL_MAX_SIZE = "20";

	public static final String PN_MODEL_NAME = "modelName";

	public static final String DEFAULT_MODEL_NAME =
		"com.choicemaker.cm.simplePersonMatching.Model1";

	public static final String PN_DATABASE_CONFIGURATION =
		"databaseConfiguration";

	public static final String DEFAULT_DATABASE_CONFIGURATION = "default";

	public static final String PN_SQL_RECORD_SELECTION = "sqlRecordSelection";

	public static final String DEFAULT_SQL_RECORD_SELECTION =
		"select record_id as id from Person";

	/*
	 * public static final String PN_JDBC_GROW_BLOCK = "jdbcPoolGrowBlock";
	 * 
	 * public static final String DEFAULT_JDBC_GROW_BLOCK = "1";
	 * 
	 * public static final String PN_JDBC_CREATE_WAIT_TIME =
	 * "jdbcCreateWaitTime";
	 * 
	 * / * * Milliseconds * / public static final String
	 * DEFAULT_JDBC_CREATE_WAIT_TIME = "1000";
	 */

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

	private static int getPropertyIntValue(Properties p, String key,
			String defaultValue) {
		assert p != null;
		assert key != null;
		assert defaultValue != null;
		String s = p.getProperty(key, defaultValue);
		int retVal;
		try {
			retVal = Integer.valueOf(s);
		} catch (NumberFormatException x) {
			String msg =
				"Invalid value ('" + s + "') for property '" + key + "': "
						+ x.toString();
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

	public static Properties loadProperties(Reader r) throws IOException {
		Properties p = new Properties();
		p.load(r);
		return p;
	}

	private static void logSecurityCredential(String key, String value) {
		value = createPasswordHint(value);
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

	private static void logProperty(String key, String value) {
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

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

		return retVal;
	}

	public static void main(String[] args) throws Exception {

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);
		// CMPlatform cmp = InstallablePlatform.getInstance();

		String propertyFileName =
			System.getProperty(PN_PROPERTY_FILE, DEFAULT_PROPERTY_FILE);
		FileReader fr = new FileReader(propertyFileName);
		Properties p = loadProperties(fr);
		DataSource ds = configureDatasource(p);

		String modelName = p.getProperty(PN_MODEL_NAME, DEFAULT_MODEL_NAME);
		logProperty(PN_MODEL_NAME, modelName);

		String databaseConfiguration =
			p.getProperty(PN_DATABASE_CONFIGURATION,
					DEFAULT_DATABASE_CONFIGURATION);
		logProperty(PN_DATABASE_CONFIGURATION, databaseConfiguration);

		String selection =
			p.getProperty(PN_SQL_RECORD_SELECTION, DEFAULT_SQL_RECORD_SELECTION);
		logProperty(PN_SQL_RECORD_SELECTION, selection);

		PMManager.loadModelPlugins();
		ImmutableProbabilityModel m1 =
			PMManager.getImmutableModelInstance(modelName);
		assert m1 != null : "null model";

		DbReaderParallel dbr =
			OracleSerialMRPSource.getDatabaseReader(m1, databaseConfiguration);
		final int noCursors = dbr.getNoCursors();

		// Get a database connection (and optionally configure debugging)
		Connection conn = ds.getConnection();
		OracleRemoteDebugging.doDebugging(conn);

		// Execute the stored procedure that retrieves records and marked
		// pairs
		CallableStatement stmt =
			OracleSerialMRPSource.prepareCmtTrainingAccessSnaphot(conn);
		OracleSerialMRPSource.executeCmtTrainingAccessSnaphot(stmt, selection,
				dbr);

		// Update the result sets representing records and marked pairs
		ResultSet markedPairs =
			(ResultSet) stmt
					.getObject(OracleSerialMRPSource.PARAM_IDX_PAIR_CURSOR);
		ResultSet cursorOfRecordCursors =
			(ResultSet) stmt
					.getObject(OracleSerialMRPSource.PARAM_IDX_RECORD_CURSOR_CURSOR);
		ResultSet[] recordCursors =
			OracleSerialMRPSource.createRecordCursors(cursorOfRecordCursors,
					noCursors);

		// Create the map of record ids to full records
		dbr.open(recordCursors);
		Map<?, ?> recordMap = OracleSerialMRPSource.createRecordMap(dbr);
		System.out.println("Number of records: " + recordMap.size());

		// Get the first currentPair
		int pairCount = -1;
		ImmutableMarkedRecordPair currentPair;
		do {
			++pairCount;
			currentPair =
				OracleSerialMRPSource.getNextPairInternal(recordMap,
						markedPairs);
		} while (currentPair != null);
		System.out.println("Number of pairs: " + pairCount);

	}

	// private DataSource dataSource;
	// private String jdbcUrl;
	// private String user;
	// private String password;
	// private String modelName;
	// private String databaseConfiguration;

}
