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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT;

/**
 * Unchecked base class for testing purposes.
 */
class UncheckedParams {

	private static final Logger logger = Logger
			.getLogger(UncheckedParams.class.getName());

	private final boolean isHelp;
	private final List<String> errors = new ArrayList<>();
	private final int partitionCount;
	private final String inputFileName;
	private final LOG_PARTITIONER_FILE_FORMAT inputFormat;
	private final char inputFieldSeparator;
	private final String inputLineSeparator;
	private final String outputFileName;
	private final LOG_PARTITIONER_FILE_FORMAT outputFormat;
	private final char outputFieldSeparator;
	private final String outputLineSeparator;

	public UncheckedParams(boolean isHelp, List<String> errors,
			String inputFileName, LOG_PARTITIONER_FILE_FORMAT inputFormat,
			char inputFieldSeparator, String inputLineSeparator,
			String outputFileName, LOG_PARTITIONER_FILE_FORMAT outputFormat,
			char outputFieldSeparator, String outputLineSeparator,
			int partitionCount) {

		this.isHelp = isHelp;
		if (errors != null) {
			this.errors.addAll(errors);
		}
		this.inputFileName = inputFileName;
		this.inputFormat = inputFormat;
		this.inputFieldSeparator = inputFieldSeparator;
		this.inputLineSeparator = inputLineSeparator;

		this.outputFileName = outputFileName;
		this.outputFormat = outputFormat;
		this.outputFieldSeparator = outputFieldSeparator;
		this.outputLineSeparator = outputLineSeparator;

		this.partitionCount = partitionCount;

		logger.fine(this.toString());
	}

	public boolean isHelp() {
		return isHelp;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public List<String> getErrors() {
		return errors;
	}

	public char getInputFieldSeparator() {
		return inputFieldSeparator;
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

	public char getOutputFieldSeparator() {
		return outputFieldSeparator;
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
		return "UncheckedParams [isHelp=" + isHelp + ", errors=" + errors
				+ ", partitionCount=" + partitionCount + ", inputFileName="
				+ inputFileName + ", inputFormat="
				+ inputFormat + ", inputFieldSeparator=" + inputFieldSeparator
				+ ", inputLineSeparator=" + inputLineSeparator
				+ ", outputFileName=" + outputFileName + ", outputFormat=" + outputFormat
				+ ", outputFieldSeparator=" + outputFieldSeparator
				+ ", outputLineSeparator=" + outputLineSeparator + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + inputFieldSeparator;
		result =
			prime * result
					+ ((inputFileName == null) ? 0 : inputFileName.hashCode());
		result =
			prime * result
					+ ((inputFormat == null) ? 0 : inputFormat.hashCode());
		result =
			prime
					* result
					+ ((inputLineSeparator == null) ? 0 : inputLineSeparator
							.hashCode());
		result = prime * result + (isHelp ? 1231 : 1237);
		result = prime * result + outputFieldSeparator;
		result =
			prime
					* result
					+ ((outputFileName == null) ? 0 : outputFileName.hashCode());
		result =
			prime * result
					+ ((outputFormat == null) ? 0 : outputFormat.hashCode());
		result =
			prime
					* result
					+ ((outputLineSeparator == null) ? 0 : outputLineSeparator
							.hashCode());
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
		UncheckedParams other = (UncheckedParams) obj;
		if (errors == null) {
			if (other.errors != null) {
				return false;
			}
		} else if (!errors.equals(other.errors)) {
			return false;
		}
		if (inputFieldSeparator != other.inputFieldSeparator) {
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
		if (inputLineSeparator == null) {
			if (other.inputLineSeparator != null) {
				return false;
			}
		} else if (!inputLineSeparator.equals(other.inputLineSeparator)) {
			return false;
		}
		if (isHelp != other.isHelp) {
			return false;
		}
		if (outputFieldSeparator != other.outputFieldSeparator) {
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
		if (outputLineSeparator == null) {
			if (other.outputLineSeparator != null) {
				return false;
			}
		} else if (!outputLineSeparator.equals(other.outputLineSeparator)) {
			return false;
		}
		if (partitionCount != other.partitionCount) {
			return false;
		}
		return true;
	}

}
