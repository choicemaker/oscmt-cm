package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MergeGroup<T extends Comparable<T> & Serializable>
		extends Serializable {

	/** Returns a unique identifier for the group */
	String getGroupId();

	/** Returns the match connectivity criteria for the group */
	IGraphProperty getGraphConnectivity();

	/** Checks whether a record belongs to this instance */
	boolean containsRecord(DataAccessObject<T> record);

	/** Returns the records in the merge group */
	List<DataAccessObject<T>> getGroupRecords();

	/**
	 * Returns the evaluated match and hold relationships between records in the
	 * group.
	 */
	List<EvaluatedPair<T>> getGroupPairs();

	/**
	 * Returns the match or hold relationship between two records in the group.
	 * If either record is not in the group, an illegal argument exception is
	 * thrown.
	 */
	EvaluatedPair<T> getGroupPair(DataAccessObject<T> r1,
			DataAccessObject<T> r2);

}
