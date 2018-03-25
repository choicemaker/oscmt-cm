/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.ejb;

import static com.choicemaker.cm.args.ProcessingEventBean.DONE;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.naming.NamingException;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.BatchProcessingNotification;
import com.choicemaker.cm.batch.api.WorkflowListener;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaService;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.data.OabaNotification;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cm.transitivity.api.TransitivityService;
import com.choicemaker.cm.transitivity.ejb.TransitivityNotification;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.api.remote.BatchMatchingRemote;

@Singleton
@Local({
		BatchMatching.class, WorkflowListener.class })
@Remote(BatchMatchingRemote.class)
public class BatchMatchingBean implements BatchMatching, WorkflowListener {

	// private static final long serialVersionUID = 271L;

	private static final String SOURCE_CLASS =
		BatchMatchingBean.class.getSimpleName();

	private static final Logger logger =
		Logger.getLogger(BatchMatchingBean.class.getName());

	// Instance data is OK because EJB is a singleton
	private Set<Long> transtivityAnalysisPending = new HashSet<>();

	@EJB
	private UrmBatchController urmJobManager;

	@EJB
	private OabaService oabaService;

	@EJB
	private TransitivityService transService;

	@EJB
	private OabaJobManager oabaJobController;

	@EJB
	private TransitivityJobManager transJobController;

	@EJB
	private TransitivityParametersController paramsController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private ServerConfigurationController serverManager;

	// -- Access control

	@Lock(LockType.READ)
	public boolean isTransitivityAnalysisPending(long oabaId) {
		boolean retVal = transtivityAnalysisPending.contains(oabaId);
		logger.info("Transitivity pending for job " + oabaId + ": " + retVal);
		return retVal;
	}

	@Lock(LockType.WRITE)
	public void setTransitivityAnalysisPending(long oabaId, boolean isPending) {
		logger.info("Setting transitivity-pending for job " + oabaId + ": "
				+ isPending);
		if (isPending) {
			transtivityAnalysisPending.add(oabaId);
		} else {
			transtivityAnalysisPending.remove(oabaId);
		}
	}

	// -- Batch match and analysis

	@Override
	public long startDeduplicationAndAnalysis(String externalID,
			TransitivityParameters tp, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration)
			throws ServerConfigurationException {

		final String METHOD = "startDeduplicationAndAnalysis";
		logger.entering(SOURCE_CLASS, METHOD);

		// Create and persist the job and its associated objects
		BatchJob urmJob = urmJobManager.createPersistentUrmJob(externalID);
		final long retVal = urmJob.getId();
		assert urmJob.isPersistent();
		logger.info("Offline batch deduplication and analysis (job id: "
				+ retVal + ")");
		if (tp != null && !tp.isPersistent()) {
			tp = paramsController.save(tp);
			logger.info("Non-persistent OabaParameters have been saved: "
					+ tp.getId());
		}
		if (oabaSettings != null && !oabaSettings.isPersistent()) {
			oabaSettings = oabaSettingsController.save(oabaSettings);
			logger.info("Non-persistent OabaSettings have been saved: "
					+ oabaSettings.getId());
		}
		if (serverConfiguration != null
				&& !serverConfiguration.isPersistent()) {
			serverConfiguration = serverManager.save(serverConfiguration);
			logger.info("Non-persistent ServerConfiguration has been saved: "
					+ serverConfiguration.getId());
		}

		// Mark the job as started and start processing by the StartOabaMDB EJB
		urmJob.markAsQueued();
		urmJob.markAsStarted();

		long oabaId = oabaService.startDeduplication(externalID, tp,
				oabaSettings, serverConfiguration, urmJob);
		logger.info("Started OABA (job id: " + oabaId + ")");
		this.setTransitivityAnalysisPending(oabaId, true);

		logger.exiting(SOURCE_CLASS, METHOD, retVal);
		return retVal;
	}

