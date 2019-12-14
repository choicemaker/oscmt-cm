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

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerCommandLine.COMMAND_LINE;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.choicemaker.util.LogFrequencyPartitioner;
import com.choicemaker.util.LogFrequencyPartitioner.ValueCount;
import com.choicemaker.util.LogFrequencyPartitioner.ValueRank;

/**
 * LogPartitionerApp is an application that partitions values into evenly
 * spaced, logarithmic bins based on value-frequency counts.
 * <p/>
 * Consider the following value- count pairs:
 * <p/>
 * 
 * <pre>
 * { "value 1", 1 },
 * { "value 3", 3 },
 * { "value 4", 4 },
 * { "value 10", 10 },
 * { "value 31", 31 },
 * { "value 32", 32 },
 * { "value 100", 100 }
 * </pre>
 *
 * A 4-bin, evenly spaced logarithmic partition from the minimum count (1) to
 * the maximum count (100) defines the following ranges:
 * <p/>
 * <ul>
 * <li>Range 0: [1.00 - 3.16) <i>(least frequent)</i></li>
 * <li>Range 1: [3.16 - 10.0)</li>
 * <li>Range 2: [10.0 - 31.6)</li>
 * <li>Range 3: [31.6 - 100.0] <i>(most frequent)</i></li>
 * </ul>
 * <p/>
 * Each float-value range is rounded to an integer by adding 0.5 and then
 * truncating. This produces the following value-partition pairs:
 * 
 * <pre>
 * { "value 1", 0 },
 * { "value 3", 0 },
 * { "value 4", 0 },
 * { "value 10", 1 },
 * { "value 31", 1 },
 * { "value 32", 2 },
 * { "value 100", 3 }
 * </pre>
 */
public class LogPartitionerApp {

	private static final Logger logger = Logger
			.getLogger(LogPartitionerApp.class.getName());

	public static final int STATUS_OK = 0;
	public static final int ERROR_BAD_INPUT = 1;

	/**
	 * Entry point for the application. Command line parameters may be listed by
	 * invoking main with empty args, or by specifying the '-help' option.
	 * 
	 * @param args
	 *            a non-null arrray, possibly empty
	 * @throws ParseException
	 *             if the command line can not be parsed
	 * @see LogPartitionerCommandLine
	 */
	public static void main(String[] args) throws Exception {

		logger.fine("LogPartitionerApp (main) args: " + Arrays.toString(args));

		int exitCode = STATUS_OK;
		PrintWriter console = null;
		try {
			console = new PrintWriter(new OutputStreamWriter(System.out));

			final LogPartitionerParams appParms =
				LogPartitionerCommandLine.parseCommandLine(args);
			assert appParms != null;

			if (appParms.isHelp() && !appParms.hasErrors()) {
				printHelp(console);
				exitCode = STATUS_OK;
			} else if (appParms.isHelp() && appParms.hasErrors()) {
				printErrors(console, appParms.getErrors());
				printHelp(console);
				exitCode = ERROR_BAD_INPUT;
			} else if (appParms.hasErrors()) {
				assert !appParms.isHelp();
				printErrors(console, appParms.getErrors());
				printUsage(console);
				exitCode = ERROR_BAD_INPUT;
			} else {
				assert !appParms.isHelp();
				assert !appParms.hasErrors();
				final LogPartitionerApp app = new LogPartitionerApp(appParms);
				final int count = app.createPartitions();
				printResult(console, count, appParms.getOutputFile());
				exitCode = STATUS_OK;
			}
			console.flush();

		} catch (Exception x) {
			x.printStackTrace(console);

		} finally {
			if (console != null) {
				console.close();
			}
		}

		System.exit(exitCode);
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

	public int createPartitions() throws IOException {

		int retVal = 0;
		try {
			List<ValueCount> input =
				readInput(getAppParams().getInputFileName(), getAppParams()
						.getInputFormat(), getAppParams()
						.getInputFieldSeparator(), getAppParams()
						.getInputLineSeparator());
			List<ValueRank> output =
				LogFrequencyPartitioner.partition(input, getAppParams()
						.getPartitionCount());
			retVal =
				writeOutput(output, getAppParams().getOutputFileName(),
						getAppParams().getOutputFormat(), getAppParams()
								.getOutputFieldSeparator(), getAppParams()
								.getOutputLineSeparator());
		} catch (IOException x) {
			logger.severe(x.toString());
			throw x;
		}
		return retVal;
	}

	public static List<ValueCount> readInput(String fileName,
			LogPartitionerFileFormat fileFormat, char fieldSeparator,
			String lineSeparator) throws IOException {
		List<ValueCount> retVal = null;
		switch (fileFormat) {
		case DELIMITED:
			retVal =
				LogFrequencyPartitioner.readFile(fileName, fieldSeparator,
						lineSeparator);
			break;
		case ALT_LINES:
		default:
			retVal =
				LogFrequencyPartitioner.readFile(fileName, null, lineSeparator);
		}
		assert retVal != null;
		return retVal;
	}

	public static <T extends ValueRank> int writeOutput(List<T> output,
			String fileName, LogPartitionerFileFormat fileFormat,
			char fieldSeparator, String lineSeparator) throws IOException {
		int retVal;
		switch (fileFormat) {
		case DELIMITED:
			retVal =
				LogFrequencyPartitioner.writeFile(output, fileName,
						fieldSeparator, lineSeparator);
			break;
		case ALT_LINES:
		default:
			retVal =
				LogFrequencyPartitioner.writeFile(output, fileName, null,
						lineSeparator);
		}
		return retVal;
	}

	public static void printResult(PrintWriter pw, int count, File outputFile)
			throws IOException {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		if (outputFile == null) {
			throw new IllegalArgumentException("null output file");
		}
		pw.println();
		pw.println(count + " value-paritition pairs written to "
				+ outputFile.getAbsolutePath());
		pw.println();
	}

	public static void printErrors(PrintWriter pw, List<String> errors)
			throws IOException {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		if (errors != null && !errors.isEmpty()) {
			pw.println();
			pw.println("Errors:");
			for (String error : errors) {
				pw.println(error);
			}
		}
	}

	public static void printHelp(PrintWriter pw) {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		Options options = LogPartitionerCommandLine.createOptions();
		HelpFormatter formatter = new HelpFormatter();
		pw.println();
		final String header = null;
		final String footer = null;
		boolean autoUsage = true;
		formatter.printHelp(pw, formatter.getWidth(), COMMAND_LINE, header,
				options, formatter.getLeftPadding(),
				formatter.getDescPadding(), footer, autoUsage);
		pw.println();
	}

	public static void printUsage(PrintWriter pw) {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		Options options = LogPartitionerCommandLine.createOptions();
		HelpFormatter formatter = new HelpFormatter();
		pw.println();
		formatter.printUsage(pw, formatter.getWidth(), COMMAND_LINE, options);
		pw.println();
	}

}
