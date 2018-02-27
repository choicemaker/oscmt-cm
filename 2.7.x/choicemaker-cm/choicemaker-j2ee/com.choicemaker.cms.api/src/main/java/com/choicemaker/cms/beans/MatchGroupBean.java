package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.cms.util.EvaluatedPairAssist;
import com.choicemaker.util.Precondition;

public class MatchGroupBean<T extends Comparable<T> & Serializable>
		implements Serializable, MatchGroup<T> {

	private static final long serialVersionUID = 271L;

	private final DataAccessObject<T> q;

	// Unmodifiable; see constructor
	private final List<EvaluatedPair<T>> pairs;

	public MatchGroupBean(DataAccessObject<T> q, List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null pairs", pairs);
		this.q = q;
		List<EvaluatedPair<T>> list = new ArrayList<>();
		list.addAll(pairs);
		this.pairs = Collections.unmodifiableList(list);
		
		boolean assertionsEnabled = false;
		assert assertionsEnabled = true;
		if (assertionsEnabled) {
			for (EvaluatedPair<T> pair : this.pairs) {
				assert pair != null;
			}
		}
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
	public List<EvaluatedPair<T>> getEvaluatedPairs() {
		return pairs;
	}

	@Override
	public String toString() {
		return "MatchCandidates [q=" + q.getId() + ", pairs.size()="
				+ pairs.size() + "]";
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
//		result = prime * result + ((q == null) ? 0 : q.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		@SuppressWarnings("unchecked")
//		MatchGroupBean<T> other = (MatchGroupBean<T>) obj;
//		if (pairs == null) {
//			if (other.pairs != null)
//				return false;
//		} else if (!pairs.equals(other.pairs))
//			return false;
//		if (q == null) {
//			if (other.q != null)
//				return false;
//		} else if (!q.equals(other.q))
//			return false;
//		return true;
//	}

}
