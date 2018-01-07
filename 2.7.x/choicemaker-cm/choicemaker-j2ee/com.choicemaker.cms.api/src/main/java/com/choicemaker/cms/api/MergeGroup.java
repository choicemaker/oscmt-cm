package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.args.IGraphProperty;
import com.choicemaker.cm.core.Record;

public interface MergeGroup<T extends Comparable<T>> {
	IGraphProperty getConnectivity();
	List<Record<T>> getRecords();
	List<EvaluatedPair<T>> getPairs();
}
