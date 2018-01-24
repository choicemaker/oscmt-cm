package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.AbaSettings;
import com.choicemaker.cms.api.OnlineMatching;
import com.choicemaker.cms.beans.MatchCandidatesBean;

@Stateless
public class OnlineMatchingBean<T extends Comparable<T> & Serializable>
		implements OnlineMatching<T> {

	@EJB
	private AbaStatisticsController statsController;

	@Override
	public MatchCandidates<T> getMatchCandidates(
			final DataAccessObject<T> query, final AbaParameters parameters,
			final AbaSettings settings,
			final AbaServerConfiguration configuration)
			throws IOException, BlockingException {

		// Preconditions are checked by delegate methods

		OnlineDelegate<T> delegate = new OnlineDelegate<T>();
		List<Match> matches = delegate.getMatchList(query, parameters, settings,
				configuration, statsController);

		final ImmutableProbabilityModel model =
			ParameterHelper.getModel(parameters);
		List<EvaluatedPair<T>> pairs =
			delegate.createEvaluatedPairs(query, model, matches);

		MatchCandidates<T> retVal = new MatchCandidatesBean<T>(query, pairs);

		return retVal;
	}

	@Override
	public TransitiveCandidates<T> getTransitiveCandidates(
			DataAccessObject<T> query, AbaParameters parameters,
			AbaSettings settings, AbaServerConfiguration configuration,
			IGraphProperty mergeConnectivity, boolean mustIncludeQuery)
			throws IOException, BlockingException, TransitivityException {

		// Preconditions are checked by delegate methods

		OnlineDelegate<T> delegate = new OnlineDelegate<T>();
		List<Match> matches = delegate.getMatchList(query, parameters, settings,
				configuration, statsController);

		TransitiveCandidates<T> retVal = delegate.getTransitiveCandidates(query,
				matches, parameters, mergeConnectivity, mustIncludeQuery);

		return retVal;
	}

}
