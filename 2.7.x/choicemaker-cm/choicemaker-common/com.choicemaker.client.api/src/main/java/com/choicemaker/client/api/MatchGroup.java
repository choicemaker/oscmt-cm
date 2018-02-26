package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MatchGroup<T extends Comparable<T> & Serializable> {

	DataAccessObject<T> getQueryRecord();

	List<EvaluatedPair<T>> getEvaluatedPairs();

}