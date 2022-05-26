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