	@Override
	public long startLinkageAndAnalysis(String externalID,
			TransitivityParameters tp, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration)
			throws ServerConfigurationException {

		final String METHOD = "startLinkageAndAnalysis";
		logger.entering(SOURCE_CLASS, METHOD);

		// Create and persist the job and its associated objects
		BatchJob urmJob = urmJobManager.createPersistentUrmJob(externalID);
		final long retVal = urmJob.getId();
		assert urmJob.isPersistent();
		logger.info(
				"Offline batch linkage and analysis (job id: " + retVal + ")");
		if (tp != null && !tp.isPersistent()) {
			tp = paramsController.save(tp);
			logger.info("Non-persistent OabaParameters have been saved: "
					+ tp.getId());
		}
		if (oabaSettings != null && !oabaSettings.isPersistent()) {
			oabaSettings = oabaSettingsController.save(oabaSettings);
			logger.info("Non-persistent OabaSettings have been saved: "
					+ oabaSettings.getId());
		}
		if (serverConfiguration != null
				&& !serverConfiguration.isPersistent()) {
			serverConfiguration = serverManager.save(serverConfiguration);
			logger.info("Non-persistent ServerConfiguration has been saved: "
					+ serverConfiguration.getId());
		}

		// Mark the job as started and start processing by the StartOabaMDB EJB
		urmJob.markAsQueued();
		urmJob.markAsStarted();

		long oabaId = oabaService.startLinkage(externalID, tp, oabaSettings,
				serverConfiguration, urmJob);
		logger.info("Started OABA (job id: " + oabaId + ")");
		this.setTransitivityAnalysisPending(oabaId, true);

		logger.exiting(SOURCE_CLASS, METHOD, retVal);
		return retVal;
	}

	// -- Workflow processing
	// FIXME create a WorkflowController delegate

	@Override
	public void jobUpdated(BatchProcessingNotification bpn) {
		logger.info("Received OABA update notification for job " + bpn);
		if (bpn instanceof OabaNotification) {
			handleOabaNotification(bpn);

		} else if (bpn instanceof TransitivityNotification) {
			handleTransitivityNotification(bpn);

		} else {
			logger.warning("Unexpected notification type: "
					+ bpn.getClass().getName());
		}
	}

	protected BatchJob getUrmJobFromOabaJobId(final long oabaJobId) {
		BatchJob oabaJob = oabaService.getOabaJob(oabaJobId);
		assert oabaJob != null;
		long urmJobId = oabaJob.getUrmId();
		BatchJob retVal = urmJobManager.findUrmJob(urmJobId);
		return retVal;
	}

	private BatchJob getUrmJobFromTransitivityJobId(long transitivityJobId) {
		BatchJob transitivityJob =
			transService.getTransitivityJob(transitivityJobId);
		assert transitivityJob != null;
		long urmJobId = transitivityJob.getUrmId();
		BatchJob urmJob = urmJobManager.findUrmJob(urmJobId);
		return urmJob;
	}

	protected void handleOabaNotification(
			final BatchProcessingNotification bpn) {
		final long oabaJobId = bpn.getJobId();
		logger.fine("OabaNotification for " + oabaJobId);

		final BatchJob urmJob = getUrmJobFromOabaJobId(oabaJobId);
		logger.fine("URM parent job " + urmJob);

		if (urmJob == null) {
			logger.warning("Unable to handle OABA notification in "
					+ "BatchMatchingBean.handleOabaNotification: " + bpn);
			return;
		}

		if (bpn.getJobPercentComplete() == 0.0f) {
			if (urmJob.getStatus() == BatchJobStatus.NEW) {
				urmJob.markAsQueued();
			} else {
				logger.fine("ignoring notification: " + bpn);
			}

		} else if (bpn.getJobPercentComplete() > 0.0f
				&& bpn.getJobPercentComplete() < 1.0f) {
			if (urmJob.getStatus() == BatchJobStatus.NEW) {
				urmJob.markAsQueued();
			}
			if (urmJob.getStatus() == BatchJobStatus.QUEUED) {
				urmJob.markAsStarted();
			} else {
				logger.fine("ignoring notification: " + bpn);
			}

		} else if (bpn.getJobPercentComplete() >= 1.0f) {
			if (isTransitivityAnalysisPending(oabaJobId)) {
				logger.fine("Transitivity analysis pending for " + oabaJobId);
				final boolean isPending = false;
				setTransitivityAnalysisPending(oabaJobId, isPending);
				try {
					logger.fine("OabaNotification for " + oabaJobId);
					startTransitivity(bpn);
				} catch (ServerConfigurationException e) {
					logger.severe("Transitivity analysis failed for job "
							+ oabaJobId + ": " + e);
				}

			} else {
				logger.fine("URM job completed after OABA notification for job "
						+ oabaJobId);
				urmJob.markAsCompleted();
			}

		} else {
			// Possible failure, but usually benign
			logger.fine("Incomplete results for job " + oabaJobId);
		}

	}

