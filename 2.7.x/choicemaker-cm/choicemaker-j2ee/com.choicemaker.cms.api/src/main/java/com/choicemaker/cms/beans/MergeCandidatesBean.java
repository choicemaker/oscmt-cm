package com.choicemaker.cms.beans;

import java.io.Serializable;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MergeCandidates;

public class MergeCandidatesBean<T extends Comparable<T> & Serializable> implements MergeCandidates<T> {

	private static final long serialVersionUID =271L;

	@Override
	public IGraphProperty getGraphConnectivity() {
		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

	@Override
	public List<DataAccessObject<T>> getRecords() {
		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

	@Override
	public List<EvaluatedPair<T>> getPairs() {
		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

}
