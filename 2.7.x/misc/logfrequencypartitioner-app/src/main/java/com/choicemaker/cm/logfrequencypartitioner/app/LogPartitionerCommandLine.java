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

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.CM25_STANDARD;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.COMMA;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.DEFAULT_CSV_FIELD_SEPARATOR;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.PIPE;;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class LogPartitionerCommandLine {

	public static final String ARG_HELP = "help";
	public static final String DESC_HELP =
		"Help (print this message)";

	public static final String ARG_INPUT_FILE = "inputFile";
	public static final String DESC_INPUT_FILE =
		"Input file of value-count pairs";

	public static final String ARG_INPUT_FORMAT = "inputFormat";
	public static final String DESC_INPUT_FORMAT =
		"Input format: CSV (comma-separated values and counts) or "
			+ "ALT_LINES (values and counts on alternating lines)";
	
	public static final String ARG_INPUT_CSV_FIELD_SEP = "inputFieldSep";
	public static final String DESC_INPUT_CSV_FIELD_SEP =
		"Input field separator (optional): a single-charactor "
			+ "(instead of a comma) for separating CSV fields";
	
	public static final String ARG_OUTPUT_FILE = "outputFile";
	public static final String DESC_OUTPUT_FILE =
		"Output file for value-partition pairs";

	public static final String ARG_OUTPUT_FORMAT = "outputFormat";
	public static final String DESC_OUTPUT_FORMAT =
		"Output format: CSV (comma-separated values and counts) or "
			+ "ALT_LINES (values and counts on alternating lines)";
	
	public static final String ARG_OUTPUT_CSV_FIELD_SEP = "outputFieldSep";
	public static final String DESC_OUTPUT_CSV_FIELD_SEP =
		"Output field separator (optional): a single-charactor "
			+ "(instead of a comma) for separating CSV fields";
	
	public static final String ARG_PARTITION_COUNT = "numPartitions";
	public static final String DESC_PARTITION_COUNT =
		"Number of logarithmic partitions";
	
	public static final String COMMAND_LINE = "LogPartitioner";

	public static Options createOptions() {
		final boolean hasArg = true;
		Options retVal = new Options();
		Option opt;

		opt = new Option(ARG_HELP, !hasArg, DESC_HELP);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_INPUT_FILE, hasArg, DESC_INPUT_FILE);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_INPUT_FORMAT, hasArg, DESC_INPUT_FORMAT);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_INPUT_CSV_FIELD_SEP, hasArg, DESC_INPUT_CSV_FIELD_SEP);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_OUTPUT_FILE, hasArg, DESC_OUTPUT_FILE);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_OUTPUT_FORMAT, hasArg, DESC_OUTPUT_FORMAT);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_OUTPUT_CSV_FIELD_SEP, hasArg, DESC_OUTPUT_CSV_FIELD_SEP);
		opt.setRequired(false);
		retVal.addOption(opt);
		
		opt = new Option(ARG_PARTITION_COUNT, hasArg, DESC_PARTITION_COUNT);
		opt.setRequired(false);
		retVal.addOption(opt);

		return retVal;
	}

	/**
	 * Parse the command line arguments into parameters for the LogPartitionerApp.
	 * @param args non-null array of command-line arguments
	 * @return LogPartitioner parameters, or null if help is requested
	 * @throws ParseException
	 */
	public static LogPartitionerParams parseCommandLine(String[] args)
			throws ParseException {
		
		boolean isHelp = false;
		if (args == null || args.length == 0) {
			isHelp = true;
		}
		Options options = createOptions();
		CommandLineParser parser = new BasicParser();
		CommandLine cl = parser.parse(options, args);
		
		// Check for help
		if (cl.hasOption(ARG_HELP)) {
			isHelp = true;
		}
		
		LogPartitionerParams retVal;
		if (isHelp) {
			usage();
			retVal = null;
		} else {
			// Required
			String inputFileName = cl.getOptionValue(ARG_INPUT_FILE);

			String sInputFormat = cl.getOptionValue(ARG_INPUT_FORMAT);
			LOG_PARTITIONER_FILE_FORMAT inputFormat = LOG_PARTITIONER_FILE_FORMAT.valueOf(sInputFormat);

			String outputFileName = cl.getOptionValue(ARG_OUTPUT_FILE);

			String sOutputFormat = cl.getOptionValue(ARG_OUTPUT_FORMAT);
			LOG_PARTITIONER_FILE_FORMAT outputFormat = LOG_PARTITIONER_FILE_FORMAT.valueOf(sOutputFormat);

			String sPartitionCount = cl.getOptionValue(ARG_PARTITION_COUNT);	
			int partitionCount = Integer.valueOf(sPartitionCount);

			if (partitionCount < 1) {
				String msg = "Non-positive partition number: " + partitionCount;
				throw new IllegalArgumentException(msg);
			}
			
			// Optional
			String sInputFieldSep = cl.getOptionValue(ARG_INPUT_CSV_FIELD_SEP);
			char inputFieldSep = sInputFieldSep == null ? COMMA : sInputFieldSep.charAt(0);
			String sOutputFieldSep = cl.getOptionValue(ARG_OUTPUT_CSV_FIELD_SEP);
			char outputFieldSep = sOutputFieldSep == null ? COMMA : sOutputFieldSep.charAt(0);

			retVal =
				new LogPartitionerParams(inputFileName, inputFormat, inputFieldSep,
						outputFileName, outputFormat, outputFieldSep, partitionCount);
			
		}

		return retVal;
	}
	
	public static void missingArgument(String argName) {
		String msg = "Missng the required '" + argName + "' argument";
	}

	public static void usage() {
		Options options = createOptions();
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(COMMAND_LINE, options);
	}

	private LogPartitionerCommandLine() {
	}

}
