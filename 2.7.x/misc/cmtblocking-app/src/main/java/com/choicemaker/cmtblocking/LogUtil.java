/*
 * @(#)$RCSfile: LogUtil.java,v $        $Revision: 1.1.2.2 $ $Date: 2010, 2017/04/08 16:14:18 $
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
import java.util.logging.Logger;

import com.choicemaker.util.SystemPropertyUtils;

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
		SystemPropertyUtils.logSystemProperties(logger);
	}

} // class LogUtil
