package com.choicemaker.cms.exp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MergeCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.cm.core.BlockingException;
//import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.Entity;
import com.choicemaker.cm.transitivity.core.INode;
import com.choicemaker.cm.transitivity.core.TransitivityException;
import com.choicemaker.cm.transitivity.server.util.ClusteringIteratorFactory;
import com.choicemaker.cm.transitivity.util.CEFromMatchesBuilder;
import com.choicemaker.cm.urm.base.Decision3;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.MatchScore;
//import com.choicemaker.cm.urm.base.CompositeMatchScore;
//import com.choicemaker.cm.urm.base.DbRecordCollection;
//import com.choicemaker.cm.urm.base.Decision3;
//import com.choicemaker.cm.urm.base.EvalRecordFormat;
//import com.choicemaker.cm.urm.base.EvaluatedRecord;
//import com.choicemaker.cm.urm.base.ISingleRecord;
//import com.choicemaker.cm.urm.base.LinkCriteria;
//import com.choicemaker.cm.urm.base.LinkedRecordSet;
//import com.choicemaker.cm.urm.base.MatchScore;
//import com.choicemaker.cm.urm.base.RecordRef;
//import com.choicemaker.cm.urm.base.RecordType;
//import com.choicemaker.cm.urm.ejb.InternalRecordBuilder;
//import com.choicemaker.cm.urm.exceptions.ArgumentException;
//import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
//import com.choicemaker.cm.urm.exceptions.ConfigException;
//import com.choicemaker.cm.urm.exceptions.ModelException;
//import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
//import com.choicemaker.cm.urm.exceptions.RecordException;
//import com.choicemaker.cm.urm.exceptions.UrmIncompleteBlockingSetsException;
//import com.choicemaker.cm.urm.exceptions.UrmUnderspecifiedQueryException;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.AbaSettings;
import com.choicemaker.cms.beans.TransitiveCandidatesBean;
import com.choicemaker.cms.ejb.OnlineMatchingBean;
import com.choicemaker.cms.ejb.ParameterHelper;
import com.choicemaker.util.Precondition;

public class Exp<T extends Comparable<T> & Serializable> {

	private static final Logger logger = Logger.getLogger(Exp.class.getName());

	public TransitiveCandidates<T> getTransitiveCandidates(
			final OnlineMatchingBean<T> olm, final DataAccessObject<T> query,
			final AbaParameters parameters, final AbaSettings settings,
			final AbaServerConfiguration configuration,
			final IGraphProperty mergeConnectivity,
			final boolean mustIncludeQuery) throws Exception {

		final List<Match> matchList =
			olm.getMatchList(query, parameters, settings, configuration);
		final Map<SafeIndex<T>, Match> matchMap = createMatchMap(matchList);
		final ParameterHelper ph = new ParameterHelper(parameters);
		final ImmutableProbabilityModel model = ph.getModel();

		final CompositeEntity<T> compositeEntity =
			computeCompositeEntity(query, matchList, model, parameters, mergeConnectivity);

		TransitiveCandidates<T> retVal;
		if (compositeEntity == null) {
			logger.info("no matching composite entity");
			retVal = new TransitiveCandidatesBean<>(query);

		} else {
			List<INode<T>> childEntities = compositeEntity.getChildren();
			if (childEntities == null) {
				logger.info("empty composite entity");
				retVal = new TransitiveCandidatesBean<>(query);

			} else {
				retVal = getTransitiveCandidates(query, matchMap, childEntities,
						model, mustIncludeQuery);
			}
		}

		return retVal;
	}

	public CompositeEntity<T> computeCompositeEntity(DataAccessObject<T> query,
			List<Match> matchList, ImmutableProbabilityModel model,
			AbaParameters parameters, IGraphProperty mergeConnectivity) throws TransitivityException {

		@SuppressWarnings("unchecked")
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

		// Get the clusters (there should be at most one, with every record
		// connected by a hold or a match to the query record).
		CompositeEntity<T> retVal = null;
		if (compactedCeIter.hasNext()) {
			@SuppressWarnings("unchecked")
			CompositeEntity<T> _hack =
				(CompositeEntity<T>) compactedCeIter.next();
			retVal = _hack;
		}
		if (compactedCeIter.hasNext()) {
			String msg =
				"algorithm error: too many matching composite entities";
			logger.severe(msg);
			throw new Error(msg);
		}
		return retVal;
	}

	public TransitiveCandidates<T> getTransitiveCandidates(
			final DataAccessObject<T> query,
			final Map<SafeIndex<T>, Match> matchMap,
			final List<INode<T>> childEntities,
			final ImmutableProbabilityModel model,
			final boolean mustIncludeQuery) throws Exception {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null map", matchMap);
		Precondition.assertNonNullArgument("null entities", childEntities);
		Precondition.assertNonNullArgument("null model", model);

		final SafeIndex<T> queryId = new SafeIndex<T>(query.getId());
		final List<EvaluatedPair<T>> pairs = new ArrayList<>();
		final List<MergeCandidates<T>> mergeGroups = new ArrayList<>();
		for (INode<T> childNode : childEntities) {

			// Handle an isolated record
			if (childNode instanceof Entity) {
				SafeIndex<T> nodeId = new SafeIndex<T>(childNode.getNodeId());
				if (!nodeId.equals(queryId)) {
					Match m = (Match) matchMap.get(nodeId);
					EvaluatedPair<T> pair = getEvaluatedPair(query, m, model);
					pairs.add(pair);
				}

				// Handle a group of records
			} else if (childNode instanceof CompositeEntity) {
				CompositeEntity<T> group = (CompositeEntity<T>) childNode;
				List<EvaluatedPair<T>> groupPairs = new ArrayList<>();
				boolean containsQuery = false;

				// Add evaluated pairs from the group
				for (INode<T> child : group.getChildren()) {
					final SafeIndex<T> childId =
						new SafeIndex<T>(child.getNodeId());
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
				 * record
				 */
				if (mustIncludeQuery && !containsQuery) {
//					for (int n = 0; n < groupRecords.size(); n++) {
//						ISingleRecord singleRecord =
//							(ISingleRecord) groupRecords.get(n);
//						MatchScore singleScore =
//							(MatchScore) groupScores.get(n);
//						EvaluatedRecord er =
//							new EvaluatedRecord(singleRecord, singleScore);
//						evalRecords.add(er);
//					}
					// Otherwise, add the group as a whole
				} else {
//					ISingleRecord[] arGroupRecords =
//						(ISingleRecord[]) groupRecords
//								.toArray(new ISingleRecord[0]);
//					LinkCriteria criteria = new LinkCriteria(
//							linkCriteria.getGraphPropType(), mustIncludeQuery);
//					LinkedRecordSet lrs =
//						new LinkedRecordSet(null, arGroupRecords, criteria);
//					MatchScore[] scores =
//						(MatchScore[]) groupScores.toArray(new MatchScore[0]);
//					CompositeMatchScore compositeScore =
//						new CompositeMatchScore(scores);
//					// TODO - set an id for the record set
//					EvaluatedRecord er =
//						new EvaluatedRecord(lrs, compositeScore);
//					evalRecords.add(er);
				}

			} else {
				String msg = "internal error: unexpected node type";
				logger.severe(msg);
				throw new Exception(msg);
			}

		}
		TransitiveCandidatesBean<T> retVal =
			new TransitiveCandidatesBean<T>(query, pairs, mergeGroups);
		return retVal;
	}

	public Map<SafeIndex<T>, Match> createMatchMap(
			List<Match> matchList) throws BlockingException {
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

	public void addQueryMatchPairToList(
			DataAccessObject<T> query, Match match,
			ImmutableProbabilityModel model, List<EvaluatedPair<T>> list) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		Precondition.assertNonNullArgument("null list", list);
		EvaluatedPair<T> gp;
		gp = getEvaluatedPair(query, match, model);
		list.add(gp);
	}

	public void addQueryPairToList(
			DataAccessObject<T> query, List<EvaluatedPair<T>> list) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null list", list);
		float p = 1.0f;
		Decision d = Decision.MATCH;
		EvaluatedPair<T> gp;
		gp = new EvaluatedPair<T>(query, query, p, d);
		list.add(gp);
	}

	public DataAccessObject<T> getMatchDao(
			Match match, ImmutableProbabilityModel model) {
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		@SuppressWarnings("unchecked")
		Record<T> m = match.m;
		@SuppressWarnings("unchecked")
		DataAccessObject<T> retVal =
			(DataAccessObject<T>) model.getAccessor().toRecordHolder(m);
		return retVal;
	}

	public EvaluatedPair<T> getEvaluatedPair(
			DataAccessObject<T> query, Match match,
			ImmutableProbabilityModel model) {
		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null match", match);
		Precondition.assertNonNullArgument("null model", model);
		DataAccessObject<T> mDAO = getMatchDao(match, model);
		EvaluatedPair<T> retVal = new EvaluatedPair<T>(query, mDAO,
				match.probability, match.decision);
		return retVal;
	}

}
