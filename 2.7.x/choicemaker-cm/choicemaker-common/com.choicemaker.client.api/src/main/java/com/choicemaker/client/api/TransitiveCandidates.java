package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface TransitiveCandidates<T extends Comparable<T> & Serializable> extends Serializable {

	DataAccessObject<T> getQueryRecord();

	List<EvaluatedPair<T>> getEvaluatedPairs();

	List<MergeCandidates<T>> getMergeCandidates();
}
