package com.choicemaker.cms.args;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.util.Precondition;

public final class TransitiveCandidates<T extends Comparable<T> & Serializable> {

	private final RemoteRecord<T> q;
	private final List<EvaluatedPair<T>> pairs;
	private final List<MergeCandidates<T>> mergeGroups;

	public TransitiveCandidates(RemoteRecord<T> q, List<EvaluatedPair<T>> pairs,
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
	}

	RemoteRecord<T> getQueryRecord() {
		return q;
	}

	List<EvaluatedPair<T>> getEvaluatedPairs() {
		return pairs;
	}

	List<MergeCandidates<T>> getMergeCandidates() {
		return mergeGroups;
	}
}
