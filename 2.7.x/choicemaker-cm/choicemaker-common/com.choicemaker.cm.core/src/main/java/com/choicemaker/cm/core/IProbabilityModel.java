/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.util.Date;

/**
 * Extends the immutable interface of a ChoiceMaker matching model by adding
 * operations that modify the model. (Because of the functionality that this
 * interface defines, it could have been named IMutableProbabilityModel, but
 * that name would have been too easily confused with the name of the immutable
 * interface, ImmutableProbabilityModel.)
 * 
 * @author rphall
 */
public interface IProbabilityModel extends AccessProvider,
		ImmutableProbabilityModel {

	void beginMultiPropertyChange();

	void endMultiPropertyChange();

	/**
	 * Sets the translator accessors.
	 *
	 * @param newAcc
	 *            The translator accessors.
	 * @throws ModelConfigurationException
	 */
	@Override
	void setAccessor(Accessor newAcc) throws ModelConfigurationException;

	/**
	 * Sets the clues to evaluate.
	 *
	 * @param cluesToEvaluate
	 *            The clues to evaluate.
	 */
	void setCluesToEvaluate(boolean[] cluesToEvaluate)
			throws IllegalArgumentException;

	/**
	 * Set the value of enableAllCluesBeforeTraining.
	 * 
	 * @param v
	 *            Value to assign to enableAllCluesBeforeTraining.
	 */
	void setEnableAllCluesBeforeTraining(boolean v);

	/**
	 * Set the value of enableAllRulesBeforeTraining.
	 * 
	 * @param v
	 *            Value to assign to enableAllRulesBeforeTraining.
	 */
	void setEnableAllRulesBeforeTraining(boolean v);

	/**
	 * Sets the path to the probability model weights file (*.model)
	 * 
	 * If this model is in the collection of probability models, the
	 * {@link #getModelName() name} that it is associated with in the collection
	 * does not get changed.
	 * 
	 * @param filePath
	 *            A path to the model file
	 */
	void setModelFilePath(String filePath);

	/**
	 * Set the value of firingThreshold.
	 * 
	 * @param v
	 *            Value to assign to firingThreshold.
	 */
	void setFiringThreshold(int v);

	/**
	 * Set the value of lastTrainingDate.
	 * 
	 * @param v
	 *            Value to assign to lastTrainingDate.
	 */
	void setLastTrainingDate(Date v);

	void setMachineLearner(MachineLearner ml);

	void setClueFilePath(String fn);

	void setTrainedWithHolds(boolean b);

	/**
	 * Set the value of trainingSource.
	 * 
	 * @param v
	 *            Value to assign to trainingSource.
	 */
	void setTrainingSource(String v);

	/**
	 * Set the value of userName.
	 * 
	 * @param v
	 *            Value to assign to userName.
	 */
	void setUserName(String v);

}
