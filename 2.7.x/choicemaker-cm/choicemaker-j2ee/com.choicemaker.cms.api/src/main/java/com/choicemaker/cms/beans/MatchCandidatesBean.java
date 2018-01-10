package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.util.Precondition;

public class MatchCandidatesBean<T extends Comparable<T> & Serializable>
		implements Serializable, MatchCandidates<T> {

	private static final long serialVersionUID = 271L;

	private final DataAccessObject<T> q;
	private final List<EvaluatedPair<T>> pairs;

	public MatchCandidatesBean(DataAccessObject<T> q, List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null pairs", pairs);
		this.q = q;
		List<EvaluatedPair<T>> _ps = new ArrayList<>(pairs.size());
		for (int i = 0; i < pairs.size(); i++) {
			EvaluatedPair<T> p = pairs.get(i);
			Precondition.assertNonNullArgument("null pair at " + i, p);
			_ps.add(p);
		}
		this.pairs = Collections.unmodifiableList(_ps);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cms.args.MatchCandidates#getQueryRecord()
	 */
	@Override
	public DataAccessObject<T> getQueryRecord() {
		return q;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cms.args.MatchCandidates#getEvaluatedPairs()
	 */
	@Override
	public List<EvaluatedPair<T>> getEvaluatedPairs() {
		return pairs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
		result = prime * result + ((q == null) ? 0 : q.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		MatchCandidatesBean<T> other = (MatchCandidatesBean<T>) obj;
		if (pairs == null) {
			if (other.pairs != null)
				return false;
		} else if (!pairs.equals(other.pairs))
			return false;
		if (q == null) {
			if (other.q != null)
				return false;
		} else if (!q.equals(other.q))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MatchCandidates [q=" + q.getId() + ", pairs.size()="
				+ pairs.size() + "]";
	}

}
