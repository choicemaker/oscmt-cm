/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
//import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.BatchJobUtils;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationEntity;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityService;
import com.choicemaker.cm.transitivity.core.TransitivityEventBean;

/**
 * @author pcheung
 */
@Stateless
public class TransitivityServiceBean implements TransitivityService {

	private static final Logger log =
		Logger.getLogger(TransitivityServiceBean.class.getName());

	private static final String SOURCE_CLASS =
		TransitivityServiceBean.class.getSimpleName();

	protected static void logStartParameters(String externalID,
			TransitivityParameters tp, BatchJob batchJob,
			OabaParameters oabaParams, ServerConfiguration sc,
			RecordMatchingMode mode) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		pw.println("External id: " + externalID);
		pw.println("Record-matching mode: " + mode);
		pw.println(TransitivityParametersEntity.dump(tp, batchJob, oabaParams));
		pw.println(ServerConfigurationEntity.dump(sc));
		String msg = sw.toString();
		log.info(msg);
	}

	protected static void validateStartParameters(String externalID,
			TransitivityParameters tp, BatchJob batchJob,
			ServerConfiguration sc, RecordMatchingMode mode) {

		// Create an empty list of invalid parameters
		List<String> validityErrors = new LinkedList<>();
		assert validityErrors.isEmpty();

		if (tp == null) {
			validityErrors.add("null batch parameters");
		}
		if (batchJob == null) {
			validityErrors.add("null OABA job");
		}
		if (sc == null) {
			validityErrors.add("null server configuration");
		}
		if (mode == null) {
			validityErrors.add("null record-matching mode");
		}
		if (!validityErrors.isEmpty()) {
			String msg = "Invalid parameters to OabaService.startOABA: "
					+ validityErrors.toString();
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	@EJB
	OabaParametersController oabaParamsController;

	@EJB
	TransitivityJobManager jobManager;

	@EJB
	private OperationalPropertyController propController;

	@EJB
	private EventPersistenceManager eventManager;

	@Resource(name = "jms/transitivityQueue",
			lookup = "java:/choicemaker/urm/jms/transitivityQueue")
	private Queue queue;

	@Inject
	private JMSContext context;

	@Override
	public long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException {
		RecordMatchingMode mode =
			BatchJobUtils.getRecordMatchingMode(propController, batchJob);
		return startTransitivity(externalID, batchParams, batchJob, settings,
				serverConfiguration, urmJob, mode);
	}

	@Override
	public long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob, RecordMatchingMode mode)
			throws ServerConfigurationException {

		final String METHOD = "startTransitivity";
		log.entering(SOURCE_CLASS, METHOD);

		validateStartParameters(externalID, batchParams, batchJob,
				serverConfiguration, mode);

		OabaParameters oabaParams = oabaParamsController
				.findOabaParametersByBatchJobId(batchJob.getId());
		logStartParameters(externalID, batchParams, batchJob, oabaParams,
				serverConfiguration, mode);

		// Create and persist a transitivity job and its associated objects
		BatchJob transJob =
			jobManager.createPersistentTransitivityJob(externalID, batchParams,
					batchJob, settings, serverConfiguration, urmJob);
		assert transJob.isPersistent();
		final long retVal = transJob.getId();
		BatchJobUtils.setRecordMatchingMode(propController, transJob, mode);

		// Abort or continue depending on the status of the parent URM job
		final BatchJobStatus urmStatus = urmJob.getStatus();
		switch (urmStatus) {
		case ABORT_REQUESTED:
		case ABORTED:
		case COMPLETED:
		case FAILED: {
			// Mark the job as aborted and notify listeners
			log.info("Aborted transitivity analysis (job id: " + retVal + ")");
			transJob.markAsAborted();
			jobManager.save(transJob);
			eventManager.updateStatusWithNotification(transJob,
					TransitivityEventBean.DONE, new Date(), null);
			break;
		}
		case NEW:
		case QUEUED:
		case PROCESSING:
		default: {
			// Mark the job as queued, start processing by the
			// StartTransitivityMDB,
			// and notify listeners that the job is queued.
			log.info("Started transitivity analysis (job id: " + retVal + ")");
			transJob.markAsQueued();
			jobManager.save(transJob);
			sendToTransitivity(retVal);
			eventManager.updateStatusWithNotification(transJob,
					TransitivityEventBean.QUEUED, new Date(), null);
			break;
		}
		}

		log.exiting(SOURCE_CLASS, METHOD, retVal);
		return retVal;
	}

	@Override
	public BatchJob getTransitivityJob(long jobId) {
		BatchJob transJob = jobManager.findTransitivityJob(jobId);
		return transJob;
	}

	/**
	 * This method puts the request on the Transitivity Engine's message queue.
	 *
	 * @param d
	 * @throws NamingException
	 * @throws JMSException
	 */
	private void sendToTransitivity(long jobID) {
		OabaJobMessage data = new OabaJobMessage(jobID);
		MessageBeanUtils.sendStartData(data, context, queue, log);
	}

}
