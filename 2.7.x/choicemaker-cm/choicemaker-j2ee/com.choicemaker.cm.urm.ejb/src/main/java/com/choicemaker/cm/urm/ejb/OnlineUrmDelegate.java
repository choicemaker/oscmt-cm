package com.choicemaker.cm.urm.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.aba.IncompleteBlockingSetsException;
import com.choicemaker.cm.aba.UnderspecifiedQueryException;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.Entity;
import com.choicemaker.cm.transitivity.core.INode;
import com.choicemaker.cm.transitivity.ejb.util.ClusteringIteratorFactory;
import com.choicemaker.cm.transitivity.util.CEFromMatchesBuilder;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.CompositeMatchScore;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.Decision3;
import com.choicemaker.cm.urm.base.EvalRecordFormat;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.base.LinkedRecordSet;
import com.choicemaker.cm.urm.base.MatchScore;
import com.choicemaker.cm.urm.base.RecordType;
import com.choicemaker.cm.urm.base.ScoreType;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.UrmIncompleteBlockingSetsException;
import com.choicemaker.cm.urm.exceptions.UrmUnderspecifiedQueryException;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigConversion;
import com.choicemaker.cms.ejb.OnlineDelegate;
import com.choicemaker.cms.ejb.ParameterHelper;
import com.choicemaker.util.Precondition;

public class OnlineUrmDelegate<T extends Comparable<T> & Serializable> {

	public static final String NOTE_SEPARATOR = "\t";

	private static final Logger logger =
		Logger.getLogger(OnlineUrmDelegate.class.getName());

