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

	private final String groupId;
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

		this.groupId = UUID.randomUUID().toString();
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
	public String getGroupId() {
		return groupId;
	}

	@Override
	public IGraphProperty getGraphConnectivity() {
		return this.mergeConnectivity;
	}

	@Override
	public boolean containsRecord(DataAccessObject<T> record) {
		IdentifiableWrapper<T> wrapper = new IdentifiableWrapper<>(record);
		boolean retVal = wrappers.contains(wrapper);
		return retVal;
	}

	@Override
	public List<DataAccessObject<T>> getGroupRecords() {
		List<DataAccessObject<T>> retVal =
			EvaluatedPairAssist.extractRecordsFromWrappedRecords(this.wrappers);
		return retVal;
	}

	@Override
	public List<EvaluatedPair<T>> getGroupPairs() {
		return Collections.unmodifiableList(this.pairs);
	}

	@Override
	public String toString() {
		return "MergeGroupBean [mergeGroupId=" + groupId
				+ ", mergeConnectivity=" + mergeConnectivity + ", pairs:"
				+ pairs.size() + "]";
	}

	@Override
	public EvaluatedPair<T> getGroupPair(DataAccessObject<T> r1,
			DataAccessObject<T> r2) {
		// TODO stub
		throw new Error("not yet implemented");
	}

}
