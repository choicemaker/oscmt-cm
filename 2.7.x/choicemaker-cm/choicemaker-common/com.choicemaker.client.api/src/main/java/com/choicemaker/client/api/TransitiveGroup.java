package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface TransitiveGroup<T extends Comparable<T> & Serializable> extends
	MatchGroup<T> {

	
	/**
	 * Returns a list of merge groups formed by the query record and
	 * candidate records.
	 */
	List<MergeGroup<T>> getMergeGroups();
	
	/**
	 * Returns the merge group to which the specified record belongs.
	 * The specified record may be the query record or any candidate record.
	 * If the specified record does not belong to a merge candidate, or if
	 * the specified record is not the query record or a candidate record, a
	 * null merge candidate is returned.
	 */
	MergeGroup<T> getMergeGroup(DataAccessObject<T> record);
	
	/**
	 * Returns the merge group with the specified merge group id, or null
	 * if no such group exists.
	 */
	MergeGroup<T> getMergeGroup(String mergeGroupId);
	
}

