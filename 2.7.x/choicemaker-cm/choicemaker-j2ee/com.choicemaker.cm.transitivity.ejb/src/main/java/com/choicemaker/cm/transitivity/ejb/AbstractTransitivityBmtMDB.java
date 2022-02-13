/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEventBean;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.SqlRecordSourceController;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.BatchJobUtils;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.transitivity.api.TransitivityConfigurationController;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cm.transitivity.api.TransitivitySettingsController;
import com.choicemaker.cm.transitivity.ejb.util.LoggingUtils;

/**
 * Common functionality of message driven beans that perform Transitivity
 * processing steps. Bean-managed transactions.
 *
 * @author rphall
 *
 */
public abstract class AbstractTransitivityBmtMDB
		implements MessageListener, Serializable {

	private static final long serialVersionUID = 271L;

	private static final String SOURCE =
		AbstractTransitivityBmtMDB.class.getSimpleName();

	// -- Instance data

	@EJB
	private OabaJobManager oabaJobController;

	@EJB
	private TransitivityJobManager transJobController;

	@EJB
	private TransitivitySettingsController settingsController;

	@EJB
	private TransitivityParametersController paramsController;

	@EJB(beanName = "TransitivityEventManager")
	private EventPersistenceManager eventManager;

	@EJB
	private TransitivityConfigurationController serverController;

	@EJB
	private RecordSourceController rsController;

	@EJB
	private SqlRecordSourceController sqlRSController;

	@EJB
	private RecordIdController ridController;

	@EJB
	private OperationalPropertyController propController;

	@Inject
	private JMSContext jmsContext;

	protected JMSContext getJmsContext() {
		return jmsContext;
	}

	// -- Accessors

	protected final BatchJobManager getOabaJobController() {
		return oabaJobController;
	}

	protected final TransitivityJobManager getTransitivityJobController() {
		return transJobController;
	}

	protected final TransitivitySettingsController getSettingsController() {
		return settingsController;
	}

	protected final TransitivityParametersController getParametersController() {
		return paramsController;
	}

	protected final EventPersistenceManager getEventManager() {
		return eventManager;
	}

	protected final TransitivityConfigurationController getServerController() {
		return serverController;
	}

	protected final RecordSourceController getRecordSourceController() {
		return rsController;
	}

	protected final SqlRecordSourceController getSqlRecordSourceController() {
		return sqlRSController;
	}

	protected final RecordIdController getRecordIdController() {
		return ridController;
	}

	protected final OperationalPropertyController getPropertyController() {
		return propController;
	}

	// -- Template methods

	@Override
	public void onMessage(Message inMessage) {
		final String METHOD = "onMessage";
		getJmsTrace()
				.info("Entering onMessage for " + this.getClass().getName());
		ObjectMessage msg = null;
		OabaJobMessage oabaMsg = null;
		BatchJob batchJob = null;

		try {
			// Commit the JMS transaction to acknowledge receipt of the message
			int jmsTxStatus = getUserTx() == null ? Status.STATUS_NO_TRANSACTION
					: getUserTx().getStatus();
			if (jmsTxStatus != Status.STATUS_NO_TRANSACTION) {
				getLogger().fine(String.format(
						"%s.%s: committing JMS transaction", SOURCE, METHOD));
				getUserTx().commit();
				getLogger().finer(String.format(
						"%s.%s: committed JMS transaction", SOURCE, METHOD));
			} else {
				getLogger().fine(String.format("%s.%s: no JMS transaction",
						SOURCE, METHOD));
			}

			if (inMessage instanceof ObjectMessage) {
				msg = (ObjectMessage) inMessage;
				oabaMsg = (OabaJobMessage) msg.getObject();

				final long jobId = oabaMsg.jobID;
				batchJob =
					getTransitivityJobController().findTransitivityJob(jobId);
				TransitivityParameters transParams = getParametersController()
						.findTransitivityParametersByBatchJobId(jobId);
				OabaSettings settings = getSettingsController()
						.findSettingsByTransitivityJobId(jobId);
				ProcessingEventLog processingLog =
					getEventManager().getProcessingLog(batchJob);
				ServerConfiguration serverConfig = getServerController()
						.findConfigurationByTransitivityJobId(jobId);

				if (batchJob == null || transParams == null || settings == null
						|| serverConfig == null) {
					String s0 =
						"Unable to find a job, parameters, settings or server configuration for "
								+ jobId;
					String s = LoggingUtils.buildDiagnostic(s0, batchJob,
							transParams, settings, serverConfig);
					getLogger().severe(s);
					throw new IllegalStateException(s);
				}

				final String modelConfigId =
					transParams.getModelConfigurationName();
				ImmutableProbabilityModel model =
					PMManager.getModelInstance(modelConfigId);

				if (model == null) {
					String s =
						"No modelId corresponding to '" + modelConfigId + "'";
					getLogger().severe(s);
					throw new IllegalArgumentException(s);
				}

				if (BatchJobStatus.ABORT_REQUESTED
						.equals(batchJob.getStatus())) {
					abortProcessing(batchJob, processingLog);

				} else {
					processOabaMessage(oabaMsg, batchJob, transParams, settings,
							processingLog, serverConfig, model);
					updateTransivitityProcessingStatus(batchJob,
							getCompletionEvent(), new Date(), null);
					notifyProcessingCompleted(oabaMsg);
				}

			} else {
				getLogger().warning(
						"wrong type: " + inMessage.getClass().getName());
			}

		} catch (Exception e) {
			String msg0 = throwableToString(e);
			getLogger().severe(msg0);
			// FIXME weird rollback
			try {
				int status = getUserTx() == null ? Status.STATUS_NO_TRANSACTION
						: getUserTx().getStatus();
				if (status != Status.STATUS_NO_TRANSACTION) {
					getUserTx().rollback();
				}
			} catch (Exception e1) {
				String msg1 = throwableToString(e);
				getLogger().severe(msg1);
			}
			try {
				int status = getUserTx() == null ? Status.STATUS_NO_TRANSACTION
						: getUserTx().getStatus();
				if (status != Status.STATUS_NO_TRANSACTION) {
					if (batchJob != null) {
						batchJob.markAsFailed();
						getTransitivityJobController().save(batchJob);
					}
					getUserTx().rollback();
				}
			} catch (Exception e1) {
				String msg1 = throwableToString(e);
				getLogger().severe(msg1);
			}
		}
		getJmsTrace()
				.info("Exiting onMessage for " + this.getClass().getName());
	}

	protected String throwableToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(throwable.toString());
		throwable.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	protected void abortProcessing(BatchJob batchJob,
			ProcessingEventLog processingLog) {
		MessageBeanUtils.stopJob(batchJob, getTransitivityJobController(),
				getPropertyController(), processingLog);
	}

	protected void updateTransivitityProcessingStatus(BatchJob job,
			ProcessingEventBean event, Date timestamp, String info) {
		getEventManager().updateStatusWithNotification(job, event, timestamp,
				info);
	}

	protected RecordMatchingMode getRecordMatchingMode(final BatchJob job) {
		RecordMatchingMode retVal =
			BatchJobUtils.getRecordMatchingMode(getPropertyController(), job);
		if (retVal == null) {
			String msg = "Null record-matching mode for job " + job.getId();
			throw new IllegalStateException(msg);
		}
		assert retVal != null;
		return retVal;
	}

	// -- Abstract call-back methods

	protected abstract Logger getLogger();

	protected abstract Logger getJmsTrace();

	protected abstract MessageDrivenContext getMdcCtx();

	protected abstract UserTransaction getUserTx();

	protected abstract void processOabaMessage(OabaJobMessage data,
			BatchJob batchJob, TransitivityParameters params,
			OabaSettings oabaSettings, ProcessingEventLog processingLog,
			ServerConfiguration serverConfig, ImmutableProbabilityModel model)
			throws BlockingException;

	protected abstract ProcessingEventBean getCompletionEvent();

	protected abstract void notifyProcessingCompleted(OabaJobMessage data);

}
