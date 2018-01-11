package com.choicemaker.cms.exp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
//import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.Entity;
import com.choicemaker.cm.transitivity.core.INode;
import com.choicemaker.cm.transitivity.server.util.ClusteringIteratorFactory;
import com.choicemaker.cm.transitivity.util.CEFromMatchesBuilder;
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
import com.choicemaker.cms.ejb.OnlineMatchingBean;
import com.choicemaker.cms.ejb.ParameterHelper;
import com.choicemaker.util.Precondition;

public class Exp<T extends Comparable<T> & Serializable> {

	private static final Logger logger = Logger.getLogger(Exp.class.getName());

	// public TransitiveCandidates<T> getTransitiveCandidates(
	// DataAccessObject<T> query, AbaParameters parameters,
	// AbaSettings settings, AbaServerConfiguration configuration,
	// IGraphProperty mergeConnectivity)
	// throws IOException, BlockingException {

	public EvaluatedRecord[] getCompositeMatchCandidates(
			final OnlineMatchingBean<T> olm, DataAccessObject<T> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration,
			IGraphProperty mergeConnectivity, String modelName,
			float differThreshold, float matchThreshold, int maxNumMatches,
			/*
			 * , LinkCriteria linkCriteria ISingleRecord queryRecord,
			 * DbRecordCollection masterCollection, ,
			 */Object resultFormat, String externalId) throws Exception {

		final List<Match> matchList =
			olm.getMatchList(query, parameters, settings, configuration);
		final Map<Comparable<?>, Match> matchMap =
			new HashMap<Comparable<?>, Match>();
		for (Match match : matchList) {
			matchMap.put(match.id, match);
		}

		final ParameterHelper ph = new ParameterHelper(parameters);
		final ImmutableProbabilityModel model = ph.getModel();

		@SuppressWarnings("unchecked")
		final Record<T> q = model.getAccessor().toImpl(query);
		final T queryId = q.getId();

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
		CompositeEntity<T> compositeEntity = null;
		if (compactedCeIter.hasNext()) {
			@SuppressWarnings("unchecked")
			CompositeEntity<T> _hack = (CompositeEntity<T>) compactedCeIter.next();
			compositeEntity = _hack;
		}
		if (compactedCeIter.hasNext()) {
			String msg =
				"algorithm error: too many matching composite entities";
			logger.severe(msg);
			throw new Error(msg);
		}

		// Compute the return value.
		// If there's no composite entity, the return value is empty.
		// Otherwise process the groups within the entity.
		EvaluatedRecord[] retVal;
		if (compositeEntity == null) {
			logger.info("no matching composite entity");
			retVal = new EvaluatedRecord[0];

		} else {
			// Get the groups of records in the cluster
			List<INode<T>> childEntities = compositeEntity.getChildren();
			if (childEntities == null) {
				logger.info("empty composite entity");
				retVal = new EvaluatedRecord[0];
			} else {
				retVal = processChildEntities(queryId, matchMap, childEntities);
			}
		}

		return retVal;
	}

	private EvaluatedRecord[] processChildEntities(final T queryId,
			final Map<Comparable<?>, Match> matchMap,
			final List<INode<T>> childEntities) throws Exception {

		Precondition.assertNonNullArgument("null queryId", queryId);
		Precondition.assertNonNullArgument("null map", matchMap);
		Precondition.assertNonNullArgument("null child entities",
				childEntities);

		final List<EvaluatedRecord> evalRecords = new ArrayList<>();
		for (Iterator<INode<T>> itChildEntities =
			childEntities.iterator(); itChildEntities.hasNext();) {

			INode<T> childNode = itChildEntities.next();

			// Handle an isolated record
			if (childNode instanceof Entity) {
				T nodeId = childNode.getNodeId();
				if (!nodeId.equals(queryId)) {
//					Match m = (Match) matchMap.get(nodeId);
					EvaluatedRecord er = null;
					// getEvaluatedRecord(resultFormat, m, model);
					evalRecords.add(er);
				}

				// Handle a group of records
			} else if (childNode instanceof CompositeEntity) {
				CompositeEntity<T> group = (CompositeEntity<T>) childNode;
				List<ISingleRecord<T>> groupRecords = new ArrayList<>();
				List<MatchScore> groupScores = new ArrayList<>();
//				boolean isContainQuery = false;

				// Iterate over the records in the group
				for (Iterator<INode<T>> itGroup = group.getChildren().iterator(); itGroup
						.hasNext();) {

					INode<T> groupChildNode = itGroup.next();

					// Check if this record is the query record and
					// whether
					// it should be included
					if (queryId.equals(groupChildNode.getNodeId())) {
//						isContainQuery = true;
						// if (linkCriteria.isMustIncludeQuery()) {
						// if (resultFormat
						// .getRecordType() == RecordType.REF) {
						// groupRecords.add(new RecordRef(queryId));
						// } else {
						// groupRecords.add(queryRecord);
						// }
						// groupScores.add(new MatchScore(1.0f,
						// Decision3.MATCH, ""));
						// }

						// Otherwise add the record from the group as a
						// single record match
						// to the query record
					} else {
//						Match match =
//							(Match) matchMap.get(groupChildNode.getNodeId());
						ISingleRecord<T> isr = null;
						// getSingleRecord(resultFormat,match, model);
						groupRecords.add(isr);
						MatchScore score = null;
						// getMatchScore(resultFormat.getScoreType(),
						// match, model);
						groupScores.add(score);
					}

				} // for groupNodeChildren

				// If every evaluated record must be linked to the query
				// record, but the
				// query record is not part of this group, then bust the
				// group into
				// individual records
				// if (linkCriteria.isMustIncludeQuery() &&
				// !isContainQuery) {
				// for (int n = 0; n < groupRecords.size(); n++) {
				// ISingleRecord singleRecord =
				// (ISingleRecord) groupRecords.get(n);
				// MatchScore singleScore =
				// (MatchScore) groupScores.get(n);
				// EvaluatedRecord er =
				// new EvaluatedRecord(singleRecord, singleScore);
				// evalRecords.add(er);
				// }
				// // Otherwise, add the group as a whole
				// } else {
				// ISingleRecord[] arGroupRecords =
				// (ISingleRecord[]) groupRecords
				// .toArray(new ISingleRecord[0]);
				// LinkCriteria criteria =
				// new LinkCriteria(linkCriteria.getGraphPropType(),
				// linkCriteria.isMustIncludeQuery());
				// LinkedRecordSet lrs =
				// new LinkedRecordSet(null, arGroupRecords, criteria);
				// MatchScore[] scores = (MatchScore[]) groupScores
				// .toArray(new MatchScore[0]);
				// CompositeMatchScore compositeScore =
				// new CompositeMatchScore(scores);
				// // TODO - set an id for the record set
				// EvaluatedRecord er =
				// new EvaluatedRecord(lrs, compositeScore);
				// evalRecords.add(er);
				// }

			} else {
				String msg = "internal error: unexpected node type";
				logger.severe(msg);
				throw new Exception(msg);
			}

		}
		EvaluatedRecord[] retVal;
		retVal =
			(EvaluatedRecord[]) evalRecords.toArray(new EvaluatedRecord[0]);
		return retVal;
	}

}
