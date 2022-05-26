/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.base;

import java.util.Collection;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Evaluator;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.xmlconf.MlModelConf;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class DoNothingMachineLearning implements MachineLearner {

	@Override
	public Evaluator getEvaluator() {
		return null;
	}

	@Override
	public void setProbabilityModel(ImmutableProbabilityModel model) {
	}

	@Override
	public void changedAccessor(Accessor oldAccessor, Accessor newAccessor, int[] oldClueNums) {
	}

	@Override
	public Object train(Collection src, double[] firingPercentages) {
		return null;
	}

	@Override
	public MlModelConf getModelConf() {
		return DoNothingMachineLearningPersistance.instance;
	}

	@Override
	public boolean canEvaluate() {
		return false;
	}

	@Override
	public boolean canTrain() {
		return false;
	}

	@Override
	public boolean canUse(ClueSet clueset) {
		return true;
	}

	@Override
	public boolean isRegression() {
		return true;
	}
	
	@Override
	public String getModelInfo() {
		return null;
	}
}
