/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ProcessingEventBean;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.ejb.BatchJobFileUtils;
import com.choicemaker.cm.batch.ejb.AbstractBatchJobManagerBean;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;

/**
 * A stateless EJB used to manage the persistence of TransitivityJobEntity
 * instances.
 * 
 * @author rphall
 */
@Stateless
@TransactionAttribute(REQUIRED)
public class TransitivityJobManagerBean extends AbstractBatchJobManagerBean
		implements TransitivityJobManager {

	private static final Logger logger =
		Logger.getLogger(TransitivityJobManagerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityParametersController paramsController;

	@EJB
	private OabaSettingsController settingsController;

	@EJB
	private ServerConfigurationController serverManager;

	@EJB
	private EventPersistenceManager eventManager;

	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:/choicemaker/urm/jms/transStatusTopic")
	private Topic transStatusTopic;

	protected TransitivityJobEntity getBean(BatchJob oabaJob) {
		TransitivityJobEntity retVal = null;
		if (oabaJob != null) {
			final long jobId = oabaJob.getId();
			if (oabaJob instanceof TransitivityJobEntity) {
				retVal = (TransitivityJobEntity) oabaJob;
			} else {
				if (oabaJob.isPersistent()) {
					retVal = em.find(TransitivityJobEntity.class, jobId);
					if (retVal == null) {
						String msg =
							"Unable to find persistent OABA job: " + jobId;
						logger.warning(msg);
					}
				}
			}
			if (retVal == null) {
				retVal = new TransitivityJobEntity(oabaJob);
			}
		}
		return retVal;
	}

	@Override
	public BatchJob createPersistentTransitivityJob(String externalID,
			TransitivityParameters params, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration sc)
			throws ServerConfigurationException {
		return createPersistentTransitivityJob(externalID, params, batchJob,
				settings, sc, null);
	}

	@Override
	public BatchJob createPersistentTransitivityJob(String externalID,
			TransitivityParameters params, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration sc, BatchJob urmJob)
			throws ServerConfigurationException {

		if (params == null || sc == null || batchJob == null) {
			throw new IllegalArgumentException("null argument");
		}

		// Check the OABA job persistence and status
		if (!batchJob.isPersistent()) {
			throw new IllegalArgumentException("non-persistent OABA job");
		}
		BatchJobStatus oabaStatus = batchJob.getStatus();
		assert oabaStatus != null;
		String msg0 = "Precedessor OABA (job " + batchJob.getId() + ") status: "
				+ oabaStatus;
		logger.info(msg0);
		switch (oabaStatus) {
		case COMPLETED:
			logger.finest("Typical case: OABA status: " + oabaStatus);
			break;
		case ABORT_REQUESTED:
		case ABORTED:
		case FAILED:
			logger.warning("Abort state: OABA status: " + oabaStatus);
			break;
		case NEW:
		case QUEUED:
		case PROCESSING:
			String msg1 = "Incomplete state: OABA status: " + oabaStatus;
			logger.severe(msg1);
			throw new IllegalArgumentException(msg1);
		default:
			String msg2 = "Unexpectd state: OABA status: " + oabaStatus;
			logger.severe(msg2);
			throw new IllegalStateException(msg2);
		}

		// Save the parameters
		params = paramsController.save(params);
		settings = settingsController.save(settings);
		sc = serverManager.save(sc);

		TransitivityJobEntity retVal = new TransitivityJobEntity(params,
				settings, sc, batchJob, urmJob, externalID);
		em.persist(retVal);
		assert retVal.isPersistent();

		// Create a new processing entry
		// ProcessingEventLog processing =
		// eventManager.getProcessingLog(retVal);
		// Create a new entry in the processing log and check it
		TransitivityEventManager.updateStatusWithNotification(em, jmsContext,
				transStatusTopic, retVal, ProcessingEventBean.INIT, new Date(),
				null);
		BatchProcessingEvent ope =
			TransitivityEventManager.getCurrentBatchProcessingEvent(em, retVal);
		ProcessingEvent currentProcessingEvent = ope.getProcessingEvent();
		assert currentProcessingEvent.getEventId() == ProcessingEventBean.INIT
				.getEventId();
		assert currentProcessingEvent
				.getFractionComplete() == ProcessingEventBean.INIT
						.getFractionComplete();

		// Create the working directory
		File workingDir = BatchJobFileUtils.createWorkingDirectory(sc, retVal);
		retVal.setWorkingDirectory(workingDir);

		// Log the job info
		logger.info("Transitivity job: " + retVal.toString());
		logger.info("Transitivity OABA job: " + batchJob.toString());
		logger.info("Transitivity parameters: " + params.toString());
		logger.info("Server configuration: " + sc.toString());
		logger.info("Current processing event: " + currentProcessingEvent);

		return retVal;
	}

	@Override
	public BatchJob save(BatchJob batchJob) {
		return save(getBean(batchJob));
	}

	public TransitivityJobEntity save(TransitivityJobEntity job) {
		logger.fine("Saving " + job);
		if (job == null) {
			throw new IllegalArgumentException("null job");
		}
		if (!job.isPersistent()) {
			em.persist(job);
			logger.fine("Saved " + job);
		} else {
			job = em.merge(job);
			logger.fine("Merged " + job);
		}
		return job;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public BatchJob findTransitivityJob(long id) {
		TransitivityJobEntity job = em.find(TransitivityJobEntity.class, id);
		return job;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchJob> findAllTransitivityJobs() {
		Query query =
			em.createNamedQuery(TransitivityJobJPA.QN_TRANSITIVITY_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<BatchJob> retVal = query.getResultList();
		if (retVal == null) {
			retVal = new ArrayList<BatchJob>();
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchJob> findAllByOabaJobId(long batchJobId) {
		Query query = em.createNamedQuery(
				TransitivityJobJPA.QN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID);
		query.setParameter(
				TransitivityJobJPA.PN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID_BPARENTID,
				batchJobId);
		@SuppressWarnings("unchecked")
		List<BatchJob> retVal = query.getResultList();
		if (retVal == null) {
			retVal = new ArrayList<BatchJob>();
		}
		return retVal;
	}

	@Override
	public void delete(BatchJob transitivityJob) {
		if (transitivityJob.isPersistent()) {
			TransitivityJobEntity bean =
				em.find(TransitivityJobEntity.class, transitivityJob.getId());
			delete(bean);
		}
	}

	void delete(TransitivityJobEntity bean) {
		if (bean != null) {
			bean = em.merge(bean);
			em.remove(bean);
			em.flush();
		}
	}

	@Override
	public void detach(BatchJob job) {
		em.detach(job);
	}

}
