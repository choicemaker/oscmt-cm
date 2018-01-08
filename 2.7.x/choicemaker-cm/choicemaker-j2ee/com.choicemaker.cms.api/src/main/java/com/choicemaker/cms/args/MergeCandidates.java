package com.choicemaker.cms.args;

import java.util.List;

import com.choicemaker.cm.args.IGraphProperty;
import com.choicemaker.cm.core.Record;

public interface MergeCandidates<T extends Comparable<T>> {
	IGraphProperty getGraphConnectivity();
	List<Record<T>> getRecords();
	List<EvaluatedPair<T>> getPairs();
}
