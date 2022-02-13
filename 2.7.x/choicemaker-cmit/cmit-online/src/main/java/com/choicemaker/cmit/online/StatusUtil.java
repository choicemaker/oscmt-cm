/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
/*
 * Created on Sep 8, 2009
 */
package com.choicemaker.cmit.online;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rphall
 * @version $Revision$ $Date$
 */
public class StatusUtil {

	private static final Logger logger = Logger.getLogger(StatusUtil.class
			.getPackage().getName());

	private static boolean displayDetails = getLogger().isLoggable(Level.FINE);
	private static boolean isLoggingConfigured =
		getLogger().getHandlers() != null
				&& getLogger().getHandlers().length > 0;

	public static Logger getLogger() {
		return logger;
	}

	public static boolean isDisplayDetails() {
		return displayDetails;
	}

	public static boolean isLoggingConfigured() {
		return isLoggingConfigured;
	}

	public static void printDebug(String s) {
		printDebug(s, null);
	}

	public static void printDebug(String s, Throwable t) {
		// Use logging if configured
		if (isLoggingConfigured()) {
			if (t != null) {
				getLogger().fine(s + ": " + t.toString());
			} else {
				getLogger().fine(s);
			}

			// No logging configured, use Standard Error
		} else {
			String msg = s;
			if (t != null) {
				msg = s + ": " + t.toString();
			}
			System.err.println(msg);
		}
	}

	private StatusUtil() {
	}

	public static void reportStatistics(QueryStatistics statistics) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		reportStatistics(pw, statistics);
		String report = sw.toString();
		StatusUtil.getLogger().info(report);
		System.out.println(report);
	}

	public static void reportStatistics(PrintWriter pw,
			QueryStatistics statistics) {
		if (pw == null || statistics == null) {
			throw new IllegalArgumentException("null constructor argument");
		}
		pw.println("Total comparisons: " + statistics.getCountComparisons());
		pw.println("Both comparisions successful: "
				+ statistics.getCountBothSuccessful());
		pw.println("Both comparisons successful and same match results: "
				+ statistics.getCountBothSuccessfulAndSameMatches());
		pw.println("Both comparisons unsuccessful: "
				+ statistics.getCountBothUnsuccessful());
		pw.println("Both comparisons unsuccessful and same errors: "
				+ statistics.getCountBothUnsuccessfulAndSameErrors());
	}
}
