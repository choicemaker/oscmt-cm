/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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
