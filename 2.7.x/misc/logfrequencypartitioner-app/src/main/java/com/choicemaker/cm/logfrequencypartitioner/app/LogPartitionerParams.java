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

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.util.SystemPropertyUtils;

public class LogPartitionerParams extends UncheckedParams {

	private static final Logger logger = Logger
			.getLogger(LogPartitionerParams.class.getName());

	/** Deprecated 2.5 standard */
	@Deprecated
	public static final LogPartitionerFileFormat CM25_STANDARD =
		LogPartitionerFileFormat.ALT_LINES;

	public static final char COMMA = ',';
	public static final char PIPE = '|';
	public static final char DEFAULT_FIELD_SEPARATOR = COMMA;
	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	private final File inputFile;
	private final File outputFile;

	/** Help constructor */
	public LogPartitionerParams() {
		super(false, null, null, null, COMMA, null, null, null, COMMA, null, 0);
		this.inputFile = null;
		this.outputFile = null;
	}

	/** Error constructor */
	public LogPartitionerParams(boolean isHelp, List<String> errors) {
		super(isHelp, errors, null, null, COMMA, null, null, null, COMMA, null,
				0);
		if (errors == null) {
			throw new IllegalArgumentException("null error list");
		}
		this.inputFile = null;
		this.outputFile = null;
	}

	/** Full constructor */
	public LogPartitionerParams(boolean isHelp, List<String> errors,
			String inputFileName, LogPartitionerFileFormat inputFormat,
			char inputFieldSeparator, String inputLineSeparator,
			String outputFileName, LogPartitionerFileFormat outputFormat,
			char outputFieldSeparator, String outputLineSeparator,
			int partitionCount) {
		super(isHelp, errors, inputFileName, inputFormat, inputFieldSeparator,
				inputLineSeparator, outputFileName, outputFormat,
				outputFieldSeparator, outputLineSeparator, partitionCount);

		if (inputFileName == null) {
			throw new IllegalArgumentException(
					"null file name for input counts");
		} else {
			inputFileName = inputFileName.trim();
			if (inputFileName.isEmpty()) {
				throw new IllegalArgumentException(
						"blank file name for input counts");
			}
		}

		if (inputFormat == null) {
			throw new IllegalArgumentException("null input file format");
		}

		if (inputLineSeparator == null) {
			throw new IllegalArgumentException("null input line separator");
		}

		if (outputFileName == null) {
			throw new IllegalArgumentException(
					"null file name for output counts");
		} else {
			outputFileName = outputFileName.trim();
			if (outputFileName.isEmpty()) {
				throw new IllegalArgumentException(
						"blank file name for output counts");
			}
		}

		if (outputFormat == null) {
			throw new IllegalArgumentException("null output file format");
		}

		if (outputLineSeparator == null) {
			throw new IllegalArgumentException("null output line separator");
		}

		if (partitionCount < 1) {
			throw new IllegalArgumentException("non-postive partition count: "
					+ partitionCount);
		}

		this.inputFile = new File(inputFileName);
		if (!this.inputFile.exists() || !this.inputFile.isFile()) {
			String msg =
				"'" + inputFileName + "' does not exist or is not a file.";
			throw new IllegalArgumentException(msg);
		}
		this.outputFile = new File(outputFileName);
		if (this.outputFile.exists()) {
			String msg = "'" + outputFileName + "' already exists";
			throw new IllegalArgumentException(msg);
		}
		logger.fine(this.toString());
	}

	/** Deprecated ChoiceMaker 2.5 standard */
	@Deprecated
	public LogPartitionerParams(String inputFileName, String outputFileName,
			int partitionCount) {
		this(false, null, inputFileName, CM25_STANDARD, COMMA, EOL,
				outputFileName, CM25_STANDARD, COMMA, EOL, partitionCount);
	}

	public File getInputFile() {
		return inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	@Override
	public String toString() {
		return "LogPartitionerParams [getInputFile()=" + getInputFile()
				+ ", getOutputFile()=" + getOutputFile() + ", isHelp()="
				+ isHelp() + ", hasErrors()=" + hasErrors() + ", getErrors()="
				+ getErrors() + ", getInputFieldSeparator()="
				+ getInputFieldSeparator() + ", getInputFileName()="
				+ getInputFileName() + ", getInputFormat()=" + getInputFormat()
				+ ", getInputLineSeparator()=" + getInputLineSeparator()
				+ ", getOutputFieldSeparator()=" + getOutputFieldSeparator()
				+ ", getOutputFileName()=" + getOutputFileName()
				+ ", getOutputFormat()=" + getOutputFormat()
				+ ", getOutputLineSeparator()=" + getOutputLineSeparator()
				+ ", getPartitionCount()=" + getPartitionCount() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result =
			prime * result + ((inputFile == null) ? 0 : inputFile.hashCode());
		result =
			prime * result + ((outputFile == null) ? 0 : outputFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LogPartitionerParams other = (LogPartitionerParams) obj;
		if (inputFile == null) {
			if (other.inputFile != null) {
				return false;
			}
		} else if (!inputFile.equals(other.inputFile)) {
			return false;
		}
		if (outputFile == null) {
			if (other.outputFile != null) {
				return false;
			}
		} else if (!outputFile.equals(other.outputFile)) {
			return false;
		}
		return true;
	}

}
