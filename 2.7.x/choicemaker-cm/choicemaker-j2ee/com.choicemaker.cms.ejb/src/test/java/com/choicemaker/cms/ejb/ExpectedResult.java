/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.io.Serializable;
import java.util.List;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cms.api.AbaParameters;

/**
 * Hard-coded examples of composite entities and the match and transitivity
 * candidates that they should produce
 */

public class ExpectedResult<T extends Comparable<T> & Serializable> {

	public static <T extends Comparable<T> & Serializable> CompositeEntity createCompositeEntity(
			DataAccessObject<T> queryRecord,
			List<QueryCandidatePair<T>> pairs) {
		return null;
	}

	private final DataAccessObject<T> queryRecord;
	private final List<Match> matchList;
	private final AbaParameters parameters;
	private final IGraphProperty mergeConnectivity;
	private final boolean mergeGroupContainsQuery;
	private final CompositeEntity expectedCompositeEntity;
	private final TransitiveGroup<T> expectedTransitiveCandidates;

	public ExpectedResult(DataAccessObject<T> qr, List<Match> ml,
			AbaParameters params, IGraphProperty mc, boolean mustContainQuery,
			CompositeEntity expectedCE, TransitiveGroup<T> expectedTC) {
		this.queryRecord = qr;
		this.matchList = ml;
		this.parameters = params;
		this.mergeConnectivity = mc;
		this.mergeGroupContainsQuery = mustContainQuery;
		this.expectedCompositeEntity = expectedCE;
		this.expectedTransitiveCandidates = expectedTC;
	}

	public DataAccessObject<T> getInputQueryRecord() {
		return queryRecord;
	}

	public List<Match> getInputMatchList() {
		return matchList;
	}

	public AbaParameters getInputAbaParameters() {
		return parameters;
	}

	public IGraphProperty getInputMergeConnectivity() {
		return mergeConnectivity;
	}

	public boolean getInputMergeGroupContainsQuery() {
		return mergeGroupContainsQuery;
	}

	public CompositeEntity getExpectedCompositeEntity() {
		return expectedCompositeEntity;
	}

	public MatchGroup<T> getExpectedMatchCandidates() {
		throw new Error("not yet implemented");
	}

	public TransitiveGroup<T> getExpectedTransitiveCandidates() {
		return expectedTransitiveCandidates;
	}

}
