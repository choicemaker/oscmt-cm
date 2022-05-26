/*
 * Copyright (c) 2014, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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

	public static final String PN_REPETITION_COUNT = "repetitionCount";
	public static final String PN_JDBC_PROPERTIES = "jdbcProperties";
	public static final String PN_BLOCKING_PROPERTIES = "blockingProperties";
	public static final String PN_BLOCKING_SCRIPT = "blockingScript";
	public static final String PN_SQL_ID_MAP_FILE = "sqlIdMapFile";

	private final int repetitionCount;
	private String jdbcFileName;
	private String blockingFileName;
	private String scriptFileName;
	private String sqlIdMapFileName;

	public Configuration(String configFileName) throws IOException {
		if (configFileName != null && configFileName.trim().length() > 0) {
			File configfile = new File(configFileName);
			Properties properties = new Properties();
			properties.load(new FileInputStream(configfile));
			String sRepCount = properties.getProperty(PN_REPETITION_COUNT);
			int repCount;
			try {
				repCount = sRepCount == null ? 1 : Integer.parseInt(sRepCount);
				if (repCount < 0) {
					String msg = "Negative repetition count: " + repCount;
					logger.warning(msg);
				}
			} catch (NumberFormatException x) {
				String msg = "Invalid repetition count: '" + sRepCount + "'";
				logger.warning(msg);
				repCount = 0;
			}
			assert repCount >= 0;
			this.repetitionCount = repCount;
			if (repetitionCount == 0) {
				String msg = "Repetition count set to zero (0)";
				logger.warning(msg);;
			}
			this.jdbcFileName = properties.getProperty(PN_JDBC_PROPERTIES);
			this.blockingFileName =
				properties.getProperty(PN_BLOCKING_PROPERTIES);
			this.scriptFileName = properties.getProperty(PN_BLOCKING_SCRIPT);
			this.sqlIdMapFileName = properties.getProperty(PN_SQL_ID_MAP_FILE);
		} else {
			this.repetitionCount = 0;
			this.jdbcFileName = null;
			this.blockingFileName = null;
			this.scriptFileName = null;
			this.sqlIdMapFileName = null;
		}
	}

	public int getRepetitionCount() {
		return this.repetitionCount;
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

	public String getSqlIdMapFileName() {
		return sqlIdMapFileName;
	}

}
