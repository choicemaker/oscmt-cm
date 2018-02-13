/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.ejb.EJB;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.base.SubsetDbRecordCollection;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigurationEntity;
import com.choicemaker.util.StringUtils;

class UrmEjbAssist<T extends Comparable<T> & Serializable> {

	private static final Logger logger =
		Logger.getLogger(UrmEjbAssist.class.getName());

	@EJB(lookup = "java:app/NamedConfigurationControllerBean/com.choicemaker.cms.api.NamedConfigurationController")
	private UrmConfigurationAdapter adapter;

	@EJB(lookup = "java:app/UrmConfigurationSingleton")
	private NamedConfigurationController ncController;

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

	NamedConfiguration createCustomizedConfiguration(IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxSingle) throws ConfigException {

		assert qRc != null;
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
		if (StringUtils.nonEmptyString(ncName)) {
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

	public NamedConfiguration createCustomizedConfiguration(
			DbRecordCollection mRc, String modelName,
			float differThreshold, float matchThreshold, int maxNumMatches) throws ConfigException {
		
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
		if (StringUtils.nonEmptyString(ncName)) {
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

	public EvaluatedRecord[] computeEvaluatedRecords(
			MatchCandidates<T> matchCandidates) {
		// TODO Auto-generated method stub
		return null;
	}

	public EvaluatedRecord[] computeEvaluatedRecords(
			TransitiveCandidates<T> transitiveCandidates) {
		// TODO Auto-generated method stub
		return null;
	}

	public IGraphProperty computeGraphProperty(LinkCriteria linkCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean computeQueryInclusion(LinkCriteria linkCriteria) {
		// TODO Auto-generated method stub
		return false;
	}

}
