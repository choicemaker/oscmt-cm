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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT;
import com.choicemaker.util.SystemPropertyUtils;

public class LogPartitionerCommandLine {

	/** For testing */
	public static class ParseResults {
		boolean isHelp;
		String inputFileName;
		LOG_PARTITIONER_FILE_FORMAT inputFormat;
		char inputFieldSep;
		String inputLineSep;
		String outputFileName;
		LOG_PARTITIONER_FILE_FORMAT outputFormat;
		char outputFieldSep;
		String outputLineSep;
		int partitionCount;
		String errors;
	}

	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	public static final String ARG_HELP = "help";
	public static final String DESC_HELP = "Help (print this message)";

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
				+ "(instead of a comma) for separating CSV fields; "
				+ "if no separator is specified, a comma is the default";

	public static final String ARG_INPUT_LINE_SEP = "inputLineSep";
	public static final String DESC_INPUT_LINE_SEP =
		"Output line separator (optional): a charactor sequence for separating "
				+ "input lines; if no separator is specified, the default for the "
				+ "current system is used";

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
				+ "(instead of a comma) for separating CSV fields; "
				+ "if no separator is specified, a comma is the default";

	public static final String ARG_OUTPUT_LINE_SEP = "outputLineSep";
	public static final String DESC_OUTPUT_LINE_SEP =
		"Output line separator (optional): a charactor sequence for separating "
				+ "output lines; if no separator is specified, the default for the "
				+ "current system is used";

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

		opt =
			new Option(ARG_INPUT_CSV_FIELD_SEP, hasArg,
					DESC_INPUT_CSV_FIELD_SEP);
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

		opt = new Option(ARG_PARTITION_COUNT, hasArg, DESC_PARTITION_COUNT);
		opt.setRequired(false);
		retVal.addOption(opt);

