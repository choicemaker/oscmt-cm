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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class LogPartitionerCommandLine {

	// Required
	public static final String ARG_INPUT_PAIRS = "inputCounts";
	public static final String DESC_INPUT_PAIRS =
		"Input file of value-count pairs";

	public static final String ARG_OUTPUT_PAIRS = "outputPartitions";
	public static final String DESC_OUTPUT_PAIRS =
		"Output file for value-partition pairs";

	public static final String COMMAND_LINE = "LogPartitioner";

	private static final int EXPECTED_COUNT_PARAMS = 2;

	public static Options createOptions() {
		Options retVal = new Options();
		retVal.addOption(ARG_INPUT_PAIRS, true, DESC_INPUT_PAIRS);
		retVal.addOption(ARG_OUTPUT_PAIRS, true, DESC_OUTPUT_PAIRS);
		return retVal;
	}

	public static LogPartitionerParams parseCommandLine(String[] args)
			throws ParseException {
		if (args == null || args.length != EXPECTED_COUNT_PARAMS) {
			usage();
			//System.exit(LogPartitioner.ERROR_BAD_INPUT);
		}
		Options options = createOptions();
		CommandLineParser parser = new BasicParser();
		CommandLine cl = parser.parse(options, args);

		String inputPairsFileName = cl.getOptionValue(ARG_INPUT_PAIRS);
		String outputPairsFileName = cl.getOptionValue(ARG_OUTPUT_PAIRS);

		LogPartitionerParams retVal =
			new LogPartitionerParams(inputPairsFileName, outputPairsFileName);

		return retVal;
	}

	public static void usage() {
		Options options = createOptions();
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(COMMAND_LINE, options);
	}

	private LogPartitionerCommandLine() {
	}

}
