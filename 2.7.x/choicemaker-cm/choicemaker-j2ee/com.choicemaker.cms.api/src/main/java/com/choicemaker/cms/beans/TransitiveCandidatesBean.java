package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.MergeCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.util.Precondition;

public class TransitiveCandidatesBean<T extends Comparable<T> & Serializable>
		implements TransitiveCandidates<T> {

	private static final long serialVersionUID = 271L;
	private final DataAccessObject<T> q;
	private final List<EvaluatedPair<T>> pairs;
	private final List<MergeCandidates<T>> mergeGroups;

	/**
	 * Creates an empty TransitiveCandidates instance; that is, query record
	 * that is not linked to any other record by match or hold relationships.
	 */
	public TransitiveCandidatesBean(DataAccessObject<T> q) {
		this(q, Collections.emptyList(), Collections.emptyList());
	}

	/**
	 * Creates a TransitiveCandidates instance with the specified pairs and
	 * merge groups.
	 * 
	 * @param q
	 *            a non-null record holder
	 * @param pairs
	 *            a non-null, but possibly empty, list of EvaluatedPair
	 *            instances.
	 * @param mergeGroups
	 *            a non-null list of merge candidates. Every candidate must be a
	 *            member of some pair in the <code>pairs</code> list
	 */
	public TransitiveCandidatesBean(DataAccessObject<T> q,
			List<EvaluatedPair<T>> pairs,
			List<MergeCandidates<T>> mergeGroups) {
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

		List<MergeCandidates<T>> _mgs = new ArrayList<>(mergeGroups.size());
		for (int i = 0; i < mergeGroups.size(); i++) {
			MergeCandidates<T> p = mergeGroups.get(i);
			Precondition.assertNonNullArgument("null merge group at " + i, p);
			_mgs.add(p);
		}
		this.mergeGroups = Collections.unmodifiableList(_mgs);

		// FIXME check that every merge candidate is a member of some match pair
	}

	public DataAccessObject<T> getQueryRecord() {
		return q;
	}

	public List<EvaluatedPair<T>> getEvaluatedPairs() {
		return pairs;
	}

	public List<MergeCandidates<T>> getMergeCandidates() {
		return mergeGroups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mergeGroups == null) ? 0 : mergeGroups.hashCode());
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
		@SuppressWarnings("rawtypes")
		TransitiveCandidatesBean other = (TransitiveCandidatesBean) obj;
		if (mergeGroups == null) {
			if (other.mergeGroups != null)
				return false;
		} else if (!mergeGroups.equals(other.mergeGroups))
			return false;
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
		return "TransitiveCandidatesBean [q=" + q + ", pairs:" + pairs.size()
				+ ", merges:" + mergeGroups.size() + "]";
	}


}