	private void handleTransitivityNotification(
			BatchProcessingNotification bpn) {
		final long transitivityJobId = bpn.getJobId();
		logger.fine("TransitivityNotification for " + transitivityJobId);

		final BatchJob urmJob =
			getUrmJobFromTransitivityJobId(transitivityJobId);
		logger.fine("URM parent job " + urmJob);

		if (urmJob == null) {
			logger.warning("Unable to handle transitivity notification in "
					+ "BatchMatchingBean.handleTransitivityNotification: "
					+ bpn);
			return;
		}

		if (bpn.getJobPercentComplete() == 0.0f) {
			if (urmJob.getStatus() == BatchJobStatus.NEW) {
				urmJob.markAsQueued();
			} else {
				logger.fine("ignoring notification: " + bpn);
			}

		} else if (bpn.getJobPercentComplete() > 0.0f
				&& bpn.getJobPercentComplete() < 1.0f) {
			if (urmJob.getStatus() == BatchJobStatus.NEW) {
				urmJob.markAsQueued();
			}
			if (urmJob.getStatus() == BatchJobStatus.QUEUED) {
				urmJob.markAsStarted();
			} else {
				logger.fine("ignoring notification: " + bpn);
			}

		} else if (bpn.getJobPercentComplete() >= 1.0f) {
			logger.fine(
					"URM job completed after Transitivity notification for job "
							+ transitivityJobId);
			urmJob.markAsCompleted();
			notifyCompletion(bpn);

		} else {
			// Possible failure, but usually benign
			logger.fine("Incomplete results for job " + transitivityJobId);
		}
	}

	// -- Transitivity analysis

	private void notifyCompletion(BatchProcessingNotification bpn) {
		assert bpn != null;
		assert bpn.getJobPercentComplete() >= 1.0f;

		final long transId = bpn.getJobId();
		final BatchJob transJob =
			transJobController.findTransitivityJob(transId);
		if (transJob == null) {
			String msg = "Missing Transitivity job: " + transId;
			throw new IllegalStateException(msg);
		}

		final long urmId = transJob.getUrmId();
		final BatchJob urmJob = urmJobManager.findUrmJob(urmId);
		if (urmJob == null) {
			String msg = "Missing URM job: " + urmId;
			throw new IllegalStateException(msg);
		}

		final Date now = new Date();
		final String info = null;
		sendToUpdateStatus(urmJob, DONE, now, info);
	}

	protected void sendToUpdateStatus(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		// FIXME
		// eventManager.updateStatusWithNotification(job, event,
		// timestamp, info);
	}

