package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MatchGroup<T extends Comparable<T> & Serializable>
		extends Serializable {

	/** @return a unique identifier for the group */
	String getGroupId();

	/**
	 * @return the query record against which the candidate records are
	 * compared.
	 */
	DataAccessObject<T> getQueryRecord();

	List<DataAccessObject<T>> getCandidateRecords();

	/**
	 * @return a list of all EvaluatedPairs between the query record and the
	 * candidate records.
	 */
	List<QueryCandidatePair<T>> getQueryCandidatePairs();

	/**
	 * @param candidate a non-null instance
	 * @return the EvaluatedPair between the query record and the specified
	 * candidate record.
	 */
	QueryCandidatePair<T> getQueryCandidatePair(DataAccessObject<T> candidate);
}
