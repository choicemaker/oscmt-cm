/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.client.api.MergeGroup;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.args.OabaLinkageType;
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
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.base.LinkedRecordSet;
import com.choicemaker.cm.urm.base.MatchScore;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.base.SubsetDbRecordCollection;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigurationEntity;
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

	OabaLinkageType computeMatchingTask(IRecordCollection qRc,
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

	NamedConfiguration createCustomizedConfiguration(
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

	/* public */
	NamedConfiguration createCustomizedConfiguration(
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
			// FIXME stubbed note
			String note = "FIXME placeholder note";
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
		List<QueryCandidatePair<T>> evaluatedPairs = tcs.getQueryCandidatePairs();

		boolean containsQuery = false;
		List<EvaluatedRecord> evaluatedRecords = new ArrayList<>();
		for (MergeGroup<T> mergeGroup : mergeGroups) {

			if (mergeGroup.containsRecord(queryRecord)) {
				assert !containsQuery;
				containsQuery = true;
				LinkedRecordSet<T> lrs =
					createLinkedRecordSetFromMergeGroup(mergeGroup);
				CompositeMatchScore cms = null; // FIXME
				EvaluatedRecord er = new EvaluatedRecord(lrs, cms);
				evaluatedRecords.add(er);

			} else if (!mergeGroup.containsRecord(queryRecord)
					&& linkCriteria.isMustIncludeQuery()) {
				/*
				 * Break up a merge groups that does not include the query
				 * record
				 */
				// TODO stub
				throw new Error("not yet implemented");

			} else {
				assert !linkCriteria.isMustIncludeQuery();
				assert !mergeGroup.containsRecord(queryRecord);
				LinkedRecordSet<T> lrs =
					createLinkedRecordSetFromMergeGroup(mergeGroup);
				CompositeMatchScore cms = null; // FIXME
				EvaluatedRecord er = new EvaluatedRecord(lrs, cms);
				evaluatedRecords.add(er);
			}
		}

		if (!containsQuery && linkCriteria.isMustIncludeQuery()) {
			// Create a match relationship between the query record and itself
			IRecordHolder<T> q = (IRecordHolder<T>) queryRecord;
			IMatchScore matchScore = new MatchScore(1.0f, Decision3.MATCH, "");
			EvaluatedRecord er = new EvaluatedRecord(q, matchScore);
			evaluatedRecords.add(er);
		}

		// Exclude query-to-candidate match relationships for candidates
		// that are part of merge groups
		// TODO stub
		throw new Error("not yet implemented");

	}

	private LinkedRecordSet<T> createLinkedRecordSetFromMergeGroup(
			MergeGroup<T> mergeGroup) {
		// TODO Auto-generated method stub
		return null;
	}

}
