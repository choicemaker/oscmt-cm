/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

/**
 * A list of well-known, but non-exclusive, names for model properties
 * 
 * @author rphall
 */
public interface ModelAttributeNames {
	String AN_ACCESSOR_CLASS = "accessorClass";
	String AN_CLASS = "class";
	String AN_CLUE_FILE_NAME = "clueFileName";
	String AN_ENABLE_ALL_CLUES_BEFORE_TRAINING = "enableAllCluesBeforeTraining";
	String AN_ENABLE_ALL_RULES_BEFORE_TRAINING = "enableAllRulesBeforeTraining";
	String AN_FIRING_THRESHOLD = "firingThreshold";
	String AN_LAST_TRAINING_DATE = "lastTrainingDate";
	String AN_TRAINED_WITH_HOLDS = "trainedWithHolds";
	String AN_TRAINING_SOURCE = "trainingSource";
	String AN_USER_NAME = "userName";
	String AN_BUILD_VERSION = "buildVersion";
}
