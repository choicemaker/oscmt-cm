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
import java.util.logging.Logger;

import com.choicemaker.util.SystemPropertyUtils;

public class LogPartitionerParams {

	private static final Logger logger = Logger
			.getLogger(LogPartitionerParams.class.getName());

	public static enum LOG_PARTITIONER_FILE_FORMAT {
		CSV, ALT_LINES
	}

	/** Deprecated 2.5 standard */
	@Deprecated
	public static final LOG_PARTITIONER_FILE_FORMAT CM25_STANDARD =
		LOG_PARTITIONER_FILE_FORMAT.ALT_LINES;

	public static final char COMMA = ',';
	public static final char PIPE = '|';
	public static final char DEFAULT_CSV_FIELD_SEPARATOR = COMMA;
	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	private final int partitionCount;

	private final String inputFileName;
	private final File inputFile;
	private final LOG_PARTITIONER_FILE_FORMAT inputFormat;
	private final char inputCsvFieldSeparator;
	private final String inputLineSeparator;

	private final String outputFileName;
	private final File outputFile;
	private final LOG_PARTITIONER_FILE_FORMAT outputFormat;
	private final char outputCsvFieldSeparator;
	private final String outputLineSeparator;

	/** Recommended constructor */
	public LogPartitionerParams(String inputFileName, char csvFieldSeparator,
			String outputFileName, int partitionCount) {
		this(inputFileName, LOG_PARTITIONER_FILE_FORMAT.CSV, csvFieldSeparator,
				EOL, outputFileName, LOG_PARTITIONER_FILE_FORMAT.CSV,
				csvFieldSeparator, EOL, partitionCount);
	}

	/** Full constructor */
	public LogPartitionerParams(String inputFileName,
			LOG_PARTITIONER_FILE_FORMAT inputFormat,
			char inputCsvFieldSeparator, String inputLineSeparator,
			String outputFileName, LOG_PARTITIONER_FILE_FORMAT outputFormat,
			char outputCsvFieldSeparator, String outputLineSeparator,
			int partitionCount) {

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
			throw new IllegalArgumentException(
					"null input line separator");
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
			throw new IllegalArgumentException(
					"null output line separator");
		}

		if (partitionCount < 1) {
			throw new IllegalArgumentException("non-postive partition count: "
					+ partitionCount);
		}

		this.inputFileName = inputFileName;
		this.inputFile = new File(this.inputFileName);
		if (!this.inputFile.exists() || !this.inputFile.isFile()) {
			String msg =
				"'" + inputFileName + "' does not exist or is not a file.";
			throw new IllegalArgumentException(msg);
		}
		this.inputFormat = inputFormat;
		this.inputCsvFieldSeparator = inputCsvFieldSeparator;
		this.inputLineSeparator = inputLineSeparator;

		this.outputFileName = outputFileName;
		this.outputFile = new File(this.outputFileName);
		if (this.outputFile.exists()) {
			String msg = "'" + outputFileName + "' already exists";
			throw new IllegalArgumentException(msg);
		}
		this.outputFormat = outputFormat;
		this.outputCsvFieldSeparator = outputCsvFieldSeparator;
		this.outputLineSeparator = outputLineSeparator;

		this.partitionCount = partitionCount;

		logger.info(this.toString());
	}

	/** Deprecated ChoiceMaker 2.5 standard */
	@Deprecated
	public LogPartitionerParams(String inputFileName, String outputFileName,
			int partitionCount) {
		this(inputFileName, CM25_STANDARD, COMMA, EOL, outputFileName,
				CM25_STANDARD, COMMA, EOL, partitionCount);
	}

	public char getInputCsvFieldSeparator() {
		return inputCsvFieldSeparator;
	}

	public File getInputFile() {
		return inputFile;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public LOG_PARTITIONER_FILE_FORMAT getInputFormat() {
		return inputFormat;
	}

	public String getInputLineSeparator() {
		return inputLineSeparator;
	}

	public char getOutputCsvFieldSeparator() {
		return outputCsvFieldSeparator;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public LOG_PARTITIONER_FILE_FORMAT getOutputFormat() {
		return outputFormat;
	}

	public String getOutputLineSeparator() {
		return outputLineSeparator;
	}

	public int getPartitionCount() {
		return partitionCount;
	}

	@Override
	public String toString() {
		return "LogPartitionerParams [partitionCount=" + partitionCount
				+ ", inputFileName=" + inputFileName + ", inputFile="
				+ inputFile + ", outputFileName=" + outputFileName
				+ ", outputFile=" + outputFile + ", inputFormat=" + inputFormat
				+ ", inputCsvFieldSeparator=" + inputCsvFieldSeparator
				+ ", outputFormat=" + outputFormat
				+ ", outputCsvFieldSeparator=" + outputCsvFieldSeparator + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + inputCsvFieldSeparator;
		result =
			prime * result + ((inputFile == null) ? 0 : inputFile.hashCode());
		result =
			prime * result
					+ ((inputFileName == null) ? 0 : inputFileName.hashCode());
		result =
			prime * result
					+ ((inputFormat == null) ? 0 : inputFormat.hashCode());
		result = prime * result + outputCsvFieldSeparator;
		result =
			prime * result + ((outputFile == null) ? 0 : outputFile.hashCode());
		result =
			prime
					* result
					+ ((outputFileName == null) ? 0 : outputFileName.hashCode());
		result =
			prime * result
					+ ((outputFormat == null) ? 0 : outputFormat.hashCode());
		result = prime * result + partitionCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LogPartitionerParams other = (LogPartitionerParams) obj;
		if (inputCsvFieldSeparator != other.inputCsvFieldSeparator) {
			return false;
		}
		if (inputFile == null) {
			if (other.inputFile != null) {
				return false;
			}
		} else if (!inputFile.equals(other.inputFile)) {
			return false;
		}
		if (inputFileName == null) {
			if (other.inputFileName != null) {
				return false;
			}
		} else if (!inputFileName.equals(other.inputFileName)) {
			return false;
		}
		if (inputFormat != other.inputFormat) {
			return false;
		}
		if (outputCsvFieldSeparator != other.outputCsvFieldSeparator) {
			return false;
		}
		if (outputFile == null) {
			if (other.outputFile != null) {
				return false;
			}
		} else if (!outputFile.equals(other.outputFile)) {
			return false;
		}
		if (outputFileName == null) {
			if (other.outputFileName != null) {
				return false;
			}
		} else if (!outputFileName.equals(other.outputFileName)) {
			return false;
		}
		if (outputFormat != other.outputFormat) {
			return false;
		}
		if (partitionCount != other.partitionCount) {
			return false;
		}
		return true;
	}

}
