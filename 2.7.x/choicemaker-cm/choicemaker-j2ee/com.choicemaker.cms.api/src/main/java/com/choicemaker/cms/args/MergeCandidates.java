package com.choicemaker.cms.args;

import java.io.Serializable;
import java.util.List;

import com.choicemaker.cm.args.IGraphProperty;

public interface MergeCandidates<T extends Comparable<T> & Serializable> {
	IGraphProperty getGraphConnectivity();
	List<RemoteRecord<T>> getRecords();
	List<EvaluatedPair<T>> getPairs();
}