		return retVal;
	}

	/**
	 * Parse the command line arguments into parameters for the
	 * LogPartitionerApp.
	 * 
	 * @param console
	 *            a non-null print writer on which to display error and usage
	 *            messages.
	 * @param args
	 *            non-null array of command-line arguments
	 * @return LogPartitioner parameters, or null if help is requested or errors
	 *         are detected.
	 * @throws ParseException
	 * @throws IOException
	 */
	public static LogPartitionerParams parseCommandLine(PrintWriter console,
			String[] args) throws ParseException, IOException {

		ParseResults parsed = parseCommandLineInternal(console, args);

		LogPartitionerParams retVal = null;
		if (parsed.isHelp) {
			usage(console);
			assert retVal == null;

		} else {
			if (!parsed.errors.isEmpty()) {
				printErrors(console, parsed.errors);
				console.println();
				usage(console);
				console.println();
				assert retVal == null;

			} else {
				retVal =
					new LogPartitionerParams(parsed.inputFileName,
							parsed.inputFormat, parsed.inputFieldSep,
							parsed.inputLineSep, parsed.outputFileName,
							parsed.outputFormat, parsed.outputFieldSep,
							parsed.outputLineSep, parsed.partitionCount);
			}
		}

		return retVal;
	}

	/** For testing */
	public static ParseResults parseCommandLineInternal(PrintWriter console,
			String[] args) throws ParseException, IOException {

		ParseResults retVal = new ParseResults();

		if (args == null || args.length == 0) {
			retVal.isHelp = true;

		} else {

			Options options = createOptions();
			CommandLineParser parser = new BasicParser();
			CommandLine cl = parser.parse(options, args);

			// Check for help
			if (cl.hasOption(ARG_HELP)) {
				retVal.isHelp = true;
			}

			retVal.errors = "";

			// Required
			retVal.inputFileName = cl.getOptionValue(ARG_INPUT_FILE);
			if (retVal.inputFileName != null) {
				retVal.inputFileName = retVal.inputFileName.trim();
			}
			if (retVal.inputFileName == null || retVal.inputFileName.isEmpty()) {
				retVal.errors += missingArgument(ARG_INPUT_FILE) + EOL;
			}

			String sInputFormat = cl.getOptionValue(ARG_INPUT_FORMAT);
			if (sInputFormat != null) {
				sInputFormat = sInputFormat.trim().toUpperCase();
			}
			if (sInputFormat == null || sInputFormat.isEmpty()) {
				retVal.errors += missingArgument(ARG_INPUT_FORMAT) + EOL;
			}
			try {
				retVal.inputFormat =
					LOG_PARTITIONER_FILE_FORMAT.valueOf(sInputFormat);
			} catch (IllegalArgumentException x) {
				retVal.errors +=
					invalidArgument(ARG_INPUT_FORMAT, sInputFormat);
			}

			retVal.outputFileName = cl.getOptionValue(ARG_OUTPUT_FILE);
			if (retVal.outputFileName != null) {
				retVal.outputFileName = retVal.outputFileName.trim();
			}
			if (retVal.outputFileName == null
					|| retVal.outputFileName.isEmpty()) {
				retVal.errors += missingArgument(ARG_OUTPUT_FILE) + EOL;
			}

			String sOutputFormat = cl.getOptionValue(ARG_OUTPUT_FORMAT);
			try {
				retVal.outputFormat =
					LOG_PARTITIONER_FILE_FORMAT.valueOf(sOutputFormat);
			} catch (IllegalArgumentException x) {
				retVal.errors +=
					invalidArgument(ARG_OUTPUT_FORMAT, sOutputFormat);
			}

			String sPartitionCount = cl.getOptionValue(ARG_PARTITION_COUNT);
			try {
				retVal.partitionCount = Integer.valueOf(sPartitionCount);
			} catch (NumberFormatException x) {
				retVal.errors +=
					invalidArgument(ARG_PARTITION_COUNT, sPartitionCount);
			}
			if (retVal.partitionCount < 1) {
				retVal.errors +=
					"Non-positive partition number: " + retVal.partitionCount
							+ EOL;
			}

			// Optional
			retVal.inputFieldSep = COMMA;
			String sInputFieldSep = cl.getOptionValue(ARG_INPUT_CSV_FIELD_SEP);
			if (sInputFieldSep != null) {
				sInputFieldSep = sInputFieldSep.trim();
				if (!sInputFieldSep.isEmpty()) {
					retVal.inputFieldSep = sInputFieldSep.charAt(0);
				}
			}

			retVal.inputLineSep = EOL;
			String sInputLineSep = cl.getOptionValue(ARG_INPUT_LINE_SEP);
			if (sInputLineSep != null) {
				if (!sInputLineSep.isEmpty()) {
					retVal.inputLineSep = sInputLineSep;
				}
			}

			retVal.outputFieldSep = COMMA;
			String sOutputFieldSep =
				cl.getOptionValue(ARG_OUTPUT_CSV_FIELD_SEP);
			if (sOutputFieldSep != null) {
				sOutputFieldSep = sOutputFieldSep.trim();
				if (!sOutputFieldSep.isEmpty()) {
					retVal.outputFieldSep = sOutputFieldSep.charAt(0);
				}
			}

			retVal.outputLineSep = EOL;
			String sOutputLineSep = cl.getOptionValue(ARG_OUTPUT_LINE_SEP);
			if (sOutputLineSep != null) {
				if (!sOutputLineSep.isEmpty()) {
					retVal.outputLineSep = sOutputLineSep;
				}
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

	public static void printErrors(PrintWriter pw, String errors)
			throws IOException {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		if (errors != null && !errors.trim().isEmpty()) {
			pw.println("Errors:");
			StringReader sr = new StringReader(errors);
			BufferedReader br = new BufferedReader(sr);
			String line = br.readLine();
			while (line != null) {
				pw.println(line);
				line = br.readLine();
			}
		}
	}

	public static void usage(PrintWriter pw) {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		Options options = createOptions();
		HelpFormatter formatter = new HelpFormatter();
		formatter.printUsage(pw, formatter.getWidth(), COMMAND_LINE, options);
	}

	private LogPartitionerCommandLine() {
	}

}
