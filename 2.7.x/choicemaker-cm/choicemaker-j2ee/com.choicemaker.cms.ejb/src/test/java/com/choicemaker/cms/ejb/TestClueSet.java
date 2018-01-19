package com.choicemaker.cms.ejb;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.BooleanActiveClues;
import com.choicemaker.cm.core.ClueDesc;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.ClueSetType;
import com.choicemaker.cm.core.Record;
import com.choicemaker.util.Precondition;

public class TestClueSet<T extends Comparable<T> & Serializable>
		implements ClueSet {

	public static final int IDX_DIFFER = 0;
	public static final String NAME_DIFFER = "dFakeDiffer";
	public static final int IDX_MATCH = 1;
	public static final String NAME_MATCH = "mFakeMatch";
	private static final boolean isRule = false;
	private static int startLineNumber = 0;
	private static int endLineNumber = 0;
	public static int DECISION_DOMAIN_SIZE = 2;

	private static final ClueDesc[] clueDescriptions = new ClueDesc[] {
			new ClueDesc(IDX_DIFFER, NAME_DIFFER, Decision.DIFFER, isRule,
					ClueDesc.NONE, startLineNumber, endLineNumber),
			new ClueDesc(IDX_MATCH, NAME_MATCH, Decision.DIFFER, isRule,
					ClueDesc.NONE, startLineNumber, endLineNumber) };

	private static final boolean[] cluesToEvaluate = new boolean[] {
			true, true };

	public static final boolean[] getCluesToEvaluate() {
		return cluesToEvaluate.clone();
	}

	private final Map<SafeIndexPair<T>, EvaluatedPair<T>> map;

	public TestClueSet(TestModel<T> m) {
		Precondition.assertNonNullArgument(m);
		Map<SafeIndexPair<T>, EvaluatedPair<T>> _map = new HashMap<>();
		List<EvaluatedPair<T>> pairs = m.getKnownPairs();
		assert pairs != null;
		for (int i = 0; i < pairs.size(); i++) {
			EvaluatedPair<T> p = pairs.get(i);
			Precondition.assertNonNullArgument("null pairs at index " + i, p);
			assert p.getQueryRecord() != null;
			SafeIndex<T> qId = new SafeIndex<T>(p.getQueryRecord().getId());
			assert p.getMatchCandidate() != null;
			SafeIndex<T> mId = new SafeIndex<T>(p.getMatchCandidate().getId());
			SafeIndexPair<T> key = new SafeIndexPair<T>(qId, mId);
			EvaluatedPair<T> previous = _map.put(key, p);
			Precondition.assertBoolean("Duplicate pair at index " + i,
					previous == null);
		}
		this.map = Collections.unmodifiableMap(_map);
	}

	@Override
	public ClueSetType getType() {
		return com.choicemaker.cm.core.ClueSetType.BOOLEAN;
	}

	@Override
	public boolean hasDecision() {
		return true;
	}

	@Override
	public int size() {
		return clueDescriptions.length;
	}

	@Override
	public int size(Decision d) {
		int retVal;
		if (Decision.MATCH.equals(d)) {
			retVal = 1;
		} else if (Decision.DIFFER.equals(d)) {
			retVal = 1;
		} else {
			retVal = 0;
		}
		return retVal;
	}

	@Override
	public ClueDesc[] getClueDesc() {
		ClueDesc[] retVal = clueDescriptions.clone();
		return retVal;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ActiveClues getActiveClues(Record q, Record m, boolean[] ignored) {
		@SuppressWarnings("unchecked")
		SafeIndex<T> id1 = new SafeIndex<T>((T) q.getId());
		@SuppressWarnings("unchecked")
		SafeIndex<T> id2 = new SafeIndex<T>((T) m.getId());
		SafeIndexPair<T> key = new SafeIndexPair<T>(id1,id2);
		EvaluatedPair<T> ep = map.get(key);
		
		BooleanActiveClues retVal = new BooleanActiveClues();
		if (Decision.MATCH.equals(ep.getMatchDecision())) {
			retVal.add(TestClueSet.IDX_MATCH, ClueDesc.NONE);
		} else if (Decision.HOLD.equals(ep.getMatchDecision())) {
			retVal.add(TestClueSet.IDX_MATCH, ClueDesc.NONE);
			retVal.add(TestClueSet.IDX_DIFFER, ClueDesc.NONE);
		} else {
			retVal.add(TestClueSet.IDX_DIFFER, ClueDesc.NONE);
		}
		
		return retVal;
	}

}
