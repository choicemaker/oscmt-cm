package com.choicemaker.cms.ejb;

import java.io.Serializable;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
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
	
	public DataAccessObject<T> getInputQueryRecord() {
		// TODO Auto-generated method stub
		return queryRecord;
	}

	public List<Match> getInputMatchList() {
		// TODO Auto-generated method stub
		return matchList;
	}

	public AbaParameters getInputAbaParameters() {
		// TODO Auto-generated method stub
		return parameters;
	}

	public IGraphProperty getInputMergeConnectivity() {
		// TODO Auto-generated method stub
		return mergeConnectivity;
	}
	
	public boolean getInputMergeGroupContainsQuery() {
		return mergeGroupContainsQuery;
	}

	public CompositeEntity<T> getExpectedCompositeEntity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public MatchCandidates<T> getExpectedMatchCandidates() {
		return null;
	}
	
	public TransitiveCandidates<T> getExpectedTransitiveCandidates() {
		return null;
	}

}
