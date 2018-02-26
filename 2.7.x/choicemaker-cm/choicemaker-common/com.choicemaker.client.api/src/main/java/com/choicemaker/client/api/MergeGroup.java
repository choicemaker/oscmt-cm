package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MergeGroup<T extends Comparable<T> & Serializable>
		extends Serializable {
	
	/** Returns a unique identifier for the group */
//	String getMergeGroupId();
	
	/** Returns the match connectivity criteria for the group */
	IGraphProperty getGraphConnectivity();

	/** Returns the records in the merge group */
	List<DataAccessObject<T>> getRecords();

	/**
	 * Returns the evaluated match and hold relationships between records
	 * in the group.
	 */
	List<EvaluatedPair<T>> getPairs();
	
	/**
	 * Returns the match or hold relationship between two records in the
	 * group. If either record is not in the group, an illegal argument
	 * exception is thrown.
	 */
//	EvaluatedPair<T> getEvaluatedPair(DataAccessObject<T> r1, DataAccessObject<T> r2);

}
