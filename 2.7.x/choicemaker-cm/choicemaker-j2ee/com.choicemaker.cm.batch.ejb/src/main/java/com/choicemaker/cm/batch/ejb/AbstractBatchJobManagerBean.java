package com.choicemaker.cm.batch.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobJPA.PN_BATCHJOB_FIND_BY_JOBID_P1;
import static com.choicemaker.cm.batch.ejb.BatchJobJPA.QN_BATCHJOB_FIND_BY_JOBID;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;

/**
 * Base class that implements batch job queries. (Derived classes implement
 * methods for creating, saving, deleting and detaching.)
 */
public abstract class AbstractBatchJobManagerBean implements BatchJobManager {

	private static final Logger logger =
			Logger.getLogger(AbstractBatchJobManagerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

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

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchJob> findAll() {
		Query query =
				em.createNamedQuery(BatchJobJPA.QN_BATCH_FIND_ALL);
			@SuppressWarnings("unchecked")
			List<BatchJob> retVal = query.getResultList();
			if (retVal == null) {
				retVal = new ArrayList<BatchJob>();
			}
			return retVal;
	}

}
