/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.CompositeMatchScore;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.Decision3;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
//import com.choicemaker.cm.urm.base.GraphProperty;
import com.choicemaker.cm.urm.base.IMatchScore;
import com.choicemaker.cm.urm.base.IRecord;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.IRecordHolder;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.base.LinkedRecordSet;
import com.choicemaker.cm.urm.base.MatchScore;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.base.SubsetDbRecordCollection;
import com.choicemaker.cm.urm.base.TextRefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.ejb.NamedConfigurationEntity;
import com.choicemaker.cms.util.IdentifiableWrapper;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * A fly-weight helper class.
 *
 * @param <T>
 *            the field type for record id values
 */
class UrmEjbAssist<T extends Comparable<T> & Serializable> {

	private static final Logger logger =
		Logger.getLogger(UrmEjbAssist.class.getName());

	public UrmEjbAssist() {
	}

	public URI extractLocationURI(TextRefRecordCollection resRc)
			throws URISyntaxException {
		Precondition.assertNonNullArgument("null record collection", resRc);

		// target files or urls
		String urlBeginingPart = null;
		String urlEndingPart = null;

		String urlString = resRc.getUrl();
		final int lastPeriod = urlString.lastIndexOf(".");
		final int lastSlash = urlString.lastIndexOf("/");
		final int lastBkSlash = urlString.lastIndexOf("\\");
		if (lastPeriod == -1 || lastPeriod < lastSlash
				|| lastPeriod < lastBkSlash) {
			urlBeginingPart = urlString;
			urlEndingPart = "";
		} else {
			urlBeginingPart = urlString.substring(0, lastPeriod);
			urlEndingPart = urlString.substring(lastPeriod);
		}
		String url = urlBeginingPart + urlEndingPart;
		logger.fine("BatchMatchAnalyzer.extractLocationURI: '" + url + "'");
		URI retVal = new URI(url);
		return retVal;
	}

	public void copyResult(UrmBatchController urmBatchController, long jobID,
			RefRecordCollection resRc)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		// Precondition
		if (!(resRc instanceof TextRefRecordCollection)) {
			String msg = "BatchMatchAnalyzer.copyResult: "
					+ "this method supports only text record collection copying";
			throw new ArgumentException(msg);
		}
		TextRefRecordCollection textRefRc = (TextRefRecordCollection) resRc;

