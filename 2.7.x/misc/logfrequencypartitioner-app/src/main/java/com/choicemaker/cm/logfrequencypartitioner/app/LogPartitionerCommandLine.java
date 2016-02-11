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
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.EOL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class LogPartitionerCommandLine {

	public static final String ARG_HELP = "help";
	public static final String DESC_HELP = "Help (print this message)";

	public static final String ARG_INPUT_FILE = "inputFile";
	public static final String DESC_INPUT_FILE =
		"[REQUIRED] Input file of value-count pairs";

	public static final String ARG_INPUT_FORMAT = "inputFormat";
	public static final String DESC_INPUT_FORMAT =
		"[REQUIRED] Input format: DELIMITED (comma-separated values and counts) or "
				+ "ALT_LINES (values and counts on alternating lines)";

	public static final String ARG_INPUT_FIELD_SEP = "inputFieldSep";
	public static final String DESC_INPUT_FIELD_SEP =
		"[OPTIONAL] Input field separator: a single-charactor "
				+ "(instead of a comma) for separating DELIMITED fields; "
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
		"[REQUIRED] Output format: DELIMITED (comma-separated values and counts) or "
				+ "ALT_LINES (values and counts on alternating lines)";

	public static final String ARG_OUTPUT_FIELD_SEP = "outputFieldSep";
	public static final String DESC_OUTPUT_FIELD_SEP =
		"[OPTIONAL] Output field separator: a single-charactor "
				+ "(instead of a comma) for separating DELIMITED fields; "
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

		opt = new Option(ARG_INPUT_FIELD_SEP, hasArg, DESC_INPUT_FIELD_SEP);
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

		opt = new Option(ARG_OUTPUT_FIELD_SEP, hasArg, DESC_OUTPUT_FIELD_SEP);
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
			} else {
				File f = new File(inputFileName);
				if (!f.exists() || !f.isFile()) {
					String msg =
						"Input file '" + f.getAbsolutePath()
								+ "' does not exist or is not a file";
					errors.add(msg);
				}
			}

			LogPartitionerFileFormat inputFormat = null;
			String sInputFormat = cl.getOptionValue(ARG_INPUT_FORMAT);
			if (sInputFormat != null) {
				sInputFormat = sInputFormat.trim().toUpperCase();
			}
			if (sInputFormat == null || sInputFormat.isEmpty()) {
				errors.add(missingArgument(ARG_INPUT_FORMAT));
			} else {
				try {
					inputFormat =
						LogPartitionerFileFormat.valueOf(sInputFormat);
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
			} else {
				File f = new File(outputFileName);
				if (f.exists()) {
					String msg =
						"Output file '" + f.getAbsolutePath()
								+ "' already exists";
					errors.add(msg);
				}
			}

			LogPartitionerFileFormat outputFormat = null;
			String sOutputFormat = cl.getOptionValue(ARG_OUTPUT_FORMAT);
			if (sOutputFormat != null) {
				sOutputFormat = sOutputFormat.trim().toUpperCase();
			}
			if (sOutputFormat == null || sOutputFormat.isEmpty()) {
				errors.add(missingArgument(ARG_OUTPUT_FORMAT));
			} else {
				try {
					outputFormat =
						LogPartitionerFileFormat.valueOf(sOutputFormat);
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
			String sInputFieldSep = cl.getOptionValue(ARG_INPUT_FIELD_SEP);
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
			String sOutputFieldSep = cl.getOptionValue(ARG_OUTPUT_FIELD_SEP);
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
			if (args.length == 1 && isHelp) {
				// Ignore errors if only help is requested
				List<String> empty = Collections.emptyList();
				retVal = new LogPartitionerParams(isHelp, empty);

			} else if (!errors.isEmpty()) {
				retVal = new LogPartitionerParams(isHelp, errors);

			} else {
				assert errors.isEmpty();
				retVal =
					new LogPartitionerParams(isHelp, errors, inputFileName,
							inputFormat, inputFieldSep, inputLineSep,
							outputFileName, outputFormat, outputFieldSep,
							outputLineSep, partitionCount);
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
		String retVal = "Missing the required '" + argName + "' argument";
		return retVal;
	}

	protected static final String OPTION_FLAG = "-";

	protected static final String MAGIC_DIVIDER = "__MAGIC_DIVIDER__";

	protected static String booleanArg(boolean isSet, String arg) {
		assert arg != null;
		assert !arg.contains(MAGIC_DIVIDER);
		String retVal = "";
		if (isSet) {
			retVal = OPTION_FLAG + arg + MAGIC_DIVIDER;
		}
		return retVal;
	}

	protected static String argWithQuotedValue(String arg, String value) {
		assert arg != null;
		assert !arg.contains(MAGIC_DIVIDER);
		String retVal = "";
		if (value != null) {
			if (value.contains(MAGIC_DIVIDER)) {
				throw new IllegalArgumentException(
						"value contains the magic divider ('" + MAGIC_DIVIDER
								+ "'): " + value);
			}
			retVal = OPTION_FLAG + arg + MAGIC_DIVIDER + value + MAGIC_DIVIDER;
		}
		return retVal;
	}

	public static String[] toCommandLine(LogPartitionerParams lpp) {
		if (lpp == null) {
			throw new IllegalArgumentException(
					"null log partitioner parameters");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(booleanArg(lpp.isHelp(), ARG_HELP));
		sb.append(argWithQuotedValue(ARG_INPUT_FILE, lpp.getInputFileName()));
		sb.append(argWithQuotedValue(ARG_INPUT_FORMAT, lpp.getInputFormat()
				.name()));
		sb.append(argWithQuotedValue(ARG_INPUT_FIELD_SEP,
				String.valueOf(lpp.getOutputFieldSeparator())));
		sb.append(argWithQuotedValue(ARG_INPUT_LINE_SEP,
				lpp.getInputLineSeparator()));
		sb.append(argWithQuotedValue(ARG_OUTPUT_FILE, lpp.getOutputFileName()));
		sb.append(argWithQuotedValue(ARG_OUTPUT_FORMAT, lpp.getOutputFormat()
				.name()));
		sb.append(argWithQuotedValue(ARG_OUTPUT_FIELD_SEP,
				String.valueOf(lpp.getOutputFieldSeparator())));
		sb.append(argWithQuotedValue(ARG_OUTPUT_LINE_SEP,
				lpp.getOutputLineSeparator()));
		sb.append(argWithQuotedValue(ARG_PARTITION_COUNT,
				String.valueOf(lpp.getPartitionCount())));

		String s = sb.toString();
		String[] retVal = s.split(MAGIC_DIVIDER);
		return retVal;
	}

	private LogPartitionerCommandLine() {
	}

}
