package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MergeCandidates;
import com.choicemaker.util.Precondition;

public class MergeCandidatesBean<T extends Comparable<T> & Serializable>
		implements MergeCandidates<T> {

	private static final long serialVersionUID = 271L;

	private final IGraphProperty mergeConnectivity;
	private final List<DataAccessObject<T>> records;
	private final List<EvaluatedPair<T>> pairs;

	public MergeCandidatesBean(IGraphProperty mergeConnectivity,
			List<DataAccessObject<T>> records, List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null merge connectivity", mergeConnectivity);
		Precondition.assertNonNullArgument("null records", records);
		Precondition.assertNonNullArgument("null pairs", pairs);
		
		this.mergeConnectivity = mergeConnectivity;
		this.records = new ArrayList<>();
		this.records.addAll(records);
		this.pairs = new ArrayList<>();
		this.pairs.addAll(pairs);
	}

	@Override
	public IGraphProperty getGraphConnectivity() {
		return this.mergeConnectivity;
	}

	@Override
	public List<DataAccessObject<T>> getRecords() {
		return Collections.unmodifiableList(this.records);
	}

	@Override
	public List<EvaluatedPair<T>> getPairs() {
		return Collections.unmodifiableList(this.pairs);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mergeConnectivity == null) ? 0
				: mergeConnectivity.hashCode());
		result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
		result = prime * result + ((records == null) ? 0 : records.hashCode());
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
		MergeCandidatesBean other = (MergeCandidatesBean) obj;
		if (mergeConnectivity == null) {
			if (other.mergeConnectivity != null)
				return false;
		} else if (!mergeConnectivity.equals(other.mergeConnectivity))
			return false;
		if (pairs == null) {
			if (other.pairs != null)
				return false;
		} else if (!pairs.equals(other.pairs))
			return false;
		if (records == null) {
			if (other.records != null)
				return false;
		} else if (!records.equals(other.records))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MergeCandidatesBean [mergeConnectivity=" + mergeConnectivity
				+ ", records:" + records.size() + ", pairs:" + pairs.size() + "]";
	}

}
