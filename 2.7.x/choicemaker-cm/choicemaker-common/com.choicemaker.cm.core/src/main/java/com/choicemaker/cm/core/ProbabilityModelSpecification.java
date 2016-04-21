/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

public interface ProbabilityModelSpecification {

	/** A path to the model weights file (*.model) */
	String getWeightFilePath();

//	/** An absolute path to the model weights file (*.model) */
//	String getWeightFileAbsolutePath();

	/**
	 * A relative path from the model weights file (*.model) to the clue set
	 * file (*.clues)
	 */
	String getClueFilePath();

	/**
	 * Returns an absolute path (possibly null) to the clue definition file.
	 */
	String getClueFileAbsolutePath();

	MachineLearner getMachineLearner();

}