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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.OnlineMatching;
import com.choicemaker.cms.api.remote.OnlineMatchingRemote;
import com.choicemaker.cms.beans.MatchGroupBean;

@Stateless
@Local(OnlineMatching.class)
@Remote(OnlineMatchingRemote.class)
public class OnlineMatchingBean<T extends Comparable<T> & Serializable>
		implements OnlineMatching<T> {

	@EJB
	private AbaStatisticsController statsController;

	@Override
	public MatchGroup<T> getMatchGroup(final DataAccessObject<T> query,
			final AbaParameters parameters, final AbaSettings settings,
			final AbaServerConfiguration configuration)
			throws IOException, BlockingException {

		// Preconditions are checked by delegate methods

		OnlineDelegate<T> delegate = new OnlineDelegate<T>();
		List<Match> matches = delegate.getMatchList(query, parameters, settings,
				configuration, statsController);

		final ImmutableProbabilityModel model =
			ParameterHelper.getModel(parameters);
		List<QueryCandidatePair<T>> pairs =
			delegate.createEvaluatedPairs(query, model, matches);

		MatchGroup<T> retVal = new MatchGroupBean<T>(query, pairs);

		return retVal;
	}

	@Override
	public TransitiveGroup<T> getTransitiveGroup(DataAccessObject<T> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration,
			IGraphProperty mergeConnectivity, boolean mustIncludeQuery)
			throws IOException, BlockingException, TransitivityException {

		// Preconditions are checked by delegate methods

		OnlineDelegate<T> delegate = new OnlineDelegate<T>();
		List<Match> matches = delegate.getMatchList(query, parameters, settings,
				configuration, statsController);

		TransitiveGroup<T> retVal = delegate.getTransitiveGroup(query, matches,
				parameters, mergeConnectivity, mustIncludeQuery);

		return retVal;
	}

}
