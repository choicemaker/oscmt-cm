package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.cms.util.EvaluatedPairAssist;
import com.choicemaker.cms.util.IdentifiableComparator;
import com.choicemaker.cms.util.IdentifiablePairKey;
import com.choicemaker.cms.util.IdentifiableWrapper;
import com.choicemaker.util.Precondition;

public class MergeGroupBean<T extends Comparable<T> & Serializable>
		implements MergeGroup<T> {

	private static final long serialVersionUID = 271L;

	private final String groupId;
	private final IGraphProperty mergeConnectivity;
	private final IdentifiableComparator<T> idComparator =
		new IdentifiableComparator<>();

	// Unmodifiable; see constructor
	private final Map<IdentifiablePairKey<T>, EvaluatedPair<T>> pairMap;

	// Unmodifiable; see constructor
	private final SortedSet<IdentifiableWrapper<T>> wrappers;

	public MergeGroupBean(IGraphProperty mergeConnectivity,
			List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null merge connectivity",
				mergeConnectivity);
		Precondition.assertNonNullArgument("null pairs", pairs);

		this.groupId = UUID.randomUUID().toString();
		this.mergeConnectivity = mergeConnectivity;
		Map<IdentifiablePairKey<T>, EvaluatedPair<T>> map = new HashMap<>();
		for (int i = 0; i < pairs.size(); i++) {
			EvaluatedPair<T> pair = pairs.get(i);
			Precondition.assertNonNullArgument("null pair at index " + i, pair);
			DataAccessObject<T> r1 = pair.getRecord1();
			Precondition.assertNonNullArgument("null record1 for pair " + i,
					r1);
			DataAccessObject<T> r2 = pair.getRecord2();
			Precondition.assertNonNullArgument("null record2 for pair " + i,
					r2);
			IdentifiablePairKey<T> key = createKey(r1, r2);
			EvaluatedPair<T> existing = map.put(key, pair);
			Precondition.assertBoolean("duplicate pair " + i, existing == null);
		}
		this.pairMap = Collections.unmodifiableMap(map);

		this.wrappers = Collections.unmodifiableSortedSet(EvaluatedPairAssist
				.extractWrappedRecordsFromPairs(pairs, null));
	}

	protected IdentifiablePairKey<T> createKey(DataAccessObject<T> r1,
			DataAccessObject<T> r2) {
		assert r1 != null;
		assert r2 != null;
		IdentifiablePairKey<T> key;
		if (idComparator.compare(r1, r2) <= 0) {
			key = new IdentifiablePairKey<>(r1, r2);
		} else {
			key = new IdentifiablePairKey<>(r2, r1);
		}
		return key;
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
		List<EvaluatedPair<T>> retVal = new ArrayList<>();
		retVal.addAll(this.pairMap.values());
		return Collections.unmodifiableList(retVal);
	}

	@Override
	public String toString() {
		return "MergeGroupBean [mergeGroupId=" + groupId
				+ ", mergeConnectivity=" + mergeConnectivity + ", pairs:"
				+ pairMap.size() + "]";
	}

	@Override
	public EvaluatedPair<T> getGroupPair(DataAccessObject<T> r1,
			DataAccessObject<T> r2) {
		Precondition.assertNonNullArgument("null record1", r1);
		Precondition.assertNonNullArgument("null record2", r2);
		IdentifiablePairKey<T> key = createKey(r1, r2);
		EvaluatedPair<T> retVal = this.pairMap.get(key);
		return retVal;
	}

}
