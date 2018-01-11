package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.AbaStatisticsController;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.AbaSettings;
import com.choicemaker.cms.api.OnlineMatching;
import com.choicemaker.cms.beans.MatchCandidatesBean;
import com.choicemaker.util.Precondition;

@Stateless
public class OnlineMatchingBean<T extends Comparable<T> & Serializable>
		implements OnlineMatching<T> {

	private static final Logger logger =
		Logger.getLogger(OnlineMatchingBean.class.getName());

	/**
	 * Checks for any specifications required for a complete online matching
	 * configuration.
	 *
	 * @param configuration
	 *            a non-null online matching context
	 * @return a non-null list of any missing specifications. The list will be
	 *         empty if the configuration is a complete specification for online
	 *         matching.
	 */
	public static List<String> listIncompleteSpecifications(
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration) {
		logger.warning("listIncompleteSpecifications is not implemented");
		return Collections.emptyList();
	}

	/** Creates a diagnostic suitable for logging or display to a user. */
	public static String createIncompleteSpecificationMessage(
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration) {
		logger.warning(
				"createIncompleteSpecificationMessage is not implemented");
		return "".intern();
	}

	public static <T extends Comparable<T> & Serializable> List<EvaluatedPair<T>> createEvaluatedPairs(
			DataAccessObject<T> query, ImmutableProbabilityModel model,
			List<Match> matches) {

		Precondition.assertNonNullArgument("null record", query);
		Precondition.assertNonNullArgument("null model", model);
		Precondition.assertNonNullArgument("null matches", matches);

		List<EvaluatedPair<T>> pairs = new ArrayList<>(matches.size());
		for (Match match : matches) {
			String[] notes = match.ac.getNotes(model);
			@SuppressWarnings("unchecked")
			DataAccessObject<T> m = (DataAccessObject<T>) model.getAccessor()
					.toRecordHolder(match.m);
			EvaluatedPair<T> p = new EvaluatedPair<T>(query, m,
					match.probability, match.decision, notes);
			pairs.add(p);
		}
		return null;
	}

	@EJB
	private AbaStatisticsController statsController;

	@Override
	public MatchCandidates<T> getMatchCandidates(
			final DataAccessObject<T> query, final AbaParameters parameters,
			final AbaSettings settings,
			final AbaServerConfiguration configuration)
			throws IOException, BlockingException {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null parameters", parameters);
		Precondition.assertNonNullArgument("null settings", settings);
		Precondition.assertNonNullArgument("null configuration", configuration);

		final ParameterHelper ph = new ParameterHelper(parameters);
		final ImmutableProbabilityModel model = ph.getModel();

		@SuppressWarnings("unchecked")
		final Record<T> q = model.getAccessor().toImpl(query);

		final AutomatedBlocker rs =
			ph.getAutomatedBlocker(q, settings, statsController);
		final float lowThreshold = parameters.getLowThreshold();
		final float highThreshold = parameters.getHighThreshold();

		final RecordDecisionMaker dm = new RecordDecisionMaker();

		List<Match> matches =
			dm.getMatches(q, rs, model, lowThreshold, highThreshold);
		List<EvaluatedPair<T>> pairs =
			createEvaluatedPairs(query, model, matches);
		MatchCandidates<T> retVal = new MatchCandidatesBean<T>(query, pairs);

		return retVal;
	}

	// FIXME make protected
	public List<Match> getMatchList(final DataAccessObject<T> query,
			final AbaParameters parameters, final AbaSettings settings,
			final AbaServerConfiguration configuration)
			throws IOException, BlockingException {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null configuration", configuration);
		List<String> missingSpecs =
			listIncompleteSpecifications(parameters, settings, configuration);
		if (missingSpecs.size() > 0) {
			String msg = createIncompleteSpecificationMessage(parameters,
					settings, configuration);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		final ParameterHelper ph = new ParameterHelper(parameters);
		final ImmutableProbabilityModel model = ph.getModel();

		@SuppressWarnings("unchecked")
		final Record<T> q = model.getAccessor().toImpl(query);
		final AutomatedBlocker rs =
			ph.getAutomatedBlocker(q, settings, statsController);
		final float lowThreshold = parameters.getLowThreshold();
		final float highThreshold = parameters.getHighThreshold();
		final RecordDecisionMaker dm = new RecordDecisionMaker();

		List<Match> retVal =
			dm.getMatches(q, rs, model, lowThreshold, highThreshold);

		return retVal;
	}

	@Override
	public TransitiveCandidates<T> getTransitiveCandidates(
			DataAccessObject<T> query, AbaParameters parameters,
			AbaSettings settings, AbaServerConfiguration configuration,
			IGraphProperty mergeConnectivity)
			throws IOException, BlockingException {
		MatchCandidates<T> mcs =
			getMatchCandidates(query, parameters, settings, configuration);
		// The match candidates are related by hold or match relationships to
		// the query record. Two tasks remain.
		// 1. Compute the transitive closure of the candidates among themselves.
		// 2. Find any merge groups among the records. Merge groups are records
		// related by match decisions that meet the connectivity requirement
		// specified by the IGraphProperty argument

		return null;
	}

}
