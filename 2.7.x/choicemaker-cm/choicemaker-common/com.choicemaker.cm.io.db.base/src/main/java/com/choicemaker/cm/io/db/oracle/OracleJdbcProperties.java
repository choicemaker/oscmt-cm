package com.choicemaker.cm.io.db.oracle;

public interface OracleJdbcProperties {

	public static final String PN_JDBC_DATASOURCE_CLASS = "jdbcDatasourceClass";

	public static final String DEFAULT_JDBC_DATASOURCE_CLASS =
		"oracle.jdbc.pool.OracleDataSource";

	public static final String PN_JDBC_DRIVER = "jdbcDriver";

	public static final String DEFAULT_JDBC_DRIVE_CLASS =
		"oracle.jdbc.OracleDriver";

	public static final String PN_JDBC_POOL_INITIAL_SIZE =
		"jdbcPoolInitialSize";

	public static final String DEFAULT_JDBC_POOL_INITIAL_SIZE = "2";

	public static final String PN_JDBC_POOL_MAX_SIZE = "jdbcPoolMaxSize";

	public static final String DEFAULT_JDBC_POOL_MAX_SIZE = "20";

	public static final String PN_CONNECTION_AUTOCOMMIT = "jdbcAutoCommit";

	public static final String DEFAULT_CONNECTION_AUTOCOMMIT = "false";

	public static final String PN_POOL_NAME = "poolName";

	public static final String PN_JDBC_URL = "jdbcUrl";

	public static final String PN_JDBC_USER = "jdbcUser";

	public static final String PN_JDBC_PASSWORD = "jdbcPassword";

}
