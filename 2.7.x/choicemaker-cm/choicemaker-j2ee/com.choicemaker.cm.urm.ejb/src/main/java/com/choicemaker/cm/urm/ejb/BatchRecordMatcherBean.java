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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.urm.api.BatchRecordMatcher;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.ejb.NamedConfigConversion;
import com.choicemaker.util.Precondition;

@Stateless
@Remote(BatchRecordMatcher.class )
public class BatchRecordMatcherBean implements BatchRecordMatcher {

	private static final String VERSION = "2.7.1";

	private static final Logger logger =
		Logger.getLogger(BatchRecordMatcherBean.class.getName());

	@EJB(lookup = "java:app/com.choicemaker.cms.ejb/BatchMatchingBean!com.choicemaker.cms.api.BatchMatching")
	private BatchMatching delegate;

	@EJB(lookup = "java:module/UrmConfigurationSingleton")
	private UrmConfigurationAdapter adapter;

	@EJB(lookup = "java:app/com.choicemaker.cms.ejb/NamedConfigurationControllerBean!com.choicemaker.cms.api.NamedConfigurationController")
	private NamedConfigurationController ncController;

	@EJB(lookup = "java:app/com.choicemaker.cms.ejb/UrmBatchControllerBean!com.choicemaker.cms.api.UrmBatchController")
	private UrmBatchController urmBatchController;

	private UrmEjbAssist<?> assist = new UrmEjbAssist<>();

	@Override
	public boolean abortJob(long jobId) {
		boolean delegateRetVal = delegate.abortJob(jobId);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.abortJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public boolean cleanJob(long jobID) throws CmRuntimeException {
		boolean delegateRetVal = delegate.cleanJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.cleanJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
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
	public boolean resumeJob(long jobID) {
		boolean delegateRetVal = delegate.resumeJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.resumeJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
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

		NamedConfiguration cmConf =
			assist.createCustomizedConfiguration(adapter, ncController, qRc,
					mRc, modelName, differThreshold, matchThreshold, maxSingle);
		OabaLinkageType task = assist.computeMatchingTask(qRc, mRc, cmConf);
		boolean isLinkage = OabaLinkageType.isLinkage(task);

		long retVal = Integer.MIN_VALUE;
		try {
			OabaParameters batchParams = null;
			batchParams =
				NamedConfigConversion.createOabaParameters(cmConf, isLinkage);
			assert batchParams != null;

			OabaSettings oabaSettings = null;
			oabaSettings = NamedConfigConversion.createOabaSettings(cmConf);
			assert oabaSettings != null;

			ServerConfiguration serverConfig = null;
			serverConfig =
				NamedConfigConversion.createServerConfiguration(cmConf);
			assert serverConfig != null;

			if (isLinkage) {
				retVal = delegate.startLinkage(trackingId, batchParams,
						oabaSettings, serverConfig);
			} else {
				retVal = delegate.startDeduplication(trackingId, batchParams,
						oabaSettings, serverConfig);
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
		boolean delegateRetVal = delegate.suspendJob(jobId);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.suspendJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public void copyResult(long jobID, RefRecordCollection resRc)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		assist.copyResult(urmBatchController, jobID, resRc);
	}

}
