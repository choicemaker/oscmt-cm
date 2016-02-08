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

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.COMMA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT;
import com.choicemaker.util.SystemPropertyUtils;

public class LogPartitionerCommandLine {

	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	public static final String ARG_HELP = "help";
	public static final String DESC_HELP = "Help (print this message)";

	public static final String ARG_INPUT_FILE = "inputFile";
	public static final String DESC_INPUT_FILE =
		"[REQUIRED] Input file of value-count pairs";

	public static final String ARG_INPUT_FORMAT = "inputFormat";
	public static final String DESC_INPUT_FORMAT =
		"[REQUIRED] Input format: CSV (comma-separated values and counts) or "
				+ "ALT_LINES (values and counts on alternating lines)";

	public static final String ARG_INPUT_CSV_FIELD_SEP = "inputFieldSep";
	public static final String DESC_INPUT_CSV_FIELD_SEP =
		"[OPTIONAL] Input field separator: a single-charactor "
				+ "(instead of a comma) for separating CSV fields; "
				+ "if no separator is specified, a comma is the default";

	public static final String ARG_INPUT_LINE_SEP = "inputLineSep";
	public static final String DESC_INPUT_LINE_SEP =
		"[OPTIONAL] Output line separator: a charactor sequence for separating "
				+ "input lines; if no separator is specified, the default for the "
				+ "current system is used";

	public static final String ARG_OUTPUT_FILE = "outputFile";
	public static final String DESC_OUTPUT_FILE =
		"[REQUIRED] Output file for value-partition pairs";

	public static final String ARG_OUTPUT_FORMAT = "outputFormat";
	public static final String DESC_OUTPUT_FORMAT =
		"[REQUIRED] Output format: CSV (comma-separated values and counts) or "
				+ "ALT_LINES (values and counts on alternating lines)";

	public static final String ARG_OUTPUT_CSV_FIELD_SEP = "outputFieldSep";
	public static final String DESC_OUTPUT_CSV_FIELD_SEP =
		"[OPTIONAL] Output field separator: a single-charactor "
				+ "(instead of a comma) for separating CSV fields; "
				+ "if no separator is specified, a comma is the default";

	public static final String ARG_OUTPUT_LINE_SEP = "outputLineSep";
	public static final String DESC_OUTPUT_LINE_SEP =
		"[OPTIONAL] Output line separator: a charactor sequence for separating "
				+ "output lines; if no separator is specified, the default for the "
				+ "current system is used";

	public static final String ARG_PARTITION_COUNT = "numPartitions";
	public static final String DESC_PARTITION_COUNT =
		"[REQUIRED] Number of logarithmic partitions";

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

		opt =
			new Option(ARG_INPUT_CSV_FIELD_SEP, hasArg,
					DESC_INPUT_CSV_FIELD_SEP);
		opt.setRequired(false);
		retVal.addOption(opt);

		opt = new Option(ARG_INPUT_LINE_SEP, hasArg, DESC_INPUT_LINE_SEP);
		opt.setRequired(false);
		retVal.addOption(opt);

		opt = new Option(ARG_OUTPUT_FILE, hasArg, DESC_OUTPUT_FILE);
		opt.setRequired(false);
		retVal.addOption(opt);

		opt = new Option(ARG_OUTPUT_FORMAT, hasArg, DESC_OUTPUT_FORMAT);
		opt.setRequired(false);
		retVal.addOption(opt);

		opt =
			new Option(ARG_OUTPUT_CSV_FIELD_SEP, hasArg,
					DESC_OUTPUT_CSV_FIELD_SEP);
		opt.setRequired(false);
		retVal.addOption(opt);

		opt = new Option(ARG_OUTPUT_LINE_SEP, hasArg, DESC_OUTPUT_LINE_SEP);
		opt.setRequired(false);
		retVal.addOption(opt);

		opt = new Option(ARG_PARTITION_COUNT, hasArg, DESC_PARTITION_COUNT);
		opt.setRequired(false);
		retVal.addOption(opt);