		// Extract URM batch job
		BatchJob urmJob = urmBatchController.findUrmJob(jobID);
		if (urmJob == null) {
			logger.warning(
					"BatchMatchAnalyzer.copyResult: no such URM job: " + jobID);
		} else {
			try {
				URI container = extractLocationURI(textRefRc);
				urmBatchController.exportResults(urmJob, container);
			} catch (IOException | URISyntaxException e) {
				String msg = "BatchMatchAnalyzer.copyResult: "
						+ "unable to copy result: " + e.toString();
				throw new RecordCollectionException(msg);
			}
		}
	}

	public EvaluatedRecord[] computeEvaluatedRecords(MatchGroup<T> matchGroup) {
		Precondition.assertNonNullArgument("null match candidates", matchGroup);
		List<EvaluatedRecord> records = new ArrayList<>();
		List<QueryCandidatePair<T>> pairs = matchGroup.getQueryCandidatePairs();
		for (QueryCandidatePair<T> pair : pairs) {
			// IRecord<T> q = (IRecord<T>) pair.getQueryRecord();
			IRecord<T> m = (IRecord<T>) pair.getMatchCandidate();
			Decision d = pair.getMatchDecision();
			String decisionName = d.getName();
			Decision3 d3 = Decision3.valueOf(decisionName);
			float p = pair.getMatchProbability();
			String note = pair.getNotesAsDelimitedString();
			IMatchScore score = new MatchScore(p, d3, note);
			EvaluatedRecord record = new EvaluatedRecord(m, score);
			records.add(record);
		}
		EvaluatedRecord[] retVal =
			records.toArray(new EvaluatedRecord[records.size()]);
		return retVal;
	}

	/**
	 * The EvaluatedRecord instances in an array returned by this method are
	 * either single records implicitly matched to a query record, or composite
	 * records matched among themselves. In addition, if the query record must
	 * be included in the returned array (
	 * <code>LinkCriteria.isMustIncludeQuery()</code>), it is represented as
	 * EvaulatedRecord with a MATCH decision and a probability score of 1.0f.
	 * <p>
	 * The array returned by this method is a weird and incomplete
	 * representation of a transitivity group. If the results must include the
	 * query record, then match relationships between records in a merge group
	 * are missing. Conversely, if the results do not include the query record,
	 * then match relationships may be missing between the query record and
	 * candidate records within merge groups.
	 */
	public EvaluatedRecord[] computeEvaluatedRecords(TransitiveGroup<T> tcs,
			LinkCriteria linkCriteria) {

		Precondition.assertNonNullArgument("null transitiveCandidates", tcs);
		Precondition.assertNonNullArgument("null link criteria", linkCriteria);

		DataAccessObject<T> queryRecord = tcs.getQueryRecord();
		List<MergeGroup<T>> mergeGroups = tcs.getMergeGroups();

		// Assume all candidates record will be added as single, evaluated
		// records. If a record is later added to a CompositeRecord,
		// it will be removed from this set.
		SortedSet<IdentifiableWrapper<T>> singleRecords = new TreeSet<>();
		// Note tcs.getCandidateRecords() excludes the query record
		for (DataAccessObject<T> candidate : tcs.getCandidateRecords()) {
			IdentifiableWrapper<T> wrappedCandidate =
				new IdentifiableWrapper<>(candidate);
			singleRecords.add(wrappedCandidate);
		}

		String mergeGroupIdOfQuery = null;
		List<EvaluatedRecord> evaluatedRecords = new ArrayList<>();
		for (MergeGroup<T> mergeGroup : mergeGroups) {

			if (mergeGroup.containsRecord(queryRecord)) {
				assert mergeGroupIdOfQuery == null;
				mergeGroupIdOfQuery = mergeGroup.getGroupId();
				logger.fine("Merge group of the query record: "
						+ mergeGroupIdOfQuery);
			}

			if (linkCriteria.isMustIncludeQuery()
					&& !mergeGroup.containsRecord(queryRecord)) {
				/*
				 * If composite records must contain the query record, and if a
				 * merge group doesn't contain the query record, then skip this
				 * merge group. This will effectively break up the merge group
				 * into individual records linked to the query record and add
				 * them to the evaluated record list
				 */
				logger.fine("Skipping merge group " + mergeGroup.getGroupId());

			} else {
				/*
				 * Otherwise, convert a merge group to a linked record set and
				 * add it to the evaluated record list. Remove each candidate
				 * record from the set of singleRecords.
				 */
				logger.fine("Add merge group " + mergeGroup.getGroupId()
						+ " as LinkdRecordSet");
				LinkedRecordSet<T> lrs = createLinkedRecordSetFromMergeGroup(
						mergeGroup, linkCriteria);
				CompositeMatchScore cms =
					createCompositeMatchScoreFromMergeGroup(mergeGroup);
				removeLinkedRecordsFromSingleRecords(lrs, singleRecords);
				EvaluatedRecord er = new EvaluatedRecord(lrs, cms);
				evaluatedRecords.add(er);
			}
		}

		// Note that the singleRecords collection excluded the query record,
		// so if it is required, then it must be added here, regardless of
		// whether it was included in any merge group
		if (/*mergeGroupIdOfQuery == null &&*/ linkCriteria.isMustIncludeQuery()) {
			// Create a match relationship between the query record and itself
			IRecordHolder<T> q = (IRecordHolder<T>) queryRecord;
			IMatchScore matchScore = new MatchScore(1.0f, Decision3.MATCH, "");
			EvaluatedRecord er = new EvaluatedRecord(q, matchScore);
			evaluatedRecords.add(er);
		}

		// Add any remaining single records as evaluated records
		for (IdentifiableWrapper<T> wrappedCandidate : singleRecords) {
			DataAccessObject<T> candidate =
				(DataAccessObject<T>) wrappedCandidate.getWrapped();
			IRecordHolder<T> irh = (IRecordHolder<T>) candidate;
			QueryCandidatePair<T> qcp = tcs.getQueryCandidatePair(candidate);
			MatchScore matchScore = createMatchScore(qcp);
			EvaluatedRecord er = new EvaluatedRecord(irh, matchScore);
			evaluatedRecords.add(er);
		}

		EvaluatedRecord[] retVal = evaluatedRecords
				.toArray(new EvaluatedRecord[evaluatedRecords.size()]);
		return retVal;
	}

	public OabaLinkageType computeMatchingTask(IRecordCollection qRc,
			RefRecordCollection mRc, NamedConfiguration cmConf) {

		assert qRc != null;
		assert cmConf != null;

		OabaLinkageType retVal;
		if (mRc == null || !StringUtils.nonEmptyString(mRc.getUrl())) {
			retVal = OabaLinkageType.STAGING_DEDUPLICATION;
		} else {
			retVal = OabaLinkageType.valueOf(cmConf.getTask());
		}

		return retVal;
	}

	public JobStatus createJobStatus(BatchJob batchJob) {
		JobStatus retVal = new JobStatus();
		retVal.setStartDate(batchJob.getStarted());
		retVal.setAbortRequestDate(batchJob.getAbortRequested());
		BatchJobStatus batchJobStatus = batchJob.getStatus();
		switch (batchJobStatus) {
		case COMPLETED:
			retVal.setFinishDate(batchJob.getCompleted());
			break;
		case FAILED:
			retVal.setFinishDate(batchJob.getFailed());
			break;
		case ABORTED:
			retVal.setFinishDate(batchJob.getAborted());
			break;
		default:
			assert retVal.getFinishDate() == null;
		}
		return retVal;
	}

	public CompositeMatchScore createCompositeMatchScoreFromMergeGroup(
			MergeGroup<T> mergeGroup) {
		List<MatchScore> list = new ArrayList<>();
		for (EvaluatedPair<T> er : mergeGroup.getGroupPairs()) {
			MatchScore matchScore = createMatchScore(er);
			list.add(matchScore);
		}
		CompositeMatchScore retVal = new CompositeMatchScore(list);
		return retVal;
	}

	/* public */
	public NamedConfiguration createCustomizedConfiguration(
			UrmConfigurationAdapter adapter,
			NamedConfigurationController ncController, DbRecordCollection mRc,
			String modelName, float differThreshold, float matchThreshold,
			int maxNumMatches) throws ConfigException {

		assert adapter != null;
		assert ncController != null;
		assert mRc != null;
		assert StringUtils.nonEmptyString(modelName);
		assert differThreshold >= 0f && differThreshold <= 1f;
		assert matchThreshold >= 0f && matchThreshold <= 1f;
		assert differThreshold <= matchThreshold;

		String ncName;
		try {
			ncName = adapter.getCmsConfigurationName(modelName);
		} catch (DatabaseException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		logger.fine("namedConfiguration: " + ncName);
		if (!StringUtils.nonEmptyString(ncName)) {
			String msg = "Missing named configuration for model configuration '"
					+ modelName + "'";
			logger.severe(msg);
			throw new ConfigException(msg);
		}

		NamedConfiguration nc =
			ncController.findNamedConfigurationByName(ncName);
		if (nc == null) {
			String msg = "Missing named configuration for '" + ncName + "'";
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		NamedConfigurationEntity retVal = new NamedConfigurationEntity(nc);
		retVal.setLowThreshold(differThreshold);
		retVal.setHighThreshold(matchThreshold);

		String jndiReferenceSource = mRc == null ? null : mRc.getUrl();
		if (StringUtils.nonEmptyString(jndiReferenceSource)) {
			retVal.setDataSource(jndiReferenceSource);
			String msg = "Using data source from reference record collection: "
					+ retVal.getDataSource();
			logger.fine(msg);
		} else if (StringUtils.nonEmptyString(retVal.getDataSource())) {
			String msg = "Using data source from named configuration: "
					+ retVal.getDataSource();
			logger.fine(msg);
		} else {
			String msg = "No data source configured";
			logger.severe(msg);
			throw new ConfigException(msg);
		}

		if (mRc instanceof SubsetDbRecordCollection) {
			String referenceSelection =
				((SubsetDbRecordCollection) mRc).getIdsQuery();
			retVal.setReferenceSelection(referenceSelection);
		}

		return retVal;
	}

	public NamedConfiguration createCustomizedConfiguration(
			UrmConfigurationAdapter adapter,
			NamedConfigurationController ncController, IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxSingle) throws ConfigException {

		assert adapter != null;
		assert ncController != null;
		assert qRc != null;
		assert StringUtils.nonEmptyString(modelName);
		assert differThreshold >= 0f && differThreshold <= 1f;
		assert matchThreshold >= 0f && matchThreshold <= 1f;
		assert differThreshold <= matchThreshold;

		String ncName;
		try {
			logger.fine("Model name: '" + modelName + "'");
			ncName = adapter.getCmsConfigurationName(modelName);
			logger.fine("Named configuration: '" + ncName + "'");
		} catch (DatabaseException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		if (!StringUtils.nonEmptyString(ncName)) {
			String msg = "Missing named configuration for model configuration '"
					+ modelName + "'";
			logger.severe(msg);
			throw new ConfigException(msg);
		}

		NamedConfiguration nc =
			ncController.findNamedConfigurationByName(ncName);
		if (nc == null) {
			String msg = "Missing named configuration for '" + ncName + "'";
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		NamedConfigurationEntity retVal = new NamedConfigurationEntity(nc);
		retVal.setLowThreshold(differThreshold);
		retVal.setHighThreshold(matchThreshold);
		retVal.setOabaMaxSingle(maxSingle);

		String jndiQuerySource = null;
		if (qRc instanceof DbRecordCollection) {
			jndiQuerySource = ((DbRecordCollection) qRc).getUrl();
		}
		String jndiReferenceSource = mRc == null ? null : mRc.getUrl();
		// Prefer the reference data source over the query data source
		if (StringUtils.nonEmptyString(jndiReferenceSource)) {
			retVal.setDataSource(jndiReferenceSource);
			String msg = "Using data source from reference record collection: "
					+ retVal.getDataSource();
			logger.fine(msg);
		} else if (StringUtils.nonEmptyString(jndiQuerySource)) {
			retVal.setDataSource(jndiQuerySource);
			String msg = "Using data source from query record collection: "
					+ retVal.getDataSource();
			logger.fine(msg);
		} else if (StringUtils.nonEmptyString(retVal.getDataSource())) {
			String msg = "Using data source from named configuration: "
					+ retVal.getDataSource();
			logger.fine(msg);
		} else {
			String msg = "No data source configured";
			logger.severe(msg);
			throw new ConfigException(msg);
		}

		if (qRc instanceof SubsetDbRecordCollection) {
			String querySelection =
				((SubsetDbRecordCollection) qRc).getIdsQuery();
			retVal.setQuerySelection(querySelection);
		}
		if (mRc instanceof SubsetDbRecordCollection) {
			String referenceSelection =
				((SubsetDbRecordCollection) mRc).getIdsQuery();
			retVal.setReferenceSelection(referenceSelection);
		}

		return retVal;
	}

	public LinkedRecordSet<T> createLinkedRecordSetFromMergeGroup(
			MergeGroup<T> mergeGroup, LinkCriteria linkCriteria) {
		Precondition.assertNonNullArgument("null merge group", mergeGroup);
		Precondition.assertNonNullArgument("null link criteria", linkCriteria);
		Precondition.assertBoolean("inconsistent graph property",
				linkCriteria.getGraphPropType().getName()
						.equals(linkCriteria.getGraphPropType().getName()));

		List<IRecordHolder<T>> list = new ArrayList<>();
		for (DataAccessObject<T> record : mergeGroup.getGroupRecords()) {
			IRecordHolder<T> irh = (IRecordHolder<T>) record;
			list.add(irh);
		}
		LinkedRecordSet<T> retVal =
			new LinkedRecordSet<T>(null, list, linkCriteria);

		return retVal;
	}

	public MatchScore createMatchScore(EvaluatedPair<T> qcp) {
		Precondition.assertNonNullArgument("null pair", qcp);
		float probability = qcp.getMatchProbability();
		Decision d = qcp.getMatchDecision();
		Decision3 d3 = Decision3.valueOf(d.getName());
		String note = qcp.getNotesAsDelimitedString();
		MatchScore retVal = new MatchScore(probability, d3, note);
		return retVal;
	}

	public void removeLinkedRecordsFromSingleRecords(LinkedRecordSet<T> lrs,
			SortedSet<IdentifiableWrapper<T>> singleRecords) {
		Precondition.assertNonNullArgument("null linked record set", lrs);
		Precondition.assertNonNullArgument("null set", singleRecords);
		logger.finer("LinkedRecordSet size: " + lrs.getRecords().length);
		logger.finer("Single record count: " + singleRecords.size());
		for (DataAccessObject<T> record : lrs.getRecords()) {
			IdentifiableWrapper<T> wrapper = new IdentifiableWrapper<>(record);
			boolean isRemoved = singleRecords.remove(wrapper);
			if (logger.isLoggable(Level.FINER)) {
				String msg =
					(isRemoved ? "Removed " : "Did not remove ") + record;
				logger.finer(msg);
			}
		}
		logger.finer("Single record count: " + singleRecords.size());
	}

}