	protected void startTransitivity(BatchProcessingNotification bpn)
			throws ServerConfigurationException {
		assert bpn != null;
		assert bpn.getJobPercentComplete() >= 1.0f;

		final long oabaId = bpn.getJobId();
		final BatchJob oabaJob = oabaJobController.findOabaJob(oabaId);
		if (oabaJob == null) {
			String msg = "Missing OABA job: " + oabaId;
			throw new IllegalStateException(msg);
		}

		final long urmId = oabaJob.getUrmId();
		final BatchJob urmJob = urmJobManager.findUrmJob(urmId);
		if (urmJob == null) {
			String msg = "Missing URM job: " + urmId;
			throw new IllegalStateException(msg);
		}

		final String externalId = urmJob.getExternalId();

		final long paramsId = oabaJob.getParametersId();
		TransitivityParameters params =
			paramsController.findTransitivityParameters(paramsId);
		if (params == null) {
			String msg = "Missing transitivity parameters: " + paramsId;
			throw new IllegalStateException(msg);
		}

		final long settingsId = oabaJob.getSettingsId();
		OabaSettings oabaSettings =
			oabaSettingsController.findOabaSettings(settingsId);
		if (oabaSettings == null) {
			String msg = "Missing OABA settings: " + settingsId;
			throw new IllegalStateException(msg);
		}

		final long serverId = oabaJob.getServerId();
		ServerConfiguration serverConfig =
			serverManager.findServerConfiguration(serverId);
		if (serverConfig == null) {
			String msg = "Missing server configuration: " + serverId;
			throw new IllegalStateException(msg);
		}

		long transId = startTransitivity(externalId, params, oabaJob,
				oabaSettings, serverConfig, urmJob);
		this.setTransitivityAnalysisPending(transId, true);
	}

	@Override
	public long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException {
		return transService.startTransitivity(externalID, batchParams, batchJob,
				settings, serverConfiguration, urmJob);
	}

	@Override
	public long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob, RecordMatchingMode mode)
			throws ServerConfigurationException {
		return transService.startTransitivity(externalID, batchParams, batchJob,
				settings, serverConfiguration, urmJob, mode);
	}

	@Override
	public BatchJob getTransitivityJob(long jobId) {
		return transService.getTransitivityJob(jobId);
	}

	// -- Batch matching services

	@Override
	public long startDeduplication(String externalID, OabaParameters bp,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException {
		return oabaService.startDeduplication(externalID, bp, oabaSettings,
				serverConfiguration, urmJob);
	}

	@Override
	public long startLinkage(String externalID, OabaParameters batchParams,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException {
		return oabaService.startLinkage(externalID, batchParams, oabaSettings,
				serverConfiguration, urmJob);
	}

	@Override
	public BatchJob getOabaJob(long jobId) {
		return oabaService.getOabaJob(jobId);
	}

	@Override
	public Object getMatchList(long jobID)
			throws RemoteException, NamingException {
		return oabaService.getMatchList(jobID);
	}

	@Override
	public IMatchRecord2Source<?> getMatchRecordSource(long jobID)
			throws RemoteException, NamingException {
		return oabaService.getMatchRecordSource(jobID);
	}

	// -- General services

	@Override
	public boolean abortJob(long jobID) {
		BatchJob urmJob = urmJobManager.findBatchJob(jobID);
		boolean retVal;
		if (urmJob == null) {
			String msg =
				"BatchMatchingBean.checkStatus: unknown jobID: " + jobID;
			logger.warning(msg);
			retVal = false;
		} else {
			urmJobManager.abortBatchJob(urmJob);
			retVal = true;
		}
		return retVal;
	}

	@Override
	public boolean suspendJob(long jobID) {
		return abortJob(jobID);
	}

	@Override
	public String checkStatus(long jobID) {
		BatchJob urmJob = urmJobManager.findBatchJob(jobID);
		String retVal;
		if (urmJob == null) {
			String msg =
				"BatchMatchingBean.checkStatus: unknown jobID: " + jobID;
			logger.warning(msg);
			retVal = null;
		} else {
			BatchJobStatus status = urmJob.getStatus();
			retVal = status == null ? null : status.toString();
		}
		return retVal;
	}

	@Override
	public boolean resumeJob(long jobID) {
		BatchJob urmJob = urmJobManager.findBatchJob(jobID);
		boolean retVal;
		if (urmJob == null) {
			String msg =
				"BatchMatchingBean.checkStatus: unknown jobID: " + jobID;
			logger.warning(msg);
			retVal = false;
		} else {
			urmJobManager.abortBatchJob(urmJob);
			retVal = true;
		}
		return retVal;
	}

	@Override
	public boolean cleanJob(long jobID) {
		logger.warning("BatchMatchingBean.cleanJob: not yet implemented");
		return false;
	}

}
