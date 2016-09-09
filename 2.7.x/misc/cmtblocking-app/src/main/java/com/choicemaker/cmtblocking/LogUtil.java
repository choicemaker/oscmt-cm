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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author rphall
 * @version $Revision: 1.1.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class LogUtil {

	public static void logExtendedInfo(String extra, String msg) {
		Date date = new Date();
		System.out.println("[" + date + "] " + extra + ": " + msg);
	}

	public static void logExtendedException(String extra, String msg,
			Throwable x) {
		Date date = new Date();
		System.err.println("[" + date + "] " + extra + ": " + msg);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		x.printStackTrace(pw);
		System.err.println(sw.toString());
	}

	static void logSystemProperties() {
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
			logExtendedInfo(Main.SOURCE, msg);
		}
	}

} // class LogUtil
