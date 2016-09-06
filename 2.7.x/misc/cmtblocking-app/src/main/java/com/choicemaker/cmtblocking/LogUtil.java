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
import java.util.Date;

/**
 *
 * @author   rphall 
 * @version   $Revision: 1.1.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class LogUtil {

	public static void logExtendedInfo(String extra, String msg) {
		Date date = new Date();
		System.out.println("[" + date + "] " + extra + ": " + msg);
	}

	public static void logExtendedException(
		String extra,
		String msg,
		Throwable x) {
		Date date = new Date();
		System.err.println("[" + date + "] " + extra + ": " + msg);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		x.printStackTrace(pw);
		System.err.println(sw.toString());
	}

} // class LogUtil
