package com.choicemaker.cmit.io.db.oracle;

import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_DATASOURCE_CLASS;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_POOL_INITIAL_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.DEFAULT_JDBC_POOL_MAX_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_DATASOURCE_CLASS;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_PASSWORD;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_POOL_INITIAL_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_POOL_MAX_SIZE;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_URL;
import static com.choicemaker.cm.io.db.oracle.OracleJdbcProperties.PN_JDBC_USER;
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
import com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource;
import com.choicemaker.cm.io.db.oracle.OracleRemoteDebugging;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.SystemPropertyUtils;

public class RecordSourceSnapshotApp {

	private static final Logger logger = Logger
			.getLogger(RecordSourceSnapshotApp.class.getName());

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
		value =
			p.getProperty(key, RecordSourceSnapshotProperties.DEFAULT_JDBC_URL);
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

	public static Properties loadProperties(Reader r) throws IOException {
		Properties p = new Properties();
		p.load(r);
		return p;
	}

	private static void logProperty(String key, String value) {
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

	private static void logSecurityCredential(String key, String value) {
		value = createPasswordHint(value);
		String msg = "Key '" + key + "': value '" + value + "'";
		logger.info(msg);
	}

	public static void main(String[] args) throws Exception {

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);
		// CMPlatform cmp = InstallablePlatform.getInstance();

		String propertyFileName =
			System.getProperty(RecordSourceSnapshotProperties.PN_PROPERTY_FILE,
					RecordSourceSnapshotProperties.DEFAULT_PROPERTY_FILE);
		FileReader fr = new FileReader(propertyFileName);
		Properties p = loadProperties(fr);
		DataSource ds = configureDatasource(p);

		String modelName =
			p.getProperty(RecordSourceSnapshotProperties.PN_MODEL_NAME,
					RecordSourceSnapshotProperties.DEFAULT_MODEL_NAME);
		logProperty(RecordSourceSnapshotProperties.PN_MODEL_NAME, modelName);

		String databaseConfiguration =
			p.getProperty(
					RecordSourceSnapshotProperties.PN_DATABASE_CONFIGURATION,
					RecordSourceSnapshotProperties.DEFAULT_DATABASE_CONFIGURATION);
		logProperty(RecordSourceSnapshotProperties.PN_DATABASE_CONFIGURATION,
				databaseConfiguration);

		String selection =
			p.getProperty(
					RecordSourceSnapshotProperties.PN_SQL_RECORD_SELECTION,
					RecordSourceSnapshotProperties.DEFAULT_SQL_RECORD_SELECTION);
		logProperty(RecordSourceSnapshotProperties.PN_SQL_RECORD_SELECTION,
				selection);

		PMManager.loadModelPlugins();
		ImmutableProbabilityModel m1 =
			PMManager.getImmutableModelInstance(modelName);
		assert m1 != null : "null model";

		DbReaderParallel dbr =
			OracleMarkedRecordPairSource.getDatabaseReader(m1,
					databaseConfiguration);
		final int noCursors = dbr.getNoCursors();

		// Get a database connection (and optionally configure debugging)
		Connection conn = ds.getConnection();
		OracleRemoteDebugging.doDebugging(conn);

		// Execute the stored procedure that retrieves records and marked
		// pairs
		CallableStatement stmt =
			OracleMarkedRecordPairSource.prepareCmtTrainingAccessSnaphot(conn);
		OracleMarkedRecordPairSource.executeCmtTrainingAccessSnaphot(stmt,
				selection, dbr);

		// Update the result sets representing records and marked pairs
		ResultSet markedPairs =
			(ResultSet) stmt
					.getObject(OracleMarkedRecordPairSource.PARAM_IDX_PAIR_CURSOR);
		ResultSet cursorOfRecordCursors =
			(ResultSet) stmt
					.getObject(OracleMarkedRecordPairSource.PARAM_IDX_RECORD_CURSOR_CURSOR);
		ResultSet[] recordCursors =
			OracleMarkedRecordPairSource.createRecordCursors(
					cursorOfRecordCursors, noCursors);

		// Create the map of record ids to full records
		dbr.open(recordCursors);
		Map<?, ?> recordMap = OracleMarkedRecordPairSource.createRecordMap(dbr);
		System.out.println("Number of records: " + recordMap.size());

		// Get the first currentPair
		int pairCount = -1;
		ImmutableMarkedRecordPair currentPair;
		do {
			++pairCount;
			currentPair =
				OracleMarkedRecordPairSource.getNextPairInternal(recordMap,
						markedPairs);
		} while (currentPair != null);
		System.out.println("Number of pairs: " + pairCount);

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

	// private DataSource dataSource;
	// private String jdbcUrl;
	// private String user;
	// private String password;
	// private String modelName;
	// private String databaseConfiguration;

}
