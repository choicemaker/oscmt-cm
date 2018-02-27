package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MatchGroup<T extends Comparable<T> & Serializable> {

	/**
	 * Returns the query record against which the candidate records are
	 * compared.
	 */
	DataAccessObject<T> getQueryRecord();
	
	List<DataAccessObject<T>> getCandidateRecords();

	/**
	 * Returns a list of all EvaluatedPairs between the query record and
	 * the candidate records.
	 */
	List<EvaluatedPair<T>> getEvaluatedPairs();

}