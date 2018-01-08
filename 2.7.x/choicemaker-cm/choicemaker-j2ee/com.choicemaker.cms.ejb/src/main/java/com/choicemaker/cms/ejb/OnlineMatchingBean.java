package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.Match;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.core.util.MatchUtils;
import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.blocking.automated.base.Blocker2;
import com.choicemaker.cms.api.OnlineContext;
import com.choicemaker.cms.api.OnlineMatching;
import com.choicemaker.cms.args.EvaluatedPair;
import com.choicemaker.cms.args.MatchCandidates;
import com.choicemaker.util.Precondition;

public class OnlineMatchingBean<T extends Comparable<T>>
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
			OnlineContext configuration) {
		throw new Error("not yet implemented");
	}
	
	/** Creates a diagnostic suitable for logging or display to a user. */
	public static String createIncompleteSpecificationMessage(OnlineContext configuration) {
		throw new Error("not yet implemented");
	}

	@Override
	public MatchCandidates<T> getMatchCandidates(Record<T> query,
			OnlineContext configuration) throws IOException {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null configuration", configuration);
		List<String> missingSpecs = listIncompleteSpecifications(configuration);
		if (missingSpecs.size() > 0) {
			String msg = createIncompleteSpecificationMessage(configuration);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		DatabaseAccessor databaseAccessor = null;
		ImmutableProbabilityModel model = null;
		Record<T> q = null;
		int limitPBS = Integer.MIN_VALUE;
		int stbsgl = Integer.MIN_VALUE;
		int limitSBS = Integer.MIN_VALUE;
		AbaStatistics stats = null;
		String databaseConfiguration = null;
		String blockingConfiguration = null;

		RecordDecisionMaker dm = new RecordDecisionMaker();
		float lowThreshold = -1.0f;
		float highThreshold = -1.0f;

		AutomatedBlocker rs =
			new Blocker2(databaseAccessor, model, q, limitPBS, stbsgl, limitSBS,
					stats, databaseConfiguration, blockingConfiguration);
		logger.fine(q.getId() + " " + rs + " " + model);

		List<Match> matches =
			dm.getMatches(q, rs, model, lowThreshold, highThreshold);
		assert matches != null;

		List<EvaluatedPair<T>> pairs = new ArrayList<>(matches.size());
		for (Match m : matches) {
			String[] notes = m.ac.getNotes(model);
			@SuppressWarnings("unchecked")
			EvaluatedPair<T> p =
				new EvaluatedPair<T>(q, m.m, m.probability, m.decision, notes);
			pairs.add(p);
		}

		MatchCandidates<T> retVal = new MatchCandidates<T>(q, pairs);
		return retVal;
	}

}
