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

}
