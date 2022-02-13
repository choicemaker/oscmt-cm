/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cms.util.EvaluatedPairAssist;

public class TransitiveGroupBean<T extends Comparable<T> & Serializable>
		extends MatchGroupBean<T> implements TransitiveGroup<T> {

	private static final long serialVersionUID = 271L;

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
			List<QueryCandidatePair<T>> pairs,
			List<MergeGroup<T>> mergeGroups) {
		super(q, pairs);

		List<MergeGroup<T>> list1;
		if (mergeGroups != null) {
			list1 = new ArrayList<>();
			list1.addAll(mergeGroups);
		} else {
			list1 = Collections.emptyList();
		}
		this.mergeGroups = Collections.unmodifiableList(list1);

		boolean assertionsEnabled = false;
		assert assertionsEnabled = true;

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
					mergeGroup.getGroupRecords();
				list2.addAll(mergeRecords);
			}
			List<DataAccessObject<T>> list3 =
				EvaluatedPairAssist.extractRecordsFromPairs(pairs);
			assert list3.containsAll(list2);
		}
	}

	@Override
	public List<MergeGroup<T>> getMergeGroups() {
		return mergeGroups;
	}

	@Override
	public String toString() {
		return "TransitiveCandidatesBean [groupId=" + getGroupId() + ", q="
				+ getQueryRecord() + ", pairs:"
				+ getQueryCandidatePairs().size() + ", merges:"
				+ mergeGroups.size() + "]";
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

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result
	// + ((mergeGroups == null) ? 0 : mergeGroups.hashCode());
	// result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
	// result = prime * result + ((q == null) ? 0 : q.hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// @SuppressWarnings("rawtypes")
	// TransitiveGroupBean other = (TransitiveGroupBean) obj;
	// if (mergeGroups == null) {
	// if (other.mergeGroups != null)
	// return false;
	// } else if (!mergeGroups.equals(other.mergeGroups))
	// return false;
	// if (pairs == null) {
	// if (other.pairs != null)
	// return false;
	// } else if (!pairs.equals(other.pairs))
	// return false;
	// if (q == null) {
	// if (other.q != null)
	// return false;
	// } else if (!q.equals(other.q))
	// return false;
	// return true;
	// }

}
