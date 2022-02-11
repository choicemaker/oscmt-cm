/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class DebugUtils {

	public static final int MAX_STACK_TRACE_DEPTH = 7;

	private DebugUtils() {
	}

	public static String printStackTrace(String msg) {
		return printStackTrace(msg, MAX_STACK_TRACE_DEPTH);
	}

	public static String printStackTrace(final String msg, final int maxDepth) {
		Precondition.assertBoolean("non-positive max stack depth: " + maxDepth,
				maxDepth > 0);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Throwable t = new RuntimeException(msg);
		pw.println(msg);
		t.printStackTrace(pw);

		String s = sw.toString();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);

		// Full stack is the fall-back value
		String retVal = s;

		// Try to truncate the stack and message to maxDepth
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			String line = br.readLine();
			int lineCount = 0;
			while (line != null && lineCount < maxDepth) {
				pw.println(line);
				++lineCount;
				line = br.readLine();
			}
			retVal = sw.toString();
		} catch (IOException x) {
			assert retVal.equals(s);
		}

		return retVal;
	}

	public static String printStackTrace0(String msg, final int count) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Exception x = new Exception(msg);
		x.printStackTrace(pw);
	
		final String s0 = sw.toString();
		StringReader sr = new StringReader(s0);
		LineNumberReader lr = new LineNumberReader(sr);
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		int c = 0;
		while (c < count) {
			String s;
			try {
				s = lr.readLine();
				pw.println(s);
			} catch (IOException e) {
				pw.println("Stack trace aborted...");
			}
		}
		pw.println("Stack trace terminated");
		
		String retVal = sw.toString();
		return retVal;
	}

}
