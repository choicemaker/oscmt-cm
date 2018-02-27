package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cms.util.EvaluatedPairAssist;
import com.choicemaker.util.Precondition;

public class TransitiveGroupBean<T extends Comparable<T> & Serializable>
		implements TransitiveGroup<T> {

	private static final long serialVersionUID = 271L;
	private final DataAccessObject<T> q;
	
	// Unmodifiable; see constructor
	private final List<EvaluatedPair<T>> pairs;
	
	// Unmodifiable; see constructor
	private final List<MergeGroup<T>> mergeGroups;

	/**
	 * Creates an empty TransitiveGroup instance; that is, a query record that
	 * is not linked to any other record by match or hold relationships.
	 */
	public TransitiveGroupBean(DataAccessObject<T> q) {
		this(q, Collections.emptyList(), Collections.emptyList());
	}

	/**
	 * Creates a TransitiveGroup instance with the specified pairs and merge
	 * groups.
	 * 
	 * @param q
	 *            a non-null record holder
	 * @param pairs
	 *            a non-null, but possibly empty, list of EvaluatedPair
	 *            instances between the query record and candidate records.
	 * @param mergeGroups
	 *            a non-null list of merge group. Every member of a group must
	 *            be a member of some pair in the <code>pairs</code> list
	 */
	public TransitiveGroupBean(DataAccessObject<T> q,
			List<EvaluatedPair<T>> pairs, List<MergeGroup<T>> mergeGroups) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null pairs", pairs);
		this.q = q;

		List<EvaluatedPair<T>> list0 = new ArrayList<>();
		list0.addAll(pairs);
		this.pairs = Collections.unmodifiableList(list0);

		boolean assertionsEnabled = false;
		assert assertionsEnabled = true;
		if (assertionsEnabled) {
			for (EvaluatedPair<T> pair : this.pairs) {
				assert pair != null;
			}
		}

		List<MergeGroup<T>> list1 = new ArrayList<>();
		list1.addAll(mergeGroups);
		this.mergeGroups = Collections.unmodifiableList(list1);

		if (assertionsEnabled) {
			for (MergeGroup<T> mergeGroup : this.mergeGroups) {
				assert mergeGroup != null;
			}
		}

		// Check that every merge candidate is a member of some match pair
		if (assertionsEnabled) {
			List<DataAccessObject<T>> list2 = new ArrayList<>();
			for (MergeGroup<T> mergeGroup : this.mergeGroups) {
				List<DataAccessObject<T>> mergeRecords =
					mergeGroup.getRecords();
				list2.addAll(mergeRecords);
			}
			List<DataAccessObject<T>> list3 =
				EvaluatedPairAssist.extractRecordsFromPairs(this.pairs);
			assert list3.containsAll(list2);
		}
	}

	public DataAccessObject<T> getQueryRecord() {
		return q;
	}

	public List<EvaluatedPair<T>> getEvaluatedPairs() {
		return pairs;
	}

	public List<MergeGroup<T>> getMergeGroups() {
		return mergeGroups;
	}

	@Override
	public String toString() {
		return "TransitiveCandidatesBean [q=" + q + ", pairs:" + pairs.size()
				+ ", merges:" + mergeGroups.size() + "]";
	}

	@Override
	public EvaluatedPair<T> getEvaluatedPair(DataAccessObject<T> candidate) {
		// TODO stub
		throw new Error("not yet implemented");
	}

	@Override
	public MergeGroup<T> getMergeGroup(DataAccessObject<T> record) {
		// TODO stub
		throw new Error("not yet implemented");
	}

	@Override
	public MergeGroup<T> getMergeGroup(String mergeGroupId) {
		// TODO stub
		throw new Error("not yet implemented");
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((mergeGroups == null) ? 0 : mergeGroups.hashCode());
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
//		@SuppressWarnings("rawtypes")
//		TransitiveGroupBean other = (TransitiveGroupBean) obj;
//		if (mergeGroups == null) {
//			if (other.mergeGroups != null)
//				return false;
//		} else if (!mergeGroups.equals(other.mergeGroups))
//			return false;
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
