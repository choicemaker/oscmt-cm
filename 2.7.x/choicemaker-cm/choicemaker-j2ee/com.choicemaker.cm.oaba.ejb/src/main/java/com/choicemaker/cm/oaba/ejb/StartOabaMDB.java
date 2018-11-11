/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_BLOCKING_FIELD_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_ID_TYPE;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_MATCHING_MODE;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchJobControl;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.impl.RecValSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ValidatorBase;
import com.choicemaker.cm.oaba.services.RecValService3;

/**
 * This message bean is the first step of the OABA. It creates rec_id, val_id
 * files using internal id translation.
 *
 * @author pcheung
 * @author rphall (EJB 3)
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/startQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode",
				propertyValue = "Dups-ok-acknowledge") })
// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class StartOabaMDB extends AbstractOabaMDB {

	private static final long serialVersionUID = 271L;

	private static final Logger log =
		Logger.getLogger(StartOabaMDB.class.getName());

	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + StartOabaMDB.class.getName());

	// @Inject
	//// @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
	// private ConnectionFactory jmxConnFactory;

	@Resource
	MessageDrivenContext jmsCtx;

	@Resource
	private UserTransaction userTx;

	@Resource(lookup = "java:/choicemaker/urm/jms/blockQueue")
	private Queue blockQueue;

	@Override
	public void onMessage(Message inMessage) {
		final String SOURCE = StartOabaMDB.class.getSimpleName();
		final String METHOD = "onMessage(Message)";
		jmsTrace.info("Entering onMessage for " + this.getClass().getName());
		ObjectMessage msg = null;
		OabaJobMessage data = null;
		BatchJob batchJob = null;

		getLogger().info("StartOabaMDB in onMessage");

		UserTransaction jmsTx = null;
		try {

			// Commit the JMS transaction to acknowledge receipt of the message
			jmsTx = jmsCtx.getUserTransaction();
			int jmsTxStatus = jmsTx == null ? Status.STATUS_NO_TRANSACTION
					: jmsTx.getStatus();
			if (jmsTxStatus != Status.STATUS_NO_TRANSACTION) {
				log.fine(String.format("%s.%s: committing JMS transaction",
						SOURCE, METHOD));
				jmsTx.commit();
				log.finer(String.format("%s.%s: committed JMS transaction",
						SOURCE, METHOD));
				jmsTx = null;
			} else {
				log.fine(
						String.format("%s.%s: no JMS transaction",
								SOURCE, METHOD));
			}

			if (inMessage instanceof ObjectMessage) {
				msg = (ObjectMessage) inMessage;
				data = (OabaJobMessage) msg.getObject();

				// BatchJob tends to lock up, so keep tx short
				userTx.begin();
				final long jobId = data.jobID;
				batchJob = getJobController().findBatchJob(jobId);
				userTx.commit();
				// FIXME END experiment

				userTx.begin();
				OabaParameters oabaParams = getParametersController()
						.findOabaParametersByBatchJobId(jobId);
				OabaSettings oabaSettings =
					getSettingsController().findOabaSettingsByJobId(jobId);
				ProcessingEventLog processingEntry =
					getEventManager().getProcessingLog(batchJob);
				userTx.commit();

				if (batchJob == null || oabaParams == null
						|| oabaSettings == null) {
					String s =
						"Unable to find a job, parameters or settings for "
								+ jobId;
					getLogger().severe(s);
					throw new IllegalArgumentException(s);
				}
				final String modelConfigId =
					oabaParams.getModelConfigurationName();
				ImmutableProbabilityModel model =
					PMManager.getModelInstance(modelConfigId);
				if (model == null) {
					String s =
						"No modelId corresponding to '" + modelConfigId + "'";
					getLogger().severe(s);
					throw new IllegalArgumentException(s);
				}

				// update status to mark as start
				userTx.begin();
				batchJob.markAsStarted();
				getJobController().save(batchJob);
				userTx.commit();

				getLogger().info("Job id: " + jobId);
				getLogger().info("Model configuration: "
						+ oabaParams.getModelConfigurationName());
				getLogger().info(
						"Differ threshold: " + oabaParams.getLowThreshold());
				getLogger().info(
						"Match threshold: " + oabaParams.getHighThreshold());
				getLogger().info("Staging record source id: "
						+ oabaParams.getQueryRsId());
				getLogger().info("Staging record source type: "
						+ oabaParams.getQueryRsType());
				getLogger().info("Master record source id: "
						+ oabaParams.getReferenceRsId());
				getLogger().info("Master record source type: "
						+ oabaParams.getReferenceRsType());
				getLogger().info(
						"Linkage type: " + oabaParams.getOabaLinkageType());

				// check to see if there are a lot of records in stage.
				// if not use single record matching instead of batch.
				getLogger().info(
						"Checking whether to use single- or batched-record blocking...");
				getLogger().info("OabaSettings maxSingle: "
						+ oabaSettings.getMaxSingle());

				ISerializableRecordSource staging = null;
				ISerializableRecordSource master = null;
				userTx.begin();
				staging = getRecordSourceController().getStageRs(oabaParams);
				master = getRecordSourceController().getMasterRs(oabaParams);
				userTx.commit();
				assert staging != null;

				RecordMatchingMode mode;
				final int maxSingle = oabaSettings.getMaxSingle();
//				userTx.begin();
				if (!isMoreThanThreshold(staging, model, maxSingle)) {
					getLogger().info("Using single record matching");
					mode = RecordMatchingMode.SRM;
					configureRecordMatchingMode(batchJob, mode);

				} else {
					getLogger().info("Using batch record matching");
					mode = RecordMatchingMode.BRM;
					configureRecordMatchingMode(batchJob, mode);
				}
//				userTx.commit();

				userTx.begin();
				final RecordIdController ric = getRecordIdController();
				MutableRecordIdTranslator<?> translator =
					ric.createMutableRecordIdTranslator(batchJob);
				userTx.commit();

				// create rec_id, val_id files
				userTx.begin();
				String blockingConfiguration =
					oabaParams.getBlockingConfiguration();
				String queryConfiguration = this.getParametersController()
						.getQueryDatabaseConfiguration(oabaParams);
				String referenceConfiguration = this.getParametersController()
						.getReferenceDatabaseConfiguration(oabaParams);
				RecValSinkSourceFactory recvalFactory =
					OabaFileUtils.getRecValFactory(batchJob);
				final BatchJobControl control =
					new BatchJobControl(this.getJobController(), batchJob);
				RecValService3 rvService = new RecValService3(staging, master,
						model, blockingConfiguration, queryConfiguration,
						referenceConfiguration, recvalFactory, ric, translator,
						processingEntry, control, mode);
				userTx.commit();
				userTx.begin();
				rvService.runService();
				userTx.commit();
				getLogger().info("Done creating rec_id, val_id files: "
						+ rvService.getTimeElapsed());

				userTx.begin();
				@SuppressWarnings("rawtypes")
				ImmutableRecordIdTranslator immutableTranslator =
					ric.toImmutableTranslator(translator);
				final RECORD_ID_TYPE recordIdType =
					immutableTranslator.getRecordIdType();
				userTx.commit();

//				userTx.begin();
				getPropertyController().setJobProperty(batchJob,
						PN_RECORD_ID_TYPE, recordIdType.name());

				final int numBlockFields = rvService.getNumBlockingFields();
				getPropertyController().setJobProperty(batchJob,
						PN_BLOCKING_FIELD_COUNT,
						String.valueOf(numBlockFields));

				// create the validator after rvService
				// Validator validator = new Validator (true, translator);
				ValidatorBase validator =
					new ValidatorBase(true, immutableTranslator);
				// FIXME move this parameter to a persistent operational
				// object
				data.validator = validator;

				updateOabaProcessingStatus(batchJob, OabaEventBean.DONE_REC_VAL,
						new Date(), null);
//				userTx.commit();
				userTx.begin();
				sendToBlocking(data);
				userTx.commit();

			} else {
				getLogger().warning(
						"wrong type: " + inMessage.getClass().getName());
			}

		} catch (Exception e) {
			String msg0 = throwableToString(e);
			log.severe(msg0);
			try {
				int status = jmsTx == null ? Status.STATUS_NO_TRANSACTION
						: jmsTx.getStatus();
				if (status != Status.STATUS_NO_TRANSACTION) {
					jmsTx.setRollbackOnly();
					jmsTx = null;
				}
			} catch (Exception e1) {
				String msg1 = throwableToString(e);
				log.severe(msg1);
			}
			try {
				int status = userTx == null ? Status.STATUS_NO_TRANSACTION
						: userTx.getStatus();
				if (status != Status.STATUS_NO_TRANSACTION) {
					if (batchJob != null) {
						batchJob.markAsFailed();
						getJobController().save(batchJob);
					}
					userTx.setRollbackOnly();
				}
			} catch (Exception e1) {
				String msg1 = throwableToString(e);
				log.severe(msg1);
			}
		}
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
	}

	protected void configureRecordMatchingMode(BatchJob batchJob,
			RecordMatchingMode mode) {
		getPropertyController().setJobProperty(batchJob,
				PN_RECORD_MATCHING_MODE, mode.name());
	}

	@Override
	protected String throwableToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(throwable.toString());
		throwable.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	/**
	 * This method checks to see if the number of records in the
	 * RECORD_SOURCE_ROLE is greater than the threshold.
	 *
	 * @param rs
	 *            - RECORD_SOURCE_ROLE
	 * @param accessProvider
	 *            - Probability Model of this RECORD_SOURCE_ROLE
	 * @param threshold
	 *            - The number of records threshold
	 * @return boolean - true if the RECORD_SOURCE_ROLE contains more than the
	 *         threshold
	 * @throws OABABlockingException
	 */
	private boolean isMoreThanThreshold(RecordSource rs,
			ImmutableProbabilityModel model, int threshold)
			throws BlockingException {

		// Preconditions need to be checked on this method, even though it is
		// private, because the arguments haven't been validated before it
		// is invoked. The arguments are derived from a persistent object,
		// BatchParameters, which may have been saved with invalid fields.
		if (model == null) {
			throw new IllegalArgumentException("null modelId");
		}
		if (rs == null) {
			throw new IllegalArgumentException("null record source");
		}

		boolean retVal = false;
		getLogger()
				.info("Checking if the number of records is more than the maxSingle threshold: "
						+ threshold);
		if (threshold <= 0) {
			getLogger().info("The threshold shortcuts further checking");
			retVal = true;
		} else {
			getLogger().info("Record source: " + rs);
			getLogger().info("Model: " + model);
			try {
				rs.setModel(model);
				rs.open();
				int count = 1;
				while (count <= threshold && rs.hasNext()) {
					rs.getNext();
					count++;
				}
				if (rs.hasNext()) {
					++count;
					getLogger().info("Number of records: " + count + "+");
					retVal = true;
				} else {
					getLogger().info("Number of records: " + count);
				}
				rs.close();
			} catch (IOException ex) {
				throw new BlockingException(ex.toString(), ex);
			}
		}
		String msg =
			"The number of records " + (retVal ? "exceeds" : "does not exceed")
					+ " the maxSingle threshold: " + threshold;
		getLogger().info(msg);

		return retVal;
	}

	private void sendToBlocking(OabaJobMessage data) {
		MessageBeanUtils.sendStartData(data, getJmsContext(), blockQueue,
				getLogger());
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected Logger getJmsTrace() {
		return jmsTrace;
	}

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {
		// Does nothing in this class. Instead, onMessage is overridden
		// and this callback is bypassed.
	}

	@Override
	protected OabaEventBean getCompletionEvent() {
		return OabaEventBean.DONE_REC_VAL;
	}

	@Override
	protected void notifyProcessingCompleted(OabaJobMessage data) {
		// Does nothing in this class. Instead, notifications are
		// sent via sendToSingleRecordMatching(..) and sendToBlocking(..)
	}

}
