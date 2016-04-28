package com.choicemaker.cmit.io.db.oracle;

public class OracleTestProperties {

	public static final String DEFAULT_JDBC_URL =
	"jdbc:oracle:thin:@localhost:1521/XE";

	public static final String PN_DATABASE_CONFIGURATION =
	"databaseConfiguration";

	public static final String DEFAULT_DATABASE_CONFIGURATION = "default";

	public static final String PN_MODEL_NAME = "modelName";

	public static final String DEFAULT_MODEL_NAME =
	"com.choicemaker.cm.simplePersonMatching.Model1";

	public static final String PN_PROPERTY_FILE = "propertyFile";

	public static final String DEFAULT_PROPERTY_FILE =
	"oracle_jdbc_test_local.properties";

	public static final String PN_SQL_RECORD_SELECTION = "sqlRecordSelection";

	public static final String DEFAULT_SQL_RECORD_SELECTION =
	"select record_id as ID from Person";

	private OracleTestProperties() {
	}

}
