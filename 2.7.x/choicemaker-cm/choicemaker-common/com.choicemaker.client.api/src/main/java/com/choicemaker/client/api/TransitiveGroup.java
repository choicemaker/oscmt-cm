package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface TransitiveGroup<T extends Comparable<T> & Serializable> extends Serializable {

	/**
	 * Returns the query record against which the candidate records are
	 * compared.
	 */
	DataAccessObject<T> getQueryRecord();
	
	/**
	 * Returns the EvaluatedPair between the query record and the specified
	 * candidate record.
	 */
//	EvaluatedPair<T> getEvaluatedPair(DataAccessObject<T> candidate);
	
	/**
	 * Returns a list of all EvaluatedPairs between the query record and
	 * the candidate records.
	 */
	List<EvaluatedPair<T>> getEvaluatedPairs();

	/**
	 * Returns a list of merge candidates formed by the query record and
	 * candidate records.
	 */
	List<MergeGroup<T>> getMergeCandidates();
	
	/**
	 * Returns the MergeCandidate to which the specified record belongs.
	 * The specified record may be the query record or any candidate record.
	 * If the specified record does not belong to a merge candidate, or if
	 * the specified record is not the query record or a candidate record, a
	 * null merge candidate is returned.
	 */
//	MergeGroup<T> getMergeCandidate(DataAccessObject<T> record);
	
}

