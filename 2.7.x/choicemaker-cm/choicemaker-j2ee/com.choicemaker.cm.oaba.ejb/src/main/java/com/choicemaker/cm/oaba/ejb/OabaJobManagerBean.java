/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobJPA.PN_BATCHJOB_FIND_BY_JOBID_P1;
import static com.choicemaker.cm.batch.ejb.BatchJobJPA.QN_BATCHJOB_FIND_BY_JOBID;
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

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ProcessingEventBean;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.ejb.BatchJobFileUtils;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;

/**
 * A stateless EJB used to manage the persistence of OabaJobEntity instances.
 * 
 * @author rphall
 */
@Stateless
@TransactionAttribute(REQUIRED)
public class OabaJobManagerBean implements OabaJobManager {

	private static final Logger logger =
		Logger.getLogger(OabaJobManagerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private ServerConfigurationController serverManager;

	@EJB
	private EventPersistenceManager eventManager;

	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:/choicemaker/urm/jms/statusTopic")
	private Topic oabaStatusTopic;

	protected OabaJobEntity getBean(BatchJob batchJob) {
		OabaJobEntity retVal = null;
		if (batchJob != null) {
			final long jobId = batchJob.getId();
			if (batchJob instanceof OabaJobEntity) {
				retVal = (OabaJobEntity) batchJob;
			} else {
				if (batchJob.isPersistent()) {
					retVal = em.find(OabaJobEntity.class, jobId);
					if (retVal == null) {
						String msg =
							"Unable to find persistent OABA job: " + jobId;
						logger.warning(msg);
					}
				}
			}
			if (retVal == null) {
				retVal = new OabaJobEntity(batchJob);
			}
		}
		return retVal;
	}

	@Override
	public BatchJob createPersistentOabaJob(String externalID,
			OabaParameters params, OabaSettings settings,
			ServerConfiguration sc) throws ServerConfigurationException {
		return createPersistentOabaJob(externalID, params, settings, sc, null);
	}

	@Override
	public BatchJob createPersistentOabaJob(String externalID,
			OabaParameters params, OabaSettings settings,
			ServerConfiguration sc, BatchJob urmJob)
			throws ServerConfigurationException {
		if (params == null || settings == null || sc == null) {
			throw new IllegalArgumentException("null argument");
		}
		if (params != null && !params.isPersistent()) {
			params = paramsController.save(params);
			logger.info("Non-persistent OabaParameters have been saved: "
					+ params.getId());
		}
		if (settings != null && !settings.isPersistent()) {
			settings = oabaSettingsController.save(settings);
			logger.info("Non-persistent OabaSettings have been saved: "
					+ settings.getId());
		}
		if (sc != null && !sc.isPersistent()) {
			sc = serverManager.save(sc);
			logger.info("Non-persistent ServerConfiguration has been saved: "
					+ sc.getId());
		}
		if (urmJob != null && !urmJob.isPersistent()) {
			logger.warning("Non-persistent URM job");
		}

		// Save the parameters

		OabaJobEntity retVal =
			new OabaJobEntity(params, settings, sc, urmJob, externalID);
		em.persist(retVal);
		assert retVal.isPersistent();

		// Create a new entry in the processing log and check it
		OabaEventManager.updateStatusWithNotification(em, jmsContext,
				oabaStatusTopic, retVal, ProcessingEventBean.INIT, new Date(),
				null);
		BatchProcessingEvent ope =
			OabaEventManager.getCurrentBatchProcessingEvent(em, retVal);
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
		logger.info("Oaba job: " + retVal.toString());
		logger.info("Oaba parameters: " + params.toString());
		logger.info("Oaba settings: " + settings.toString());
		logger.info("Server configuration: " + sc.toString());
		logger.info("Current processing event: " + currentProcessingEvent);

		return retVal;
	}

	@Override
	public BatchJob save(BatchJob batchJob) {
		return save(getBean(batchJob));
	}

	public OabaJobEntity save(OabaJobEntity job) {
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
	public BatchJob findOabaJob(long id) {
		OabaJobEntity batchJob = em.find(OabaJobEntity.class, id);
		return batchJob;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchJob> findAll() {
		Query query = em.createNamedQuery(OabaJobJPA.QN_OABAJOB_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<BatchJob> retVal = query.getResultList();
		if (retVal == null) {
			retVal = new ArrayList<BatchJob>();
		}
		return retVal;
	}

	@Override
	public void delete(BatchJob batchJob) {
		if (batchJob.isPersistent()) {
			OabaJobEntity bean = em.find(OabaJobEntity.class, batchJob.getId());
			delete(bean);
		}
	}

	void delete(OabaJobEntity bean) {
		if (bean != null) {
			bean = em.merge(bean);
			em.remove(bean);
			em.flush();
		}
	}

	@Override
	public void detach(BatchJob oabaJob) {
		em.detach(oabaJob);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public BatchJob findBatchJob(long id) {
		Query query = em.createNamedQuery(QN_BATCHJOB_FIND_BY_JOBID);
		query.setParameter(PN_BATCHJOB_FIND_BY_JOBID_P1, id);
		@SuppressWarnings("unchecked")
		List<BatchJob> entries = query.getResultList();
		if (entries != null && entries.size() > 1) {
			String msg = "Violates primary key constraint: " + entries.size();
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}
		BatchJob retVal = null;
		if (entries != null && !entries.isEmpty()) {
			assert entries.size() == 1;
			retVal = entries.get(0);
		}
		return retVal;
	}

}
