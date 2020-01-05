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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobRigor;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.IndexedPropertyController;
import com.choicemaker.cm.oaba.api.MatchPairInfoBean;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.ejb.OabaJobJPA;
import com.choicemaker.cm.transitivity.ejb.TransitiveGroupInfoBean;
import com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA;
import com.choicemaker.cm.urm.api.BatchMatchAnalyzer;
import com.choicemaker.cm.urm.api.RESULT_FILE_TYPE;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.LinkCriteria;
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
import com.choicemaker.util.StringUtils;

/**
 * Delegates to CMS BatchMatching bean.
 */
@Stateless
@Remote(BatchMatchAnalyzer.class)
public class BatchMatchAnalyzerBean implements BatchMatchAnalyzer {

	private static final String VERSION = "2.7.1";

	private static final Logger logger =
		Logger.getLogger(BatchMatchAnalyzerBean.class.getName());

	// @EJB(lookup = "java:app/com.choicemaker.cms.ejb/BatchMatchingBean!com.choicemaker.cms.api.BatchMatching")
	@EJB(beanName = "BatchMatchingBean")
	private BatchMatching delegate;

	// @EJB(lookup = "java:module/UrmConfigurationSingleton")
	@EJB(beanName = "UrmConfigurationSingleton")
	private UrmConfigurationAdapter adapter;

	// @EJB(lookup = "java:app/com.choicemaker.cms.ejb/NamedConfigurationControllerBean!com.choicemaker.cms.api.NamedConfigurationController")
	@EJB(beanName = "NamedConfigurationControllerBean")
	private NamedConfigurationController ncController;

	// @EJB(lookup = "java:app/com.choicemaker.cms.ejb/UrmBatchControllerBean!com.choicemaker.cms.api.UrmBatchController")
	@EJB(beanName = "UrmBatchControllerBean")
	private UrmBatchController urmBatchController;

	// @EJB(lookup = "java:app/com.choicemaker.cm.batch.ejb/IndexedPropertyControllerBean!com.choicemaker.cm.batch.api.IndexedPropertyController")
	@EJB(beanName = "IndexedPropertyControllerBean")
	private IndexedPropertyController idxPropController;

	private UrmEjbAssist<?> assist = new UrmEjbAssist<>();

