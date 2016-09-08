/*
 * @(#)$RCSfile: JdbcParams.java,v $        $Revision: 1.3.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author   rphall 
 * @version   $Revision: 1.3.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class JdbcParams {

	private static boolean INITIALIZED = false;
	
	public static final String PN_DRIVER_TYPE ="driverType";
	public static final String PN_SERVER_NAME ="serverName";
	public static final String PN_NETWORK_PROTOCOL ="networkProtocol";
	public static final String PN_DATABASE_NAME ="databaseName";
	public static final String PN_PORT_NUMBER ="portNumber";
	public static final String PN_USER ="user";
	public static final String PN_PASSWORD ="password";
	public static final String PN_CONNECTION_LIMIT ="connectionLimit";
	public static final String PN_AUTO_COMMIT ="autoCommit";

	private static final String DEFAULTS = "com/choicemaker/cmtblocking/defaultJDBC.properties";

	private static final Properties defaults = new Properties();
	
	private static void loadDefaultProperties() throws IOException {
		InputStream is = JdbcParams.class.getClassLoader().getResourceAsStream(DEFAULTS);
		defaults.load(is);
	}
	
	private static void registerJdbcDriver() throws SQLException {
		Driver driver = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				} // static
				
	private static void init() throws IOException, SQLException {
		if (!INITIALIZED) {
			loadDefaultProperties();
			registerJdbcDriver();
			INITIALIZED = true;
		}
	}

	private final File file;
	private final Properties properties;

	public JdbcParams(String jdbcFileName)
		throws FileNotFoundException, SQLException, IOException {
			
		if (!INITIALIZED) {
			init();
		}

		if (jdbcFileName != null && jdbcFileName.trim().length() > 0) {
			this.file = new File(jdbcFileName);
			this.properties = new Properties();
			this.properties.load(new FileInputStream(this.file));

		} else {
			this.file = null;
			this.properties = (Properties) defaults.clone();
		}

	} // ctor(String)

	private String getUrlString() {
		StringBuffer sb = new StringBuffer("jdbc:oracle:");
		sb.append(properties.get(PN_DRIVER_TYPE));
		sb.append(":@");
		sb.append(properties.get(PN_SERVER_NAME));
		sb.append(":");
		sb.append(properties.get(PN_PORT_NUMBER));
		sb.append(":");
		sb.append(properties.get(PN_DATABASE_NAME));
		String retVal = sb.toString();
		return retVal;
	}

	public Connection getConnection() throws SQLException {

		String strUrl = getUrlString();
		Connection retVal =
			DriverManager.getConnection(strUrl, this.properties);

		String strAutoCommit = this.properties.getProperty("autoCommit");
		boolean bAutoCommit = Boolean.valueOf(strAutoCommit).booleanValue();
		retVal.setAutoCommit(bAutoCommit);

		return retVal;
	} // getConnection()

	void logInfo() {
		Enumeration e = this.properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = this.properties.getProperty(key);
			logInfo("key/value: '" + key + "'/'" + value + "'");
		}
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo("JdbcParams", msg);
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException("JdbcParams", msg, x);
	}

}