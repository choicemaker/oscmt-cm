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
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.Entity;
import com.choicemaker.cm.transitivity.core.INode;
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
			ImmutableProbabilityModel model, List<EvaluatedPair<T>> list) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		Precondition.assertNonNullArgument("null list", list);
		EvaluatedPair<T> gp;
		gp = getEvaluatedPair(query, match, model);
		list.add(gp);
	}

	public void addQueryPairToList(DataAccessObject<T> query,
			List<EvaluatedPair<T>> list) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null list", list);
		float p = 1.0f;
		Decision d = Decision.MATCH;
		EvaluatedPair<T> gp;
		gp = new EvaluatedPair<T>(query, query, p, d);
		list.add(gp);
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

	public List<EvaluatedPair<T>> createEvaluatedPairs(
			DataAccessObject<T> query, ImmutableProbabilityModel model,
			List<Match> matches) {

		Precondition.assertNonNullArgument("null record", query);
		Precondition.assertNonNullArgument("null model", model);
		Precondition.assertNonNullArgument("null matches", matches);

		List<EvaluatedPair<T>> retVal = new ArrayList<>(matches.size());
		for (Match match : matches) {
			String[] notes = match.ac.getNotes(model);
			@SuppressWarnings("unchecked")
			DataAccessObject<T> m = (DataAccessObject<T>) model.getAccessor()
					.toRecordHolder(match.m);
			EvaluatedPair<T> p = new EvaluatedPair<T>(query, m,
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

	public Map<SafeIndex<T>, Match> createMatchMap(List<Match> matchList)
			throws BlockingException {
		/*
		 * At most one null index is expected, for the query. Other records
		 * should be coming from a database, and therefore should have non-null
		 * indices. This method can't handle more than one null index, because
		 * the matchMap will overwrite multiple matches with null indices. (Of
		 * course, overwriting will occur with multiple matches with the same
		 * non-null index, but this is expected for database records.
		 */
		int countListElements = 0;
		int countNullIndices = 0;
		Map<SafeIndex<T>, Match> retVal = new HashMap<>();
		for (Match match : matchList) {
			++countListElements;
			@SuppressWarnings("unchecked")
			T _hackId = (T) match.id;
			if (_hackId == null) {
				++countNullIndices;
			}
			retVal.put(new SafeIndex<T>(_hackId), match);
		}
		final int duplicateIndices = countListElements - retVal.size();
		if (countNullIndices > 1) {
			String msg = "Too many null indices: " + countNullIndices;
			logger.severe(msg);
			throw new BlockingException(msg);
		}
		if (duplicateIndices > 0) {
			String msg = "Duplicate indices! (count: " + duplicateIndices + ")";
			logger.warning(msg);
		}

		return retVal;
	}

	public EvaluatedPair<T> getEvaluatedPair(DataAccessObject<T> query,
			Match match, ImmutableProbabilityModel model) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		DataAccessObject<T> mDAO = getMatchDao(match, model);
		EvaluatedPair<T> retVal = new EvaluatedPair<T>(query, mDAO,
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

	public SortedSet<SafeIndex<T>> getRecordIndicesFromPairs(
			List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null pairs", pairs);
		SortedSet<SafeIndex<T>> retVal = new TreeSet<>();
		for (EvaluatedPair<T> gp : pairs) {
			SafeIndex<T> idx1 = new SafeIndex<T>(gp.getQueryRecord().getId());
			SafeIndex<T> idx2 =
				new SafeIndex<T>(gp.getMatchCandidate().getId());
			retVal.add(idx1);
			retVal.add(idx2);
		}
		return retVal;
	}

	private List<DataAccessObject<T>> getRecordsFromPairs(
			List<EvaluatedPair<T>> pairs) {
		Precondition.assertNonNullArgument("null pairs", pairs);
		SortedSet<SafeIndex<T>> indices = getRecordIndicesFromPairs(pairs);
		List<DataAccessObject<T>> retVal = new ArrayList<>();
		for (EvaluatedPair<T> gp : pairs) {
			DataAccessObject<T> r1 = gp.getQueryRecord();
			DataAccessObject<T> r2 = gp.getMatchCandidate();
			SafeIndex<T> idx1 = new SafeIndex<T>(r1.getId());
			SafeIndex<T> idx2 = new SafeIndex<T>(r2.getId());
			if (indices.contains(idx1)) {
				retVal.add(r1);
				indices.remove(idx1);
			}
			if (indices.contains(idx2)) {
				retVal.add(r2);
				indices.remove(idx2);
			}
			if (indices.isEmpty()) {
				break;
			}
		}
		return retVal;
	}

	public TransitiveGroup<T> getTransitiveCandidates(
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

		final Map<SafeIndex<T>, Match> matchMap = createMatchMap(matchList);

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
				retVal = getTransitiveCandidates(query, childEntities,
						model, mergeConnectivity, mustIncludeQuery);
			}
		}

		return retVal;
	}

	public TransitiveGroup<T> getTransitiveCandidates(
			final DataAccessObject<T> query,
//			final Map<SafeIndex<T>, Match> matchMap,
			final List<INode<?>> childEntities,
			final ImmutableProbabilityModel model,
			final IGraphProperty mergeConnectivity,
			final boolean mustIncludeQuery) throws TransitivityException {

		Precondition.assertNonNullArgument("null query", query);
//		Precondition.assertNonNullArgument("null map", matchMap);
		Precondition.assertNonNullArgument("null entities", childEntities);
		Precondition.assertNonNullArgument("null model", model);

		final SafeIndex<T> queryId = new SafeIndex<T>(query.getId());
		final List<EvaluatedPair<T>> pairs = new ArrayList<>();
		final List<MergeGroup<T>> mergeGroups = new ArrayList<>();
		for (INode<?> childNode : childEntities) {

			// Handle an isolated record
			if (childNode instanceof Entity) {
				@SuppressWarnings({
						"unchecked", "rawtypes" })
				SafeIndex<?> nodeId = new SafeIndex(childNode.getNodeId());
				if (!nodeId.equals(queryId)) {
					Match m = (Match) matchMap.get(nodeId);
					EvaluatedPair<T> pair = getEvaluatedPair(query, m, model);
					pairs.add(pair);
				}

				// Handle a group of records
			} else if (childNode instanceof CompositeEntity) {
				CompositeEntity group = (CompositeEntity) childNode;
				List<EvaluatedPair<T>> groupPairs = new ArrayList<>();
				boolean containsQuery = false;

				// Add evaluated pairs from the group
				for (INode<?> child : group.getChildren()) {
					@SuppressWarnings({
							"rawtypes", "unchecked" })
					final SafeIndex<?> childId =
						new SafeIndex(child.getNodeId());
					if (queryId.equals(childId)) {
						containsQuery = true;
						if (mustIncludeQuery) {
							addQueryPairToList(query, groupPairs);
						}
					} else {
						Match m = (Match) matchMap.get(child);
						addQueryMatchPairToList(query, m, model, groupPairs);
					}
				}

				/*
				 * If every evaluated record must be linked to the query record,
				 * but the query record is not part of this group, then bust the
				 * group into individual records matched against the query
				 * record. Otherwise, add the group as a merge group.
				 */
				if (mustIncludeQuery && !containsQuery) {
					SortedSet<SafeIndex<T>> mIndices =
						getRecordIndicesFromPairs(groupPairs);
					for (SafeIndex<T> idx : mIndices) {
						Match match = matchMap.get(idx);
						addQueryMatchPairToList(query, match, model, pairs);
					}

					// Otherwise, add the group as a mergeCandidate
				} else {
					pairs.addAll(groupPairs);
					List<DataAccessObject<T>> records =
						getRecordsFromPairs(groupPairs);
					MergeGroup<T> mergeCandidate =
						new MergeGroupBean<T>(mergeConnectivity, records,
								groupPairs);
					mergeGroups.add(mergeCandidate);
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