	@Override
	public boolean abortJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		boolean delegateRetVal = delegate.abortJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.abortJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public boolean cleanJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		boolean delegateRetVal = delegate.cleanJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.cleanJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		BatchJob urmJob = urmBatchController.findUrmJob(jobID);
		JobStatus retVal = UrmEjbAssist.getJobStatus(urmJob);
		return retVal;
	}

	@Override
	public List<String> getResultFileNames(long jobID, RESULT_FILE_TYPE type)
			throws CmRuntimeException {
		Precondition.assertNonNullArgument("result file type can not be null",
				type);

		String jpaType;
		String propertyName;
		switch (type) {
		case MATCH:
			jpaType = OabaJobJPA.DISCRIMINATOR_VALUE;
			propertyName = MatchPairInfoBean.PN_OABA_MATCH_RESULT_FILE;
			break;
		case TRANSMATCH:
			jpaType = TransitivityJobJPA.DISCRIMINATOR_VALUE;
			propertyName = TransitiveGroupInfoBean.PN_TRANSMATCH_PAIR_FILE;
			break;
		case TRANSGROUP:
			jpaType = TransitivityJobJPA.DISCRIMINATOR_VALUE;
			propertyName = TransitiveGroupInfoBean.PN_TRANSMATCH_GROUP_FILE;
			break;
		default:
			throw new Error("unexpected result file type: " + type);
		}
		assert StringUtils.nonEmptyString(jpaType);
		assert StringUtils.nonEmptyString(propertyName);

		List<BatchJob> targetJobs = new ArrayList<>();
		List<BatchJob> childJobs =
			urmBatchController.findAllLinkedByUrmId(jobID);
		for (BatchJob childJob : childJobs) {
			String sType = childJob.getBatchJobType();
			if (jpaType.equals(sType)) {
				targetJobs.add(childJob);
			}
		}

		if (targetJobs.size() == 0) {
			String msg0 = "No %s jobs associated with URM job id '%d'";
			String msg = String.format(msg0, type, jobID);
			throw new CmRuntimeException(msg);
		} else if (targetJobs.size() > 1) {
			List<Long> targetJobIds = new ArrayList<>();
			for (BatchJob targetJob : targetJobs) {
				long targetJobId = targetJob.getId();
				targetJobIds.add(targetJobId);
			}
			String msg0 =
				"Multiple %s jobs (%s) associated with URM job id '%d'";
			String msg =
				String.format(msg0, type, targetJobIds.toString(), jobID);
			throw new CmRuntimeException(msg);
		}
		assert targetJobs.size() == 1;

		final BatchJob targetJob = targetJobs.get(0);
		final long targetJobId = targetJob.getId();
		final BatchJobStatus targetStatus = targetJob.getStatus();
		if (BatchJobStatus.COMPLETED != targetStatus) {
			String msg0 = "%s job %d status ('%s') is not COMPLETED";
			String msg = String.format(msg0, type, targetJobId, targetStatus);
			throw new CmRuntimeException(msg);
		}

		Map<Integer, String> fileNames =
			idxPropController.findIndexedProperties(targetJob, propertyName);
		Object[] oIndices = fileNames.keySet().toArray();
		Integer[] indices =
			Arrays.copyOf(oIndices, oIndices.length, Integer[].class);
		Arrays.sort(indices);

		List<String> retVal = new ArrayList<>();
		for (int index : indices) {
			String fileName = fileNames.get(index);
			retVal.add(fileName);
		}

		return retVal;
	}

	@Override
	public Iterator<?> getResultIterator(long jobId, AnalysisResultFormat s)
			throws RecordCollectionException, ArgumentException,
			CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public Iterator<?> getResultIterator(RefRecordCollection rc,
			AnalysisResultFormat s) throws RecordCollectionException,
			ArgumentException, CmRuntimeException, RemoteException {
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
	public long startAnalysis(long jobId, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId)
			throws ModelException, ConfigException, ArgumentException,
			CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public long startAnalysis(long jobId, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId,
			BatchJobRigor rigor) throws ModelException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public long startMatchAndAnalysis(IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxSingle, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId)
			throws RecordCollectionException, ArgumentException,
			ConfigException, ModelException, CmRuntimeException,
			RemoteException {
		Precondition.assertNonNullArgument("null queries", qRc);
		// Precondition.assertNonNullArgument("null references", mRc);
		Precondition.assertNonEmptyString("null or empty model", modelName);
		Precondition.assertBoolean("invalid differ threshold",
				differThreshold >= 0f && differThreshold <= 1f);
		Precondition.assertBoolean("invalid match threshold",
				matchThreshold >= 0f && matchThreshold <= 1f);
		Precondition.assertBoolean("invalid thresholds (differ > match)",
				differThreshold <= matchThreshold);
		Precondition.assertNonNullArgument("null link criteria", c);
		Precondition.assertNonNullArgument("null result format",
				serializationFormat);

		NamedConfiguration cmConf =
			assist.createCustomizedConfiguration(adapter, ncController, qRc,
					mRc, modelName, differThreshold, matchThreshold, maxSingle,
					c, serializationFormat);
		OabaLinkageType task = assist.computeMatchingTask(qRc, mRc, cmConf);
		boolean isLinkage = OabaLinkageType.isLinkage(task);

		long retVal = Integer.MIN_VALUE;
		try {
			TransitivityParameters tp = null;
			tp = NamedConfigConversion.createTransitivityParameters(cmConf,
					isLinkage);
			assert tp != null;

			OabaSettings oabaSettings = null;
			oabaSettings = NamedConfigConversion.createOabaSettings(cmConf);
			assert oabaSettings != null;

			ServerConfiguration serverConfig = null;
			serverConfig =
				NamedConfigConversion.createServerConfiguration(cmConf);
			assert serverConfig != null;

			if (isLinkage) {
				retVal = delegate.startLinkageAndAnalysis(trackingId, tp,
						oabaSettings, serverConfig);
			} else {
				retVal = delegate.startDeduplicationAndAnalysis(trackingId, tp,
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
	public boolean suspendJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		boolean delegateRetVal = delegate.suspendJob(jobID);
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
