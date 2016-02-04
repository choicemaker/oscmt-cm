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

public class LogPartitionerParams {

	private final String inputFileName;
	private final File inputFile;
	private final String outputFileName;
	private final File outputFile;

	public LogPartitionerParams(String inputFileName, String outputFileName) {

		if (inputFileName == null || inputFileName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"null or blank file name for input counts");
		}
		if ( outputFileName == null ||  outputFileName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"null or blank file name for output partitions");
		}

		this.inputFileName =  inputFileName;
		this.inputFile = new File(this.inputFileName);
		if (!this.inputFile.exists() || !this.inputFile.isFile()) {
			String msg =
				"'" + inputFileName + "' does not exist or is not a file.";
			throw new IllegalArgumentException(msg);
		}

		this.outputFileName = outputFileName;
		this.outputFile = new File(this.outputFileName);
		if (this.outputFile.exists()) {
			String msg = "'" + outputFileName + "' already exists";
			throw new IllegalArgumentException(msg);
		}

	}

	public String getInputFileName() {
		return inputFileName;
	}

	public File getInputFile() {
		return inputFile;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public File getOutputFile() {
		return outputFile;
	}

}
