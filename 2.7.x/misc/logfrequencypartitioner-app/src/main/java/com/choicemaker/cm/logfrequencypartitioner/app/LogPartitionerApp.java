/*******************************************************************************
 * Copyright (c) 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.logfrequencypartitioner.app;

import java.util.Date;

import org.apache.commons.cli.ParseException;

/**
 * Application that ...
 * 
 * @param args
 */
public class LogPartitionerApp {

	public static final int STATUS_OK = 0;
	public static final int ERROR_BAD_INPUT = 1;
	
	/**
	 * Entry point for the application. Command line parameters:
	 * <ul>
	 * <li>-inputPairs <input pair-wise results file (*.txt)> [Required]</li>
	 * <li>-outputPairs <output pair-wise results file (*.txt)> [Required]</li>
	 * </ul>
	 * The input and output file formats are documented in the package overview.
	 * 
	 * @param args
	 *            a String array of exactly 8 elements, corresponding to the
	 *            parameters listed above.
	 * @throws ParseException
	 *             if the command line can not be parsed
	 */
	public static void main(String[] args) throws ParseException {

		final LogPartitionerParams appParms =
			LogPartitionerCommandLine.parseCommandLine(args);
		final LogPartitionerApp app = new LogPartitionerApp(appParms);
		app.createPartitions();
		System.exit(STATUS_OK);
	}

	private final LogPartitionerParams appParams;

	public LogPartitionerApp(LogPartitionerParams appParams) {
		if (appParams == null) {
			throw new IllegalArgumentException("null application parameters");
		}
		this.appParams = appParams;
	}

	public LogPartitionerParams getAppParams() {
		return appParams;
	}

	public void createPartitions() {

		final long start = new Date().getTime();
		try {


		} catch (RuntimeException x) {
			final long duration = new Date().getTime() - start;
			throw x;
		}

	}

}
