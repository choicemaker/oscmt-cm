package com.choicemaker.cms.api;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.Record;

public interface EvaluatedPair<T extends Comparable<T>> {
	Record<T> getQueryRecord();
	Record<T> getMatchCandidate();
	float getMatchProbability();
	Decision getMatchDecision();
}
