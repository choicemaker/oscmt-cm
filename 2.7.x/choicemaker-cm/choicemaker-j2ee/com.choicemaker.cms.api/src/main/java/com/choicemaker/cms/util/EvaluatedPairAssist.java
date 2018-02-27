package com.choicemaker.cms.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.util.Precondition;

public class EvaluatedPairAssist {

	public static <T extends Comparable<T> & Serializable>
	List<DataAccessObject<T>> extractRecordsFromPairs(
			List<EvaluatedPair<T>> pairs) {
		return extractRecordsFromPairs(pairs,null);
	}
	
	public static <T extends Comparable<T> & Serializable>
	List<DataAccessObject<T>> extractRecordsFromPairs(
					List<EvaluatedPair<T>> pairs,
					DataAccessObject<T> excluded) {
		Precondition.assertNonNullArgument("null pairs", pairs);

		SortedSet<IdentifiableWrapper<T>> wrappers =
			extractWrappedRecordsFromPairs(pairs, excluded);
		List<DataAccessObject<T>> retVal =
			extractRecordsFromWrappedRecords(wrappers);

		return retVal;
	}

	public static <T extends Comparable<T> & Serializable>
	List<DataAccessObject<T>> extractRecordsFromWrappedRecords(
			SortedSet<IdentifiableWrapper<T>> wrappers) {
		Precondition.assertNonNullArgument("null wrappers", wrappers);

		List<DataAccessObject<T>> retVal = new ArrayList<>();
		for (IdentifiableWrapper<T> wrapper : wrappers) {
			DataAccessObject<T> record =
				(DataAccessObject<T>) wrapper.getWrapped();
			retVal.add(record);
		}

		return retVal;
	}

	public static <T extends Comparable<T> & Serializable>
	SortedSet<IdentifiableWrapper<T>> extractWrappedRecordsFromPairs(
					List<EvaluatedPair<T>> pairs,
					DataAccessObject<T> excluded) {
		Precondition.assertNonNullArgument("null pairs", pairs);

		SortedSet<IdentifiableWrapper<T>> wrappers = new TreeSet<>();
		for (EvaluatedPair<T> pair : pairs) {
			IdentifiableWrapper<T> wrapper;
			wrapper = new IdentifiableWrapper<>(pair.getQueryRecord());
			wrappers.add(wrapper);
			wrapper = new IdentifiableWrapper<>(pair.getMatchCandidate());
			wrappers.add(wrapper);
		}
		if (excluded != null) {
			IdentifiableWrapper<T> excludedWrapper =
					excluded == null ? null : new IdentifiableWrapper<>(excluded);
			wrappers.remove(excludedWrapper);
		}

		return wrappers;
	}

	private EvaluatedPairAssist() {
	}

}
