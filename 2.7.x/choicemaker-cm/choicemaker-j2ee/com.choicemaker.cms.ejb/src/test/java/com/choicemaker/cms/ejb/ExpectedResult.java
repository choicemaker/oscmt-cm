package com.choicemaker.cms.ejb;

import java.io.Serializable;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cms.api.AbaParameters;

/** Hard-coded examples of composite entities and the match and transitivity candidates that they should produce */

public class ExpectedResult<T extends Comparable<T> & Serializable> {

	public static 
	<T extends Comparable<T> & Serializable>
	CompositeEntity<T> createCompositeEntity(DataAccessObject<T> queryRecord, List<EvaluatedPair<T>> pairs) {
		return null;
	}

	private DataAccessObject<T> queryRecord;
	private List<Match> matchList;
	private AbaParameters parameters;
	private IGraphProperty mergeConnectivity;
	boolean mergeGroupContainsQuery;
	
	public DataAccessObject<T> getQueryRecord() {
		// TODO Auto-generated method stub
		return queryRecord;
	}

	public List<Match> getMatchList() {
		// TODO Auto-generated method stub
		return matchList;
	}

	public AbaParameters getAbaParameters() {
		// TODO Auto-generated method stub
		return parameters;
	}

	public IGraphProperty getMergeConnectivity() {
		// TODO Auto-generated method stub
		return mergeConnectivity;
	}
	
	public boolean mergeGroupContainsQuery() {
		return mergeGroupContainsQuery;
	}

	public CompositeEntity<T> getCompositeEntity() {
		// TODO Auto-generated method stub
		return null;
	}

}
