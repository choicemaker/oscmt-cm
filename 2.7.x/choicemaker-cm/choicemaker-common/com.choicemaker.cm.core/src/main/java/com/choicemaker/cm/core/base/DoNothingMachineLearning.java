/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
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

	public Evaluator getEvaluator() {
		return null;
	}

	public void setProbabilityModel(ImmutableProbabilityModel model) {
	}

	public void changedAccessor(Accessor oldAccessor, Accessor newAccessor, int[] oldClueNums) {
	}

	public Object train(Collection src, double[] firingPercentages) {
		return null;
	}

	public MlModelConf getModelConf() {
		return DoNothingMachineLearningPersistance.instance;
	}

	public boolean canEvaluate() {
		return false;
	}

	public boolean canTrain() {
		return false;
	}

	public boolean canUse(ClueSet clueset) {
		return true;
	}

	public boolean isRegression() {
		return true;
	}
	
	public String getModelInfo() {
		return null;
	}
}