		return retVal;
	}

	/**
	 * Parse the command line arguments into parameters for the
	 * LogPartitionerApp.
	 * 
	 * @param args
	 *            non-null array of command-line arguments
	 * @return LogPartitioner parameters, or null if help is requested or errors
	 *         are detected.
	 * @throws ParseException
	 * @throws IOException
	 */
	public static LogPartitionerParams parseCommandLine(String[] args)
			throws ParseException, IOException {

		LogPartitionerParams retVal;

		if (args == null || args.length == 0) {
			retVal = new LogPartitionerParams();
			assert retVal.isHelp();

		} else {

			Options options = createOptions();
			CommandLineParser parser = new BasicParser();
			CommandLine cl = parser.parse(options, args);

			List<String> errors = new ArrayList<>();

			boolean isHelp = cl.hasOption(ARG_HELP);

			// Required
			String inputFileName = cl.getOptionValue(ARG_INPUT_FILE);
			if (inputFileName != null) {
				inputFileName = inputFileName.trim();
			}
			if (inputFileName == null || inputFileName.isEmpty()) {
				errors.add(missingArgument(ARG_INPUT_FILE));
			}
			File f = new File(inputFileName);
			if (!f.exists() || !f.isFile()) {
				String msg =
					"Input file '" + f.getAbsolutePath()
							+ "' does not exist or is not a file";
				errors.add(msg);
			}

			LOG_PARTITIONER_FILE_FORMAT inputFormat = null;
			String sInputFormat = cl.getOptionValue(ARG_INPUT_FORMAT);
			if (sInputFormat != null) {
				sInputFormat = sInputFormat.trim().toUpperCase();
			}
			if (sInputFormat == null || sInputFormat.isEmpty()) {
				errors.add(missingArgument(ARG_INPUT_FORMAT));
			} else {
				try {
					inputFormat =
						LOG_PARTITIONER_FILE_FORMAT.valueOf(sInputFormat);
				} catch (IllegalArgumentException x) {
					errors.add(invalidArgument(ARG_INPUT_FORMAT, sInputFormat));
				}
			}

			String outputFileName = cl.getOptionValue(ARG_OUTPUT_FILE);
			if (outputFileName != null) {
				outputFileName = outputFileName.trim();
			}
			if (outputFileName == null || outputFileName.isEmpty()) {
				errors.add(missingArgument(ARG_OUTPUT_FILE));
			}
			f = new File(outputFileName);
			if (f.exists()) {
				String msg =
					"Output file '" + f.getAbsolutePath() + "' already exists";
				errors.add(msg);
			}

			LOG_PARTITIONER_FILE_FORMAT outputFormat = null;
			String sOutputFormat = cl.getOptionValue(ARG_OUTPUT_FORMAT);
			if (sOutputFormat != null) {
				sOutputFormat = sOutputFormat.trim().toUpperCase();
			}
			if (sOutputFormat == null || sOutputFormat.isEmpty()) {
				errors.add(missingArgument(ARG_OUTPUT_FORMAT));
			} else {
				try {
					outputFormat =
						LOG_PARTITIONER_FILE_FORMAT.valueOf(sOutputFormat);
				} catch (IllegalArgumentException x) {
					errors.add(invalidArgument(ARG_OUTPUT_FORMAT, sOutputFormat));
				}
			}

			int partitionCount = 0;
			String sPartitionCount = cl.getOptionValue(ARG_PARTITION_COUNT);
			if (sOutputFormat != null) {
				sOutputFormat = sOutputFormat.trim().toUpperCase();
			}
			if (sOutputFormat == null || sOutputFormat.isEmpty()) {
				errors.add(missingArgument(ARG_OUTPUT_FORMAT));
			} else {
				try {
					partitionCount = Integer.valueOf(sPartitionCount);
				} catch (NumberFormatException x) {
					errors.add(invalidArgument(ARG_PARTITION_COUNT,
							sPartitionCount));
				}
				if (partitionCount < 1) {
					errors.add("Non-positive partition number: "
							+ partitionCount);
				}
			}

			// Optional
			char inputFieldSep = COMMA;
			String sInputFieldSep = cl.getOptionValue(ARG_INPUT_CSV_FIELD_SEP);
			if (sInputFieldSep != null) {
				sInputFieldSep = sInputFieldSep.trim();
				if (!sInputFieldSep.isEmpty()) {
					inputFieldSep = sInputFieldSep.charAt(0);
				}
			}

			String inputLineSep = EOL;
			String sInputLineSep = cl.getOptionValue(ARG_INPUT_LINE_SEP);
			if (sInputLineSep != null) {
				if (!sInputLineSep.isEmpty()) {
					inputLineSep = sInputLineSep;
				}
			}

			char outputFieldSep = COMMA;
			String sOutputFieldSep =
				cl.getOptionValue(ARG_OUTPUT_CSV_FIELD_SEP);
			if (sOutputFieldSep != null) {
				sOutputFieldSep = sOutputFieldSep.trim();
				if (!sOutputFieldSep.isEmpty()) {
					outputFieldSep = sOutputFieldSep.charAt(0);
				}
			}

			String outputLineSep = EOL;
			String sOutputLineSep = cl.getOptionValue(ARG_OUTPUT_LINE_SEP);
			if (sOutputLineSep != null) {
				if (!sOutputLineSep.isEmpty()) {
					outputLineSep = sOutputLineSep;
				}
			}

			assert errors != null;
			if (errors.isEmpty()) {
				retVal =
					new LogPartitionerParams(isHelp, errors, inputFileName,
							inputFormat, inputFieldSep, inputLineSep,
							outputFileName, outputFormat, outputFieldSep,
							outputLineSep, partitionCount);
			} else {
				retVal = new LogPartitionerParams(isHelp, errors);
			}

		}

		return retVal;
	}

	public static String invalidArgument(String argName, String argValue) {
		String retVal =
			"Invalid value ('" + argValue + "') for the '" + argName
					+ "' argument";
		return retVal;
	}

	public static String missingArgument(String argName) {
		String retVal = "Missng the required '" + argName + "' argument";
		return retVal;
	}

	private LogPartitionerCommandLine() {
	}

}
