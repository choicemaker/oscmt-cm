/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;

public class LoggingUtils {

	private LoggingUtils() {
	}

	public static String buildDiagnostic(String msg, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ServerConfiguration serverConfig) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		if (msg != null && !msg.isEmpty()) {
			pw.println(msg);
		}
		pw.println("BatchJob: " + batchJob);
		pw.println("OabaParameters: " + oabaParams);
		pw.println("OabaSettings: " + oabaSettings);
		pw.println("ServerConfiguration: " + serverConfig);
		String retVal = sw.toString();
		return retVal;
	}

}
