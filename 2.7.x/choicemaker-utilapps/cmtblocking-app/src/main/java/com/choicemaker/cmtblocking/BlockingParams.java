/*
 * @(#)$RCSfile: BlockingParams.java,v $        $Revision: 1.3.2.2 $ $Date: 2010, 2016/04/08 16:14:18 $
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
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author rphall
 * @version $Revision: 1.3.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingParams {

	private static final Logger logger =
		Logger.getLogger(BlockingParams.class.getName());

	private static boolean INITIALIZED = false;

	public static final String PN_SCHEMA = "schema";
	public static final String PN_PACKAGE = "package";
	public static final String PN_PROCEDURE = "procedure";

	private static final String DEFAULTS =
		"com/choicemaker/cmtblocking/defaultBlocking.properties";

	private static final Properties defaults = new Properties();

	private static void init() throws IOException, SQLException {
		if (!INITIALIZED) {
			loadDefaultProperties();
			INITIALIZED = true;
		}
	}

	private static void loadDefaultProperties() throws IOException {
		InputStream is =
			BlockingParams.class.getClassLoader().getResourceAsStream(DEFAULTS);
		defaults.load(is);
	}

	private final File file;
	private final Properties properties;

	public BlockingParams(String blockingFileName)
			throws FileNotFoundException, SQLException, IOException {

		if (!INITIALIZED) {
			init();
		}

		if (blockingFileName != null && blockingFileName.trim().length() > 0) {
			this.file = new File(blockingFileName);
			this.properties = new Properties();
			this.properties.load(new FileInputStream(this.file));

		} else {
			this.file = null;
			this.properties = (Properties) defaults.clone();
		}

	} // ctor(String)

	public int getNumberOfSqlArguments() {
		int retVal = 0;
		String sql = getStoredProcedureSQL();
		CharacterIterator iter = new StringCharacterIterator(sql);
		for (char c = iter.first(); c != CharacterIterator.DONE; c =
			iter.next()) {
			if (c == '?') {
				++retVal;
			}
		}
		return retVal;
	}

	public String getStoredProcedureSQL() {
		StringBuffer sb = new StringBuffer("call ");
		String s = properties.getProperty(PN_SCHEMA);
		if (s != null && s.trim().length() > 0) {
			sb.append(s);
			sb.append(".");
		}
		sb.append(properties.getProperty(PN_PACKAGE));
		sb.append(".");
		sb.append(properties.getProperty(PN_PROCEDURE));
		sb.append("(?, ?, ?, ?, ?, ?)");
		String retVal = sb.toString();
		return retVal;
	}

	void logInfo() {
		Enumeration<?> e = this.properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = this.properties.getProperty(key);
			logInfo("key/value: '" + key + "'/'" + value + "'");
		}
		logInfo("SQL: '" + getStoredProcedureSQL() + "'");
		logInfo("Number Of SQL Arguments: '" + getNumberOfSqlArguments() + "'");
	}

	private void logInfo(String msg) {
		LogUtil.logExtendedInfo(logger, msg);
	}

}
