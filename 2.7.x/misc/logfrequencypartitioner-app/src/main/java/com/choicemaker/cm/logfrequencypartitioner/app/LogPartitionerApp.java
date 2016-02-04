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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.ParseException;

import com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT;
import com.choicemaker.util.LogFrequencyPartitioner;
import com.choicemaker.util.LogFrequencyPartitioner.ValueCountPair;
import com.choicemaker.util.LogFrequencyPartitioner.ValuePartitionPair;
import com.choicemaker.util.SystemPropertyUtils;

/**
 * Application that ...
 * 
 * @param args
 */
public class LogPartitionerApp {

	private static final Logger logger = Logger
			.getLogger(LogPartitionerApp.class.getName());

	private static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

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
	public static void main(String[] args) throws Exception {
		PrintWriter console =
			new PrintWriter(new OutputStreamWriter(System.out));
		final LogPartitionerParams appParms =
			LogPartitionerCommandLine.parseCommandLine(console, args);
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

	public void createPartitions() throws IOException {

		try {
			List<ValueCountPair> input =
				readInput(getAppParams().getInputFileName(), getAppParams()
						.getInputFormat(), getAppParams()
						.getInputCsvFieldSeparator());
			List<ValuePartitionPair> output =
				LogFrequencyPartitioner.partition(input, getAppParams()
						.getPartitionCount());
			writeOutput(output, getAppParams().getOutputFileName(),
					getAppParams().getOutputFormat(), getAppParams()
							.getOutputCsvFieldSeparator());
		} catch (IOException x) {
			logger.severe(x.toString());
			throw x;
		}
	}

	public List<ValueCountPair> readInput(String fileName,
			LOG_PARTITIONER_FILE_FORMAT fileFormat, char csvFieldSeparator)
			throws IOException {
		List<ValueCountPair> retVal = null;
		switch (getAppParams().getInputFormat()) {
		case CSV:
			throw new Error("not yet implemented");
			// break;
		case ALT_LINES:
		default:
			retVal = LogFrequencyPartitioner.readFile(fileName);
		}
		assert retVal != null;
		return retVal;
	}

	public void writeOutput(List<ValuePartitionPair> output, String fileName,
			LOG_PARTITIONER_FILE_FORMAT fileFormat, char csvFieldSeparator)
			throws IOException {
		LogFrequencyPartitioner.writeFile(output, fileName,
				String.valueOf(getAppParams().getOutputCsvFieldSeparator()),
				EOL);
	}

}
