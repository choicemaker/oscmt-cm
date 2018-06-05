package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.Entity;
import com.choicemaker.cm.transitivity.core.INode;
import com.choicemaker.cm.transitivity.core.Link;
import com.choicemaker.cm.transitivity.ejb.util.ClusteringIteratorFactory;
import com.choicemaker.cm.transitivity.util.CEFromMatchesBuilder;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.beans.MergeGroupBean;
import com.choicemaker.cms.beans.TransitiveGroupBean;
import com.choicemaker.util.Precondition;

/**
 * A fly-weight helper object (that is, no instance data) that breaks out
 * various internal methods of the OnlineMatchingBean for testing. Most methods
 * have two versions, a public version and an internal version. The public
 * version checks preconditions that are simply assumed true by the internal
 * version.
 *
 * @param <T>
 */
public class OnlineDelegate<T extends Comparable<T> & Serializable> {

	private static final Logger logger =
		Logger.getLogger(OnlineDelegate.class.getName());

	public void addQueryMatchPairToList(DataAccessObject<T> query, Match match,
			ImmutableProbabilityModel model, List<QueryCandidatePair<T>> list) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		Precondition.assertNonNullArgument("null list", list);
		QueryCandidatePair<T> ep = getEvaluatedPair(query, match, model);
		list.add(ep);
	}

	public void addQueryPairToList(DataAccessObject<T> query,
			List<QueryCandidatePair<T>> list) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null list", list);
		float p = 1.0f;
		Decision d = Decision.MATCH;
		QueryCandidatePair<T> ep = new QueryCandidatePair<T>(query, query, p, d);
		list.add(ep);
	}

	public void assertValidArguments(DataAccessObject<?> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration) {
		Precondition.assertNonNullArgument("null query", query);
		List<String> missingSpecs =
			listIncompleteSpecifications(parameters, settings, configuration);
		if (missingSpecs.size() > 0) {
			String msg = createIncompleteSpecificationMessage(parameters,
					settings, configuration);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	public void assertValidArguments(DataAccessObject<T> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration,
			AbaStatisticsController statsController) {
		assertValidArguments(query, parameters, settings, configuration);
		Precondition.assertNonNullArgument("null ABA statistics controller",
				statsController);
	}

	public CompositeEntity computeCompositeEntity(DataAccessObject<T> query,
			List<Match> matchList, AbaParameters parameters,
			IGraphProperty mergeConnectivity) throws TransitivityException {

		final ImmutableProbabilityModel model =
			ParameterHelper.getModel(parameters);

		final Record<T> q = model.getAccessor().toImpl(query);

		final String modelName = parameters.getModelConfigurationName();
		final float differThreshold = parameters.getLowThreshold();
		final float matchThreshold = parameters.getHighThreshold();

		// Get a iterator over the returned records
		CEFromMatchesBuilder builder =
			new CEFromMatchesBuilder(q, matchList.iterator(), modelName,
					differThreshold, matchThreshold);
		@SuppressWarnings("rawtypes")
		Iterator ceIter = builder.getCompositeEntities();

		// Get an iterator to group the records into clusters
		ClusteringIteratorFactory f = ClusteringIteratorFactory.getInstance();
		@SuppressWarnings("rawtypes")
		Iterator compactedCeIter =
			f.createClusteringIterator(mergeConnectivity.getName(), ceIter);

		/*
		 * Get the clusters (there should be at most one, with every record
		 * connected by a hold or a match to the query record).
		 */
		CompositeEntity retVal = null;
		if (compactedCeIter.hasNext()) {
			retVal = (CompositeEntity) compactedCeIter.next();
			if (compactedCeIter.hasNext()) {
				String msg =
					"algorithm error: too many matching composite entities";
				logger.severe(msg);
				throw new Error(msg);
			}
		}
		return retVal;
	}

	public List<QueryCandidatePair<T>> createEvaluatedPairs(
			DataAccessObject<T> query, ImmutableProbabilityModel model,
			List<Match> matches) {

		Precondition.assertNonNullArgument("null record", query);
		Precondition.assertNonNullArgument("null model", model);
		Precondition.assertNonNullArgument("null matches", matches);

		List<QueryCandidatePair<T>> retVal = new ArrayList<>(matches.size());
		for (Match match : matches) {
			String[] notes = match.ac.getNotes(model);
			@SuppressWarnings("unchecked")
			DataAccessObject<T> m = (DataAccessObject<T>) model.getAccessor()
					.toRecordHolder(match.m);
			QueryCandidatePair<T> p = new QueryCandidatePair<T>(query, m,
					match.probability, match.decision, notes);
			retVal.add(p);
		}
		return retVal;
	}

	/** Creates a diagnostic suitable for logging or display to a user. */
	public String createIncompleteSpecificationMessage(AbaParameters parameters,
			AbaSettings settings, AbaServerConfiguration configuration) {
		logger.warning(
				"createIncompleteSpecificationMessage is not implemented");
		return "".intern();
	}

	protected Map<T, Match> createMatchMap(List<Match> matches)
			throws BlockingException {
		/*
		 * No null indices are expected, because Match instances record ids from
		 * the database.
		 */
		Map<T, Match> retVal = new HashMap<>();
		for (Match match : matches) {
			@SuppressWarnings("unchecked")
			T id = (T) match.id;
			assert id != null;
			retVal.put(id, match);
		}
		return retVal;
	}

	public QueryCandidatePair<T> getEvaluatedPair(DataAccessObject<T> query,
			Match match, ImmutableProbabilityModel model) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		DataAccessObject<T> mDAO = getMatchDao(match, model);
		QueryCandidatePair<T> retVal = new QueryCandidatePair<T>(query, mDAO,
				match.probability, match.decision);
		return retVal;
	}

	public DataAccessObject<T> getMatchDao(Match match,
			ImmutableProbabilityModel model) {
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		@SuppressWarnings("unchecked")
		Record<T> m = match.m;
		@SuppressWarnings("unchecked")
		DataAccessObject<T> retVal =
			(DataAccessObject<T>) model.getAccessor().toRecordHolder(m);
		return retVal;
	}

	public List<Match> getMatchList(final DataAccessObject<T> query,
			final AbaParameters parameters, final AbaSettings settings,
			final AbaServerConfiguration configuration,
			final AbaStatisticsController statsController)
			throws IOException, BlockingException {

		assertValidArguments(query, parameters, settings, configuration,
				statsController);

		final ParameterHelper ph = new ParameterHelper(parameters);
		final ImmutableProbabilityModel model =
			ParameterHelper.getModel(parameters);

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

	public SortedSet<T> getCandidateIndicesFromPairs(
			List<QueryCandidatePair<T>> pairs) {
		Precondition.assertNonNullArgument("null pairs", pairs);
		SortedSet<T> retVal = new TreeSet<>();
		for (QueryCandidatePair<T> ep : pairs) {
			// T idx1 = ep.getQueryRecord().getId();
			T idx2 = ep.getMatchCandidate().getId();
			// retVal.add(idx1);
			retVal.add(idx2);
		}
		return retVal;
	}

	public TransitiveGroup<T> getTransitiveGroup(
			final DataAccessObject<T> query, final List<Match> matchList,
			final AbaParameters parameters,
			final IGraphProperty mergeConnectivity,
			final boolean mustIncludeQuery)
			throws TransitivityException, BlockingException {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match list", matchList);
		Precondition.assertNonNullArgument("null parameters", parameters);
		Precondition.assertNonNullArgument("null merge connectivity",
				mergeConnectivity);

		final Map<T, Match> matchMap = createMatchMap(matchList);

		final CompositeEntity compositeEntity = computeCompositeEntity(query,
				matchList, parameters, mergeConnectivity);

		TransitiveGroup<T> retVal;
		if (compositeEntity == null) {
			logger.info("no matching composite entity");
			retVal = new TransitiveGroupBean<>(query);

		} else {
			List<INode<?>> childEntities = compositeEntity.getChildren();
			if (childEntities == null) {
				logger.info("empty composite entity");
				retVal = new TransitiveGroupBean<>(query);

			} else {
				ImmutableProbabilityModel model =
					ParameterHelper.getModel(parameters);
				retVal = getTransitiveGroup(query, matchMap, childEntities,
						model, mergeConnectivity, mustIncludeQuery);
			}
		}

		return retVal;
	}

	public TransitiveGroup<T> getTransitiveGroup(
			final DataAccessObject<T> query, final Map<T, Match> matchMap,
			final List<INode<?>> childEntities,
			final ImmutableProbabilityModel model,
			final IGraphProperty mergeConnectivity,
			final boolean mustIncludeQuery) throws TransitivityException {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null map", matchMap);
		Precondition.assertNonNullArgument("null entities", childEntities);
		Precondition.assertNonNullArgument("null model", model);

		final Map<T, DataAccessObject<T>> candidateMap = new HashMap<>();
		for (Map.Entry<T, Match> me : matchMap.entrySet()) {
			T key = me.getKey();
			DataAccessObject<T> candidate = getMatchDao(me.getValue(), model);
			candidateMap.put(key, candidate);
		}

		/*
		 * Note query.getId() may be null
		 */
		T queryId = query.getId();

		final List<QueryCandidatePair<T>> pairs = new ArrayList<>();
		final List<MergeGroup<T>> mergeGroups = new ArrayList<>();
		for (INode<?> childNode : childEntities) {

			// Handle an isolated record
			if (childNode instanceof Entity) {
				@SuppressWarnings("unchecked")
				T childId = (T) childNode.getNodeId();
				assert childId != null;
				if (!childId.equals(queryId)) {
					Match m = matchMap.get(childId);
					assert m != null;
					QueryCandidatePair<T> pair = getEvaluatedPair(query, m, model);
					pairs.add(pair);
				}

				// Handle a group of records
			} else if (childNode instanceof CompositeEntity) {
				CompositeEntity compositeEntity = (CompositeEntity) childNode;
				List<QueryCandidatePair<T>> queryCandidatePairs = new ArrayList<>();
				List<EvaluatedPair<T>> mergGroupPairs = new ArrayList<>();
				boolean containsQuery = false;

				// Add evaluated pairs from the group
				for (INode<?> child : compositeEntity.getChildren()) {
					assert child instanceof Entity;
					@SuppressWarnings("unchecked")
					final T childId = (T) child.getNodeId();
					if ((queryId == null && childId == null)
							|| childId.equals(queryId)) {
						containsQuery = true;
					} else {
						Match m = matchMap.get(childId);
						addQueryMatchPairToList(query, m, model,
								queryCandidatePairs);
					}

					/*
					 * Merge group pairs are pairs between candidate records
					 * that are formed by transitivity analysis. The pairs are
					 * not represented by matches in the matchMap. Instead, they
					 * are pairs among the compositeEntity.getLinkDefinition()
					 * in which both records are candidates.
					 */
					List<Link<?>> links = compositeEntity.getAllLinks();
					for (Link<?> link : links) {
						@SuppressWarnings("unchecked")
						List<MatchRecord2<T>> mrs =
							((Link<T>) link).getLinkDefinition();
						for (MatchRecord2<T> mr : mrs) {
							T id1 = mr.getRecordID1();
							T id2 = mr.getRecordID2();
							DataAccessObject<T> c1 = candidateMap.get(id1);
							DataAccessObject<T> c2 = candidateMap.get(id2);
							if (c1 != null && c2 != null) {
								QueryCandidatePair<T> ep = new QueryCandidatePair<>(c1,
										c2, mr.getProbability(),
										mr.getMatchType(), mr.getNotes());
								mergGroupPairs.add(ep);
							}
						}
					}

				}

				/*
				 * If every evaluated pair must include the query record, but
				 * the query record is not part of this group, then bust the
				 * group into individual records matched against the query
				 * record. Otherwise, add the group as a merge group.
				 */
				if (mustIncludeQuery && !containsQuery) {
					SortedSet<T> mIndices =
						getCandidateIndicesFromPairs(queryCandidatePairs);
					for (T idx : mIndices) {
						Match match = matchMap.get(idx);
						addQueryMatchPairToList(query, match, model, pairs);
					}

					// Otherwise, add the group as a mergeGroup
				} else {
					assert containsQuery || !mustIncludeQuery;
					pairs.addAll(queryCandidatePairs);
//					if (containsQuery) {
//						mergGroupPairs.addAll(queryCandidatePairs);
//					}
					MergeGroup<T> mergeGroup = new MergeGroupBean<T>(
							mergeConnectivity, mergGroupPairs);
					mergeGroups.add(mergeGroup);
				}

			} else {
				String msg = "internal error: unexpected node type";
				logger.severe(msg);
				throw new TransitivityException(msg);
			}

		}
		TransitiveGroupBean<T> retVal =
			new TransitiveGroupBean<T>(query, pairs, mergeGroups);
		return retVal;
	}

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
	public List<String> listIncompleteSpecifications(AbaParameters parameters,
			AbaSettings settings, AbaServerConfiguration configuration) {
		List<String> retVal = new ArrayList<>();
		if (parameters == null) {
			retVal.add("null parameters");
		}
		if (settings == null) {
			retVal.add("null settings");
		}
		if (configuration == null) {
			retVal.add("null server configuration");
		}
		/*
		 * FIXME check required fields of non-null parameters, settings and
		 * configuration
		 */
		logger.warning("listIncompleteSpecifications is not fully implemented");
		return retVal;
	}

}
