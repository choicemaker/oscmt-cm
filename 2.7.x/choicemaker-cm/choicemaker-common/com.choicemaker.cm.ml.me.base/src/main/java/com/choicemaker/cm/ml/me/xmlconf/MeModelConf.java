/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.ml.me.xmlconf;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.xmlconf.MlModelConf;
import com.choicemaker.cm.ml.me.base.MaximumEntropy;
import com.choicemaker.util.ArrayHelper;

public class MeModelConf implements MlModelConf {
	private MaximumEntropy ml;

	public MeModelConf() {
	}

	public MeModelConf(MaximumEntropy ml) {
		this.ml = ml;
	}

	public MachineLearner readMachineLearner(Element e, Accessor acc, List<?> clues, int[] oldClueNums) {
		MaximumEntropy me = new MaximumEntropy();
		me.setTrainingIterations(Integer.parseInt(e.getAttributeValue("trainingIterations")));
		float[] weights = ArrayHelper.getOneArray(acc.getClueSet().size());
		@SuppressWarnings("unchecked")
		Iterator<Element> iClues = (Iterator<Element>) clues.iterator();
		int i = 0;
		while (iClues.hasNext()) {
			int clueNum = oldClueNums[i];
			Element cl = (Element) iClues.next();
			if (clueNum != -1) {
				weights[clueNum] = Float.parseFloat(cl.getAttributeValue("weight"));
			}
			++i;
		}
		me.setWeights(weights);
		return me;
	}

	public void saveMachineLearner(Element e) {
		e.setAttribute("trainingIterations", String.valueOf(ml.getTrainingIterations()));
	}

	public void saveClue(Element e, int clueNum) {
		e.setAttribute("weight", String.valueOf(ml.getWeights()[clueNum]));
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.xmlconf.MlModelConf#getExtensionPointId()
	 */
	public String getExtensionPointId() {
		return "com.choicemaker.cm.ml.me.base.me";
	}
}
