package com.choicemaker.cms.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobJPA.PN_BATCHJOB_FIND_BY_JOBID_P1;
import static com.choicemaker.cm.batch.ejb.BatchJobJPA.QN_BATCHJOB_FIND_BY_JOBID;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.ejb.OabaJobJPA;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cms.api.UrmJobController;

/**
 * A stateless EJB used to manage the persistence of UrmJobEntity instances.
 * 
 * @author rphall
 */
@Stateless
public class UrmJobControllerBean implements UrmJobController {

	private static final Logger logger = Logger
			.getLogger(UrmJobControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityParametersController paramsController;

	@EJB
	private OabaSettingsController settingsController;

	@EJB
	private ServerConfigurationController serverManager;

	// @EJB
	// private ProcessingController processingController;

	// FIXME
	// @Inject
	// private JMSContext jmsContext;

	@Resource(lookup = "java:/choicemaker/urm/jms/transStatusTopic")
	private Topic transStatusTopic;

	protected UrmJobEntity getBean(BatchJob oabaJob) {
		UrmJobEntity retVal = null;
		if (oabaJob != null) {
			final long jobId = oabaJob.getId();
			if (oabaJob instanceof UrmJobEntity) {
				retVal = (UrmJobEntity) oabaJob;
			} else {
				if (oabaJob.isPersistent()) {
					retVal = em.find(UrmJobEntity.class, jobId);
					if (retVal == null) {
						String msg =
							"Unable to find persistent OABA job: " + jobId;
						logger.warning(msg);
					}
				}
			}
			if (retVal == null) {
				retVal = new UrmJobEntity(oabaJob);
			}
		}
		return retVal;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BatchJob createPersistentUrmJob(String externalID) {

		UrmJobEntity retVal = new UrmJobEntity(externalID);
		em.persist(retVal);
		assert retVal.isPersistent();

		// FIXME
		// // Create a new processing entry
		// // ProcessingEventLog processing =
		// // processingController.getProcessingLog(retVal);
		// // Create a new entry in the processing log and check it
		// UrmProcessingControllerBean.updateStatusWithNotification(em,
		// jmsContext, transStatusTopic, retVal, BatchProcessingEvent.INIT,
		// new Date(), null);
		// BatchProcessingEvent ope =
		// UrmProcessingControllerBean.getCurrentBatchProcessingEvent(em,
		// retVal);
		// ProcessingEvent currentProcessingEvent = ope.getProcessingEvent();
		// assert currentProcessingEvent.getEventId() ==
		// BatchProcessingEvent.INIT
		// .getEventId();
		// assert currentProcessingEvent.getPercentComplete() ==
		// BatchProcessingEvent.INIT
		// .getPercentComplete();

		return retVal;
	}

	@Override
	public BatchJob save(BatchJob batchJob) {
		return save(getBean(batchJob));
	}

	public UrmJobEntity save(UrmJobEntity job) {
		if (job == null) {
			throw new IllegalArgumentException("null job");
		}
		if (!job.isPersistent()) {
			em.persist(job);
		} else {
			job = em.merge(job);
		}
		return job;
	}

	@Override
	public BatchJob findUrmJob(long id) {
		UrmJobEntity job = em.find(UrmJobEntity.class, id);
		return job;
	}

	@Override
	public List<BatchJob> findAllUrmJobs() {
		Query query = em.createNamedQuery(UrmJobJPA.QN_URM_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<BatchJob> retVal = query.getResultList();
		if (retVal == null) {
			retVal = new ArrayList<BatchJob>();
		}
		return retVal;
	}

	@Override
	public List<BatchJob> findAllLinkedByUrmId(long urmId) {
		Query query =
			em.createNamedQuery(UrmJobJPA.QN_URM_FIND_ALL_BY_URM_ID);
		query.setParameter(UrmJobJPA.PN_URM_FIND_ALL_BY_URM_ID_URMID,
				urmId);
		@SuppressWarnings("unchecked")
		List<BatchJob> retVal = query.getResultList();
		if (retVal == null) {
			retVal = new ArrayList<BatchJob>();
		}
		return retVal;
	}

	@Override
	public void delete(BatchJob urmJob) {
		if (urmJob.isPersistent()) {
			UrmJobEntity bean =
				em.find(UrmJobEntity.class, urmJob.getId());
			delete(bean);
		}
	}

	void delete(UrmJobEntity bean) {
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

}
