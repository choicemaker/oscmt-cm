package com.choicemaker.cms.ejb;

import java.io.Serializable;

import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.Evaluator;

public class TestEvaluator extends Evaluator {

	public <T extends Comparable<T> & Serializable> TestEvaluator(
			TestModel<T> model) {
		super(model);
	}

	@Override
	public float getProbability(ActiveClues a) {
		return 0;
	}

	@Override
	public String getSignature() {
		return null;
	}

}