	public EvaluatedRecord[] getCompositeMatchCandidates(
			final ISingleRecord<T> queryRecord,
			final DbRecordCollection masterCollection,
			final String modelConfigurationName, final float differThreshold,
			final float matchThreshold, final int UNUSED_maxNumMatches,
			final LinkCriteria linkCriteria,
			final EvalRecordFormat resultFormat, final String externalId,
			final UrmConfigurationAdapter adapter,
			final NamedConfigurationController ncController,
			final AbaStatisticsController statsController,
			final UrmEjbAssist<T> assist) throws ConfigException,
			UrmIncompleteBlockingSetsException, UrmUnderspecifiedQueryException,
			CmRuntimeException, RemoteException {

		Precondition.assertNonNullArgument("null query", queryRecord);
		Precondition.assertNonNullArgument("null references", masterCollection);
		Precondition.assertNonEmptyString("null or empty model",
				modelConfigurationName);
		Precondition.assertBoolean("invalid differ threshold",
				differThreshold >= 0f && differThreshold <= 1f);
		Precondition.assertBoolean("invalid match threshold",
				matchThreshold >= 0f && matchThreshold <= 1f);
		Precondition.assertBoolean("invalid thresholds (differ > match)",
				differThreshold <= matchThreshold);
		Precondition.assertNonNullArgument("null adapter", adapter);
		Precondition.assertNonNullArgument("null ncController", ncController);
		Precondition.assertNonNullArgument("null statsController",
				statsController);
		Precondition.assertNonNullArgument("null assist", assist);

		final int oabaMaxSingle = Integer.MAX_VALUE;
		NamedConfiguration cmConf = assist.createCustomizedConfiguration(
				adapter, ncController, masterCollection, modelConfigurationName,
				differThreshold, matchThreshold, oabaMaxSingle);

		List<EvaluatedRecord> evalRecords = new ArrayList<>();
		try {
			final AbaParameters abaParams =
				NamedConfigConversion.createAbaParameters(cmConf);
			assert abaParams != null;

			final AbaSettings abaSettings =
				NamedConfigConversion.createAbaSettings(cmConf);
			assert abaSettings != null;

			final AbaServerConfiguration serverConfig =
				NamedConfigConversion.createAbaServerConfiguration(cmConf);
			assert serverConfig != null;

			final OnlineDelegate<T> delegate = new OnlineDelegate<>();
			final T queryId = queryRecord.getId();

			/*
			 * Construct a server-side RecordImpl from a client-side
			 * RecordHolder
			 */
			final ImmutableProbabilityModel model =
				ParameterHelper.getModel(abaParams);
			final String modelName = model.getModelName();
			final Accessor accessor = model.getAccessor();
			final Record<T> q = accessor.toImpl(queryRecord);

			// Perform matching
			List<Match> sortedMatches = delegate.getMatchList(queryRecord,
					abaParams, abaSettings, serverConfig, statsController);

			// Create a map of candidate ids to candidate records
			Map<T, Match> matches = delegate.createMatchMap(sortedMatches);

			// Get a iterator over the returned records
			CEFromMatchesBuilder builder =
				new CEFromMatchesBuilder(q, sortedMatches.iterator(), modelName,
						differThreshold, matchThreshold);
			Iterator<?> ceIter = builder.getCompositeEntities();

			// Get an iterator to group the records into clusters
			final String name = linkCriteria.getGraphPropType().getName();
			ClusteringIteratorFactory f =
				ClusteringIteratorFactory.getInstance();
			Iterator<?> compactedCeIter =
				f.createClusteringIterator(name, ceIter);

			// Get the clusters (there should be just one, with every record
			// connected by a hold or a match to the query record)
			CompositeEntity compositeEntity = null;
			if (compactedCeIter.hasNext()) {
				compositeEntity = (CompositeEntity) compactedCeIter.next();
			}
			if (compositeEntity == null) {
				logger.info("no matching composite entity");
				return null;
			} else if (compactedCeIter.hasNext()) {
				String msg =
					"algorithm error: too many matching composite entities";
				logger.severe(msg);
				throw new Error(msg);
			}

			// Get the groups of records in the cluster
			List<INode<?>> childEntities = compositeEntity.getChildren();
			if (childEntities == null) {
				logger.info("empty composite entity");
				return null;
			}

			for (Iterator<INode<?>> itChildEntities =
				childEntities.iterator(); itChildEntities.hasNext();) {

				@SuppressWarnings("unchecked")
				INode<T> childNode = (INode<T>) itChildEntities.next();

				// Handle an isolated record
				if (childNode instanceof Entity) {
					T nodeId = childNode.getNodeId();
					if (!nodeId.equals(queryId)) {
						Match m = matches.get(nodeId);
						EvaluatedRecord er =
							getSingleEvaluatedRecord(resultFormat, m, model);
						evalRecords.add(er);
					}

					// Handle a group of records
				} else if (childNode instanceof CompositeEntity) {
					CompositeEntity group = (CompositeEntity) childNode;
					List<ISingleRecord<T>> groupRecords = new ArrayList<>();
					List<MatchScore> groupScores = new ArrayList<>();
					boolean isContainQuery = false;

					// Iterate over the records in the group
					for (Iterator<INode<?>> itGroup =
						group.getChildren().iterator(); itGroup.hasNext();) {

						@SuppressWarnings("unchecked")
						INode<T> groupChildNode = (INode<T>) itGroup.next();

						// Check if this record is the query record and whether
						// it should be included
						if (queryId.equals(groupChildNode.getNodeId())) {
							isContainQuery = true;
							if (linkCriteria.isMustIncludeQuery()) {
								if (resultFormat
										.getRecordType() == RecordType.REF) {
									String msg =
										"Record type '" + RecordType.REF
												+ "' no longer supported";
									throw new ConfigException(msg);
								} else {
									groupRecords.add(queryRecord);
								}
								groupScores.add(new MatchScore(1.0f,
										Decision3.MATCH, ""));
							}

							// Otherwise add the record from the group as a
							// single record match
							// to the query record
						} else {
							Match match =
								matches.get(groupChildNode.getNodeId());
							final ScoreType unused = null;
							groupRecords
									.add(getSingleRecord(unused, match, model));
							groupScores.add(getMatchScore(match, model));
						}

					} // for groupNodeChildren

					// If every evaluated record must be linked to the query
					// record, but the
					// query record is not part of this group, then bust the
					// group into
					// individual records
					if (linkCriteria.isMustIncludeQuery() && !isContainQuery) {
						for (int n = 0; n < groupRecords.size(); n++) {
							ISingleRecord<T> singleRecord = groupRecords.get(n);
							MatchScore singleScore = groupScores.get(n);
							EvaluatedRecord er =
								new EvaluatedRecord(singleRecord, singleScore);
							evalRecords.add(er);
						}
						// Otherwise, add the group as a whole
					} else {
						@SuppressWarnings("unchecked")
						ISingleRecord<T>[] arGroupRecords =
							groupRecords.toArray(new ISingleRecord[0]);
						LinkCriteria criteria =
							new LinkCriteria(linkCriteria.getGraphPropType(),
									linkCriteria.isMustIncludeQuery());
						LinkedRecordSet<T> lrs = new LinkedRecordSet<>(null,
								arGroupRecords, criteria);
						MatchScore[] scores =
							groupScores.toArray(new MatchScore[0]);
						CompositeMatchScore compositeScore =
							new CompositeMatchScore(scores);
						EvaluatedRecord er =
							new EvaluatedRecord(lrs, compositeScore);
						evalRecords.add(er);
					}

				} else {
					String msg = "internal error: unexpected node type";
					logger.severe(msg);
					throw new CmRuntimeException(msg);
				}

			}

		} catch (IncompleteBlockingSetsException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new UrmIncompleteBlockingSetsException(msg);
		} catch (UnderspecifiedQueryException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new UrmUnderspecifiedQueryException(msg);
		} catch (BlockingException | IOException | TransitivityException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new ConfigException(msg);
		}

		EvaluatedRecord[] retVal = evalRecords.toArray(new EvaluatedRecord[0]);
		return retVal;
	}

	private MatchScore getMatchScore(Match match,
			ImmutableProbabilityModel model) {
		String note = "";
		String[] notes = match.ac.getNotes(model);
		for (String n : notes) {
			note += NOTE_SEPARATOR + n;
		}
		String decisionName = match.decision.getName();
		Decision3 d3 = Decision3.valueOf(decisionName);
		MatchScore score = new MatchScore(match.probability, d3, note);
		return score;
	}

	private ISingleRecord<T> getSingleRecord(ScoreType unused, Match match,
			ImmutableProbabilityModel model) {
		assert match.m != null : "null candidate record";
		assert match.m instanceof ISingleRecord : "not a database record: "
				+ match.m.getClass().getName();
		Accessor accessor = model.getAccessor();
		@SuppressWarnings("unchecked")
		ISingleRecord<T> retVal =
			(ISingleRecord<T>) accessor.toRecordHolder(match.m);
		return retVal;
	}

	private EvaluatedRecord getSingleEvaluatedRecord(EvalRecordFormat unused,
			Match m, ImmutableProbabilityModel model) {
		final ScoreType unused2 = null;
		ISingleRecord<T> candidate = getSingleRecord(unused2, m, model);
		MatchScore score = getMatchScore(m, model);
		EvaluatedRecord retVal = new EvaluatedRecord(candidate, score);
		return retVal;
	}

}
