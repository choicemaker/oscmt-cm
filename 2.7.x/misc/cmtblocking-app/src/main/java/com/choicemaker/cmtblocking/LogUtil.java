/*
 * @(#)$RCSfile: LogUtil.java,v $        $Revision: 1.1.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author rphall
 * @version $Revision: 1.1.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class LogUtil {

	public static void logExtendedException(Logger logger, String msg,
			Throwable x) {
		logger.warning(msg + ": " + x.toString());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		x.printStackTrace(pw);
		logger.fine(sw.toString());
	}

	public static void logExtendedInfo(Logger logger, String msg) {
		logger.info(msg);
	}

	static void logSystemProperties(Logger logger) {
		Properties p = System.getProperties();
		List<String> sortedKeys = new ArrayList<>();
		Set<Object> keys = p.keySet();
		for (Object o : keys) {
			if (o instanceof String) {
				String key = (String) o;
				sortedKeys.add(key);
			}
		}
		Collections.sort(sortedKeys);
		for (String key : sortedKeys) {
			String value = p.getProperty(key);
			String msg = "System property '" + key + "'/'" + value + "'";
			logExtendedInfo(logger, msg);
		}
	}

} // class LogUtil
