package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.core.Record;

public interface MatchCandidateGroup<T extends Comparable<T>> {
	Record<T> getQueryRecord();

	List<EvaluatedPair<T>> getEvaluatedPairs();
}
