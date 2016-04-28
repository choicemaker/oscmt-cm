/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

public interface ModelTrainingRegistry {

	String INSTANCE = "instance";

	ModelTrainer getModelTrainer(ImmutableProbabilityModel model);

	MachineLearner getMachineLearning(ImmutableProbabilityModel model);

	void register(ImmutableProbabilityModel model, MachineLearner ml);

	PairEvaluator getPairEvaluator(ImmutableProbabilityModel model);

	void register(ImmutableProbabilityModel model, PairEvaluator pe);

}
