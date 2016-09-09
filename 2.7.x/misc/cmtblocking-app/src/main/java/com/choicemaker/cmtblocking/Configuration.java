/*
 * @(#)$RCSfile: Configuration.java,v $        $Revision: 1.3.2.2 $ $Date: 2010/04/08 16:14:18 $
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
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author rphall
 * @version $Revision: 1.3.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class Configuration {

	private static final Logger logger =
		Logger.getLogger(Configuration.class.getName());

	public static final String PN_JDBC_PROPERTIES = "jdbcProperties";
	public static final String PN_BLOCKING_PROPERTIES = "blockingProperties";
	public static final String PN_BLOCKING_SCRIPT = "blockingScript";

	private String jdbcFileName;
	private String blockingFileName;
	private String scriptFileName;

	public Configuration(String configFileName) throws IOException {
		if (configFileName != null && configFileName.trim().length() > 0) {
			File configfile = new File(configFileName);
			Properties properties = new Properties();
			properties.load(new FileInputStream(configfile));
			this.jdbcFileName = properties.getProperty(PN_JDBC_PROPERTIES);
			this.blockingFileName =
				properties.getProperty(PN_BLOCKING_PROPERTIES);
			this.scriptFileName = properties.getProperty(PN_BLOCKING_SCRIPT);
		} else {
			this.jdbcFileName = null;
			this.blockingFileName = null;
			this.scriptFileName = null;
		}
	}

	public String getBlockingFileName() {
		return this.blockingFileName;
	}

	public BlockingParams getBlockingParams()
			throws FileNotFoundException, SQLException, IOException {
		return new BlockingParams(this.getBlockingFileName());
	}

	public BlockingScript getBlockingScript() {
		return new BlockingScript(this.getScriptFileName());
	}

	public String getJdbcFileName() {
		return this.jdbcFileName;
	}

	public JdbcParams getJdbcParams()
			throws FileNotFoundException, SQLException, IOException {
		return new JdbcParams(this.getJdbcFileName());
	}

	public String getScriptFileName() {
		return this.scriptFileName;
	}

	void logInfo() {
		logInfo("jdbcFileName = '" + this.jdbcFileName + "'");
		logInfo("blockingFileName = '" + this.blockingFileName + "'");
		logInfo("scriptFileName = '" + this.scriptFileName + "'");
	}

	private void logInfo(String msg) {
		LogUtil.logExtendedInfo(logger, msg);
	}

}
