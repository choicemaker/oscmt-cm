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

import java.util.List;

import org.jdom2.Element;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.xmlconf.MlModelConf;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class DoNothingMachineLearningPersistance implements MlModelConf {
	public static DoNothingMachineLearningPersistance instance = new DoNothingMachineLearningPersistance();

	public DoNothingMachineLearningPersistance() {
	}

	@Override
	public MachineLearner readMachineLearner(Element e, Accessor acc, List<?> clues, int[] oldClueNums) {
		return new DoNothingMachineLearning();
	}

	@Override
	public void saveClue(Element e, int clueNum) {
	}

	@Override
	public void saveMachineLearner(Element e) {
	}

	@Override
	public String getExtensionPointId() {
		return "com.choicemaker.cm.core.none";
	}
}
