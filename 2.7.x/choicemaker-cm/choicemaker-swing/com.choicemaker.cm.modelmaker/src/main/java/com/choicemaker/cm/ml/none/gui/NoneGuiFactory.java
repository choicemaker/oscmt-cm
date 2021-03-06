/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.ml.none.gui;

import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.base.DoNothingMachineLearning;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.modelmaker.gui.hooks.TrainDialogPlugin;
import com.choicemaker.cm.modelmaker.gui.ml.MlGuiFactory;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class NoneGuiFactory extends MlGuiFactory {
	/**
	 * @see com.choicemaker.cm.ml.gui.MlGuiFactory#getTrainDialogPlugin(com.choicemaker.cm.core.MachineLearner)
	 */
	@Override
	public TrainDialogPlugin getTrainDialogPlugin(MachineLearner learner) {
		return new NoneTrainDialogPlugin();
	}
	/**
	 * @see com.choicemaker.cm.core.base.DynamicDispatchHandler#getHandler()
	 */
	@Override
	public Object getHandler() {
		return this;
	}
	/**
	 * @see com.choicemaker.cm.core.base.DynamicDispatchHandler#getHandledType()
	 */
	@Override
	public Class getHandledType() {
		return DoNothingMachineLearning.class;
	}
	
	@Override
	public String toString() {
		return ChoiceMakerCoreMessages.m.formatMessage("ml.none.label");
	}
	/**
	 * @see com.choicemaker.cm.ml.gui.MlGuiFactory#getMlInstance()
	 */
	@Override
	public MachineLearner getMlInstance() {
		return new DoNothingMachineLearning();
	}
}
