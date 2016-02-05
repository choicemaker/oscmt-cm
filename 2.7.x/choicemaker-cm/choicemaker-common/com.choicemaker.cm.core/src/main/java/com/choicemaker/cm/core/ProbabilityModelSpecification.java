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