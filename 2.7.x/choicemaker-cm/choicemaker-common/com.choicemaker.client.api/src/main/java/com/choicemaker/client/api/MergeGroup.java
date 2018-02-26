package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MergeCandidates<T extends Comparable<T> & Serializable> extends Serializable {
	IGraphProperty getGraphConnectivity();
	List<DataAccessObject<T>> getRecords();
	List<EvaluatedPair<T>> getPairs();
}
