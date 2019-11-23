/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
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
import com.choicemaker.client.api.WellKnownGraphProperties;
import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.CompositeMatchScore;
import com.choicemaker.cm.urm.base.CompositeRecord;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.Decision3;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
//import com.choicemaker.cm.urm.base.GraphProperty;
import com.choicemaker.cm.urm.base.IMatchScore;
import com.choicemaker.cm.urm.base.IRecord;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.IRecordHolder;
import com.choicemaker.cm.urm.base.ISingleRecord;
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

	public static final int DEFAULT_FRACTION_COMPLETE = 0;

	public static final MatchScore PERFECT_SCORE =
		new MatchScore(1.0f, Decision3.MATCH, "");

	private static boolean _assertsEnabled = false;
	static {
		assert _assertsEnabled = true;
	}
	public static final boolean assertsEnabled = _assertsEnabled;

	public static JobStatus getJobStatus(BatchJob urmJob)
			throws ArgumentException, ConfigException, CmRuntimeException,
			RemoteException {
		JobStatus retVal = null;
		if (urmJob != null) {
			retVal = new JobStatus();
			retVal.setStepDescription(urmJob.getDescription());
			retVal.setFinishDate(urmJob.getCompleted());
			retVal.setJobId(urmJob.getId());
			retVal.setStartDate(urmJob.getStarted());
			retVal.setStatus(urmJob.getStatus().toString());
			retVal.setAbortRequestDate(urmJob.getAborted());
			// retVal.setErrorDescription(null);
			// retVal.setStepId(0)
			// retVal.setStepStartDate(null)
			retVal.setFractionComplete(DEFAULT_FRACTION_COMPLETE);
			retVal.setTrackingId(urmJob.getExternalId());
		}
		return retVal;
	}

	public UrmEjbAssist() {
	}

	public void assertValid(List<EvaluatedRecord> records) {
		if (assertsEnabled) {
			assert records != null;
			boolean isValid = true;
			for (int i = 0; i < records.size(); i++) {
				EvaluatedRecord record = records.get(i);
				if (record == null) {
					isValid = false;
					logger.warning("Record " + i + " is null");
				}
				if (!equalRecordAndScoreCount(record)) {
					isValid = false;
					String msg =
						"Unequal record and score counts for evaluated record "
								+ i;
					logger.severe(msg);
					if (logger.isLoggable(Level.FINE)) {
						msg = "Evaluated Record index: " + i + ": "
								+ dumpInfo(record);
						logger.fine(msg);
					}
				}
			}
			assert isValid;
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
		assertValid(evaluatedRecords);
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
				 * them to the evaluated record list. A group containing the
				 * query record must be handled specially
				 */
				logger.fine("Skipping merge group " + mergeGroup.getGroupId());

			} else {
				/*
				 * Otherwise, convert a merge group to a linked record set and
				 * add it to the evaluated record list. Remove each candidate
				 * record from the set of singleRecords.
				 */
				LinkedRecordSet<T> lrs;
				CompositeMatchScore cms;
				if (mergeGroup.containsRecord(queryRecord)) {
					// Create a match relationship between the query record and
					// itself and add it as the first evaluated record
					logger.finest(
							"Adding query record as first evaluated record");
					IRecordHolder<T> q = (IRecordHolder<T>) queryRecord;
					IMatchScore matchScore = PERFECT_SCORE;
					EvaluatedRecord er = new EvaluatedRecord(q, matchScore);
					evaluatedRecords.add(0, er);
					assertValid(evaluatedRecords);
					logger.finer(
							"Added query record as first evaluated record");
				}
				logger.fine("Add merge group " + mergeGroup.getGroupId()
						+ " as a LinkdRecordSet");
				lrs = createLinkedRecordSetFromMergeGroup(queryRecord,
						mergeGroup, linkCriteria);
				cms = createCompositeMatchScoreFromMergeGroup(queryRecord,
						mergeGroup);
				assert lrs.getRecords() != null;
				assert cms.getInnerScores() != null;
				assert lrs.getRecords().length == cms.getInnerScores().length;
				removeLinkedRecordsFromSingleRecords(lrs, singleRecords);
				EvaluatedRecord er = new EvaluatedRecord(lrs, cms);
				evaluatedRecords.add(er);
				assertValid(evaluatedRecords);
			}
		}

		// Note that the singleRecords collection excluded the query record,
		// so if it is required, then it must be added here, unless it was
		// already handled as part of some other merge group
		if (mergeGroupIdOfQuery == null && linkCriteria.isMustIncludeQuery()) {
			// Create a match relationship between the query record and itself
			// and add it as the first evaluated record
			logger.finest("Adding query record as first evaluated record");
			IRecordHolder<T> q = (IRecordHolder<T>) queryRecord;
			IMatchScore matchScore = PERFECT_SCORE;
			EvaluatedRecord er = new EvaluatedRecord(q, matchScore);
			evaluatedRecords.add(0, er);
			assertValid(evaluatedRecords);
			logger.finer("Added query record as first evaluated record");
		}

		// At this point, the query record is the first evaluated if
		// (1) it was part of some merge group or (2) it was required.
		if (assertsEnabled && evaluatedRecords.size() > 0) {
			if (mergeGroupIdOfQuery != null
					|| linkCriteria.isMustIncludeQuery()) {
				EvaluatedRecord firstER = evaluatedRecords.get(0);
				assert PERFECT_SCORE.equals(firstER.getScore());
				IRecord<?> firstIR = firstER.getRecord();
				assert firstIR != null;
				Comparable<?> firstId = firstIR.getId();
				if (firstId == null) {
					assert queryRecord.getId() == null;
				} else {
					assert firstId.equals(queryRecord.getId());
				}
			}
		}

		// Add any remaining single records as evaluated records.
		// Exclude the query record since it is already be added.
		for (IdentifiableWrapper<T> wrappedCandidate : singleRecords) {
			DataAccessObject<T> candidate =
				(DataAccessObject<T>) wrappedCandidate.getWrapped();
			IRecordHolder<T> irh = (IRecordHolder<T>) candidate;
			QueryCandidatePair<T> qcp = tcs.getQueryCandidatePair(candidate);
			MatchScore matchScore = createMatchScore(qcp);
			EvaluatedRecord er = new EvaluatedRecord(irh, matchScore);
			evaluatedRecords.add(er);
			assertValid(evaluatedRecords);
		}

		assertValid(evaluatedRecords);
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

	/**
	 * Create a composite score from scores of candidate records against the
	 * query record.
	 */
	public CompositeMatchScore createCompositeMatchScoreFromMergeGroup(
			DataAccessObject<T> queryRecord, MergeGroup<T> mergeGroup0) {
		List<MatchScore> scores = new ArrayList<>();
		for (EvaluatedPair<T> ep : mergeGroup0.getGroupPairs()) {
			if (!ep.getRecord1().equals(queryRecord)
					&& !ep.getRecord2().equals(queryRecord)) {
				// Neither record matches the query record
				continue;
			}
			if (ep.getRecord1().equals(queryRecord)
					&& ep.getRecord2().equals(queryRecord)) {
				// Skip a match of the query record against itself
				continue;
			}
			MatchScore matchScore = createMatchScore(ep);
			scores.add(matchScore);
		}
		CompositeMatchScore retVal = new CompositeMatchScore(scores);
		return retVal;
	}

	// public CompositeMatchScore createCompositeMatchScoreFromMergeGroup(
	// MergeGroup<T> mergeGroup) {
	// List<MatchScore> list = new ArrayList<>();
	// for (EvaluatedPair<T> er : mergeGroup.getGroupPairs()) {
	// MatchScore matchScore = createMatchScore(er);
	// list.add(matchScore);
	// }
	// CompositeMatchScore retVal = new CompositeMatchScore(list);
	// return retVal;
	// }
	
	public static final LinkCriteria DEFAULT_LINK_CRITERIA =
			new LinkCriteria(WellKnownGraphProperties.GP_SCM, true);
	
	public static final AnalysisResultFormat DEFAULT_ANALYSIS_RESULT_FORMAT = 
			AnalysisResultFormat.SORT_BY_HOLD_GROUP;

	public NamedConfiguration createCustomizedConfiguration(
			UrmConfigurationAdapter adapter,
			NamedConfigurationController ncController, DbRecordCollection mRc,
			String modelName, float differThreshold, float matchThreshold,
			int oabaMaxSingle) throws ConfigException {

		final IRecordCollection qRc = null;
		NamedConfiguration retVal = createCustomizedConfiguration(adapter,
				ncController, qRc, mRc, modelName, differThreshold,
				matchThreshold, oabaMaxSingle, DEFAULT_LINK_CRITERIA,
				DEFAULT_ANALYSIS_RESULT_FORMAT);
		return retVal;
	}

	public NamedConfiguration createCustomizedConfiguration(
			UrmConfigurationAdapter adapter,
			NamedConfigurationController ncController, IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int oabaMaxSingle)
			throws ConfigException {
		NamedConfiguration retVal = createCustomizedConfiguration(adapter,
				ncController, qRc, mRc, modelName, differThreshold,
				matchThreshold, oabaMaxSingle, DEFAULT_LINK_CRITERIA,
				DEFAULT_ANALYSIS_RESULT_FORMAT);
		return retVal;
	}

	public NamedConfiguration createCustomizedConfiguration(
			UrmConfigurationAdapter adapter,
			NamedConfigurationController ncController, IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int oabaMaxSingle, LinkCriteria c,
			AnalysisResultFormat serializationFormat)
			throws ConfigException {

		assert adapter != null;
		assert ncController != null;
		assert mRc != null;
		assert StringUtils.nonEmptyString(modelName);
		assert differThreshold >= 0f && differThreshold <= 1f;
		assert matchThreshold >= 0f && matchThreshold <= 1f;
		assert differThreshold <= matchThreshold;

		String ncName = null;
		try {
			logger.fine("Model name: '" + modelName + "'");
			ncName = adapter.getCmsConfigurationName(modelName);
			logger.fine("Named configuration: '" + ncName + "'");
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
		retVal.setOabaMaxSingle(oabaMaxSingle);
		retVal.setTransitivityGraph(c.getGraphPropType().getName());
		retVal.setTransitivityFormat(serializationFormat.getDisplayName());

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

	/**
	 * Create a linked record set from candidate records, excluding the query
	 * record.
	 */
	public LinkedRecordSet<T> createLinkedRecordSetFromMergeGroup(
			DataAccessObject<T> queryRecord, MergeGroup<T> mergeGroup,
			LinkCriteria linkCriteria) {
		Precondition.assertNonNullArgument("null query record", queryRecord);
		Precondition.assertNonNullArgument("null merge group", mergeGroup);
		Precondition.assertNonNullArgument("null link criteria", linkCriteria);
		Precondition.assertBoolean("inconsistent graph property",
				linkCriteria.getGraphPropType().getName()
						.equals(linkCriteria.getGraphPropType().getName()));

		List<IRecordHolder<T>> list = new ArrayList<>();
		for (DataAccessObject<T> record : mergeGroup.getGroupRecords()) {
			if (queryRecord.equals(record)) {
				continue;
			}
			IRecordHolder<T> irh = (IRecordHolder<T>) record;
			list.add(irh);
		}
		LinkedRecordSet<T> retVal =
			new LinkedRecordSet<T>(null, list, linkCriteria);
		return retVal;
	}

	// public LinkedRecordSet<T> createLinkedRecordSetFromMergeGroup(
	// MergeGroup<T> mergeGroup, LinkCriteria linkCriteria) {
	// Precondition.assertNonNullArgument("null merge group", mergeGroup);
	// Precondition.assertNonNullArgument("null link criteria", linkCriteria);
	// Precondition.assertBoolean("inconsistent graph property",
	// linkCriteria.getGraphPropType().getName()
	// .equals(linkCriteria.getGraphPropType().getName()));
	//
	// List<IRecordHolder<T>> list = new ArrayList<>();
	// for (DataAccessObject<T> record : mergeGroup.getGroupRecords()) {
	// IRecordHolder<T> irh = (IRecordHolder<T>) record;
	// list.add(irh);
	// }
	// LinkedRecordSet<T> retVal =
	// new LinkedRecordSet<T>(null, list, linkCriteria);
	//
	// return retVal;
	// }

	public MatchScore createMatchScore(EvaluatedPair<T> qcp) {
		Precondition.assertNonNullArgument("null pair", qcp);
		float probability = qcp.getMatchProbability();
		Decision d = qcp.getMatchDecision();
		Decision3 d3 = Decision3.valueOf(d.getName());
		String note = qcp.getNotesAsDelimitedString();
		MatchScore retVal = new MatchScore(probability, d3, note);
		return retVal;
	}

	public String dumpInfo(EvaluatedRecord er) {
		final String INDENT = "  ";
		final String TWODENT = INDENT + INDENT;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		if (er == null) {
			pw.println("<null>");
		} else {
			IRecord<?> r = er.getRecord();
			pw.print("EvaluatedRecord info");
			if (r instanceof ISingleRecord<?>) {
				ISingleRecord<?> isr = (ISingleRecord<?>) r;
				pw.println(INDENT + "Record id: " + isr.getId());
				MatchScore ms = (MatchScore) er.getScore();
				pw.println(INDENT + "Score: " + ms.toString());
			} else if (r instanceof CompositeRecord<?>) {
				CompositeRecord<?> cr = (CompositeRecord<?>) r;
				pw.println(INDENT + "Composite id: " + cr.getId());
				if (cr instanceof LinkedRecordSet<?>) {
					LinkedRecordSet<?> lrs = (LinkedRecordSet<?>) cr;
					pw.println(INDENT + "Linkage criteria: "
							+ lrs.getCriteria().toString());
				}
				IRecord<?>[] records = cr.getRecords();
				pw.println(INDENT + "Records: " + records.length);
				CompositeMatchScore cms = (CompositeMatchScore) er.getScore();
				MatchScore[] scores = cms.getInnerScores();
				pw.println(INDENT + "Scores: " + scores.length);
				final int limit = Math.max(records.length, scores.length);
				for (int i = 0; i < limit; i++) {
					pw.print(TWODENT);
					if (i < records.length) {
						pw.print("Record[" + i + "]: " + records[i].getId()
								+ " ");
					}
					if (i < scores.length) {
						pw.print("Score[" + i + "]: " + scores[i].toString());
					}
					pw.println();
				}
			} else {
				pw.println("Unexpected record type: " + r.getClass().getName());
			}
		}
		String reVal = sw.toString();
		return reVal;
	}

	public boolean equalRecordAndScoreCount(EvaluatedRecord evaluatedRecord) {
		boolean retVal;
		IRecord<?> r = evaluatedRecord.getRecord();
		if (r instanceof ISingleRecord<?>) {
			retVal = true;
		} else if (r instanceof CompositeRecord<?>) {
			CompositeRecord<?> cr = (CompositeRecord<?>) r;
			IRecord<?>[] records = cr.getRecords();
			CompositeMatchScore cms =
				(CompositeMatchScore) evaluatedRecord.getScore();
			MatchScore[] scores = cms.getInnerScores();
			retVal = records.length == scores.length;
		} else {
			logger.warning("Unexpected record type: " + r.getClass().getName());
			retVal = false;
		}
		return retVal;
	}

	public boolean equalRecordAndScoreCount(
			EvaluatedRecord[] evaluatedRecords) {
		Precondition.assertNonNullArgument("null records", evaluatedRecords);
		boolean retVal = true;
		for (int i = 0; i < evaluatedRecords.length; i++) {
			EvaluatedRecord er = evaluatedRecords[i];
			if (!equalRecordAndScoreCount(er)) {
				retVal = false;
			}
		}
		return retVal;
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

	// public MergeGroup<T> stripQueryRecordFromMergeGroup(
	// DataAccessObject<T> queryRecord, MergeGroup<T> mergeGroup) {
	// IGraphProperty gp = mergeGroup.getGraphConnectivity();
	// List<EvaluatedPair<T>> pairs = mergeGroup.getGroupPairs();
	// for (EvaluatedPair<T> pair : pairs) {
	//
	// }
	// MergeGroupBean<T> retVal = new MergeGroupBean<>();
	// public MergeGroupBean(IGraphProperty mergeConnectivity,
	// List<EvaluatedPair<T>> pairs) {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
