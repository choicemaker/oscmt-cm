/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.naming.NamingException;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.urm.BatchRecordMatcher;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.base.SubsetDbRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigConversion;
import com.choicemaker.cms.ejb.NamedConfigurationEntity;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

@Remote
public class BatchRecordMatcherBean implements BatchRecordMatcher {

	private static final String VERSION = "2.7.1";

	private static final Logger logger =
		Logger.getLogger(BatchRecordMatcherBean.class.getName());

	@EJB(lookup = "java:app/BatchMatchingBean/com.choicemaker.cms.api.BatchMatching")
	private BatchMatching delegate;

	@EJB(lookup = "java:app/NamedConfigurationControllerBean/com.choicemaker.cms.api.NamedConfigurationController")
	private UrmConfigurationAdapter adapter;

	@EJB(lookup = "java:app/UrmConfigurationSingleton")
	private NamedConfigurationController ncController;

	@Override
	public boolean abortJob(long jobId) {
		throw new Error("not yet implemented");
	}

	@Override
	public boolean cleanJob(long jobID) throws CmRuntimeException {
		throw new Error("not yet implemented");
	}

	protected OabaLinkageType computeMatchingTask(IRecordCollection qRc,
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

	protected NamedConfiguration createCustomizedConfiguration(
			IRecordCollection qRc, RefRecordCollection mRc, String modelName,
			float differThreshold, float matchThreshold, int maxSingle)
			throws ConfigException {

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

	@Override
	public long[] getJobList() throws ArgumentException, ConfigException,
			CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public Iterator<?> getResultIter(long jobId)
			throws RecordCollectionException, ArgumentException,
			CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public Iterator<?> getResultIter(RefRecordCollection rc)
			throws RecordCollectionException, ArgumentException,
			CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public String getVersion(Object context) throws RemoteException {
		return VERSION;
	}

	@Override
	public boolean resumeJob(long jobId) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public long startMatching(IRecordCollection qRc, RefRecordCollection mRc,
			String modelName, float differThreshold, float matchThreshold,
			int maxSingle, String trackingId)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {

		Precondition.assertNonNullArgument("null queries", qRc);
		// Precondition.assertNonNullArgument("null references", mRc);
		Precondition.assertNonEmptyString("null or empty model", modelName);
		Precondition.assertBoolean("invalid differ threshold",
				differThreshold >= 0f && differThreshold <= 1f);
		Precondition.assertBoolean("invalid match threshold",
				matchThreshold >= 0f && matchThreshold <= 1f);
		Precondition.assertBoolean("invalid thresholds (differ > match)",
				differThreshold <= matchThreshold);

		NamedConfiguration cmConf = createCustomizedConfiguration(qRc, mRc,
				modelName, differThreshold, matchThreshold, maxSingle);
		OabaLinkageType task = computeMatchingTask(qRc, mRc, cmConf);
		boolean isLinkage = OabaLinkageType.isLinkage(task);

		long retVal = Integer.MIN_VALUE;
		OabaParameters batchParams = null;
		OabaSettings oabaSettings = null;
		ServerConfiguration serverConfig = null;
		try {
			batchParams =
				NamedConfigConversion.createOabaParameters(cmConf, isLinkage);
			assert batchParams != null;

			oabaSettings = NamedConfigConversion.createOabaSettings(cmConf);
			assert oabaSettings != null;

			serverConfig =
				NamedConfigConversion.createServerConfiguration(cmConf);
			assert serverConfig != null;

			if (isLinkage) {
				retVal = delegate.startLinkage(trackingId, batchParams,
						oabaSettings, serverConfig, null);
			} else {
				retVal = delegate.startDeduplication(trackingId, batchParams,
						oabaSettings, serverConfig, null);
			}
		} catch (NamingException | ServerConfigurationException
				| URISyntaxException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		assert retVal != Integer.MIN_VALUE;

		return retVal;
	}

	@Override
	public boolean suspendJob(long jobId) {
		throw new Error("not implemented");
	}

}
