package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.cms.util.EvaluatedPairAssist;
import com.choicemaker.util.Precondition;

public class MatchGroupBean<T extends Comparable<T> & Serializable>
		implements Serializable, MatchGroup<T> {

	private static final long serialVersionUID = 271L;

	private final String groupId;
	private final DataAccessObject<T> q;

	// Unmodifiable; see constructor
	private final List<QueryCandidatePair<T>> pairs;

	public MatchGroupBean(DataAccessObject<T> q, List<QueryCandidatePair<T>> pairs) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null pairs", pairs);

		this.groupId = UUID.randomUUID().toString();
		this.q = q;
		List<QueryCandidatePair<T>> list = new ArrayList<>();
		list.addAll(pairs);
		this.pairs = Collections.unmodifiableList(list);

		boolean assertionsEnabled = false;
		assert assertionsEnabled = true;
		if (assertionsEnabled) {
			for (QueryCandidatePair<T> pair : this.pairs) {
				assert pair != null;
			}
		}
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public DataAccessObject<T> getQueryRecord() {
		return q;
	}

	@Override
	public List<DataAccessObject<T>> getCandidateRecords() {
		List<DataAccessObject<T>> retVal =
			EvaluatedPairAssist.extractRecordsFromPairs(pairs, q);
		return retVal;
	}

	@Override
	public List<QueryCandidatePair<T>> getQueryCandidatePairs() {
		return pairs;
	}

	@Override
	public String toString() {
		return "MatchCandidates [groupId=" + getGroupId() + ", q="
				+ getQueryRecord() + ", pairs:"
				+ getQueryCandidatePairs().size() + "]";
	}

	@Override
	public QueryCandidatePair<T> getQueryCandidatePair(
			DataAccessObject<T> candidate) {
		// TODO stub
		throw new Error("not yet implemented");
	}

}
