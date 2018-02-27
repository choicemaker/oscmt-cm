package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.cms.util.EvaluatedPairAssist;
import com.choicemaker.cms.util.IdentifiableWrapper;
import com.choicemaker.util.Precondition;

public class MergeGroupBean<T extends Comparable<T> & Serializable>
		implements MergeGroup<T> {

	private static final long serialVersionUID = 271L;

	private final String mergeGroupId;
	private final IGraphProperty mergeConnectivity;

	// Unmodifiable; see constructor
	private final List<EvaluatedPair<T>> pairs;
	
	// Unmodifiable; see constructor
	private final SortedSet<IdentifiableWrapper<T>> wrappers;

	public MergeGroupBean(IGraphProperty mergeConnectivity,
			List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null merge connectivity",
				mergeConnectivity);
		Precondition.assertNonNullArgument("null pairs", pairs);

		this.mergeGroupId = UUID.randomUUID().toString();
		this.mergeConnectivity = mergeConnectivity;
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
		
		this.wrappers = Collections.unmodifiableSortedSet(EvaluatedPairAssist
				.extractWrappedRecordsFromPairs(pairs, null));
	}

	@Override
	public String getMergeGroupId() {
		return mergeGroupId;
	}

	@Override
	public IGraphProperty getGraphConnectivity() {
		return this.mergeConnectivity;
	}
	
	@Override
	public boolean containsRecord(DataAccessObject<T> record) {
		IdentifiableWrapper<T> wrapper= new IdentifiableWrapper<>(record);
		boolean retVal = wrappers.contains(wrapper);
		return retVal;
	}

	@Override
	public List<DataAccessObject<T>> getRecords() {
		List<DataAccessObject<T>> retVal =
			EvaluatedPairAssist.extractRecordsFromWrappedRecords(this.wrappers);
		return retVal;
	}

	@Override
	public List<EvaluatedPair<T>> getEvaluatedPairs() {
		return Collections.unmodifiableList(this.pairs);
	}

	@Override
	public String toString() {
		return "MergeGroupBean [mergeGroupId=" + mergeGroupId
				+ ", mergeConnectivity=" + mergeConnectivity + ", pairs:"
				+ pairs.size() + "]";
	}

	@Override
	public EvaluatedPair<T> getEvaluatedPair(DataAccessObject<T> r1,
			DataAccessObject<T> r2) {
		// TODO stub
		throw new Error("not yet implemented");
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((mergeConnectivity == null) ? 0
	// : mergeConnectivity.hashCode());
	// result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
	// result = prime * result + ((records == null) ? 0 : records.hashCode());
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
	// MergeGroupBean other = (MergeGroupBean) obj;
	// if (mergeConnectivity == null) {
	// if (other.mergeConnectivity != null)
	// return false;
	// } else if (!mergeConnectivity.equals(other.mergeConnectivity))
	// return false;
	// if (pairs == null) {
	// if (other.pairs != null)
	// return false;
	// } else if (!pairs.equals(other.pairs))
	// return false;
	// if (records == null) {
	// if (other.records != null)
	// return false;
	// } else if (!records.equals(other.records))
	// return false;
	// return true;
	// }

}
