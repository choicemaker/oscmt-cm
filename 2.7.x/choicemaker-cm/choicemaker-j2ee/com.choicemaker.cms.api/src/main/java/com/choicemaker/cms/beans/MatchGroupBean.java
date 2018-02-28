package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.cms.util.EvaluatedPairAssist;
import com.choicemaker.util.Precondition;

public class MatchGroupBean<T extends Comparable<T> & Serializable>
		implements Serializable, MatchGroup<T> {

	private static final long serialVersionUID = 271L;

	private final String groupId;
	private final DataAccessObject<T> q;

	// Unmodifiable; see constructor
	private final Map<T, QueryCandidatePair<T>> pairMap;

	public MatchGroupBean(DataAccessObject<T> q,
			List<QueryCandidatePair<T>> pairs) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null pairs", pairs);

		this.groupId = UUID.randomUUID().toString();
		this.q = q;
		final T qId = q.getId();
		Map<T, QueryCandidatePair<T>> map = new HashMap<>();
		for (int i = 0; i < pairs.size(); i++) {
			QueryCandidatePair<T> pair = pairs.get(i);
			Precondition.assertNonNullArgument("null pair at index " + i, pair);
			DataAccessObject<T> query = pair.getQueryRecord();
			Precondition.assertNonNullArgument("null query for pair " + i,
					query);
			T queryId = query.getId();
			Precondition.assertBoolean("inconsistent query id for pair " + i,
					(qId == null && queryId == null)
							|| (qId != null && qId.equals(queryId)));
			DataAccessObject<T> candidate = pair.getMatchCandidate();
			Precondition.assertNonNullArgument("null candidate for pair " + i,
					candidate);
			T candidateId = candidate.getId();
			Precondition.assertNonNullArgument(
					"null candidate id for pair " + i, candidateId);
			assert candidateId != null;
			Precondition.assertBoolean(
					"candidate id equals query id for pair " + i,
					!candidateId.equals(q.getId()));
			QueryCandidatePair<T> existing = map.put(candidateId, pair);
			Precondition.assertBoolean("duplicate candidate id for pair " + i,
					existing == null);
		}
		this.pairMap = Collections.unmodifiableMap(map);
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
		List<QueryCandidatePair<T>> pairs = getQueryCandidatePairs();
		List<DataAccessObject<T>> retVal =
			EvaluatedPairAssist.extractRecordsFromPairs(pairs, q);
		return retVal;
	}

	@Override
	public List<QueryCandidatePair<T>> getQueryCandidatePairs() {
		List<QueryCandidatePair<T>> retVal = new ArrayList<>();
		retVal.addAll(this.pairMap.values());
		return Collections.unmodifiableList(retVal);
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
		Precondition.assertNonNullArgument("null candidate", candidate);
		T id = candidate.getId();
		QueryCandidatePair<T> retVal = id == null ? null : this.pairMap.get(id);
		return retVal;
	}

}
