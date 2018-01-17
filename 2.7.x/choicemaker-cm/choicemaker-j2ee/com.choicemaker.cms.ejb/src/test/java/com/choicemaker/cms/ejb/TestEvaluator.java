package com.choicemaker.cms.ejb;

import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.Evaluator;

public class TestEvaluator extends Evaluator {
	
	public TestEvaluator(TestModel model) {
		super(model);
	}

	@Override
	public float getProbability(ActiveClues a) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

}
