/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.ejb.AbstractBatchJobManagerBean;
import com.choicemaker.cms.api.UrmJobManager;

/**
 * A stateless EJB used to manage the persistence of UrmJobEntity instances.
 * 
 * @author rphall
 */
@Stateless
@TransactionAttribute(REQUIRED)
public class UrmJobManagerBean extends AbstractBatchJobManagerBean
		implements UrmJobManager {

	private static final Logger logger =
		Logger.getLogger(UrmJobManagerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

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
	public BatchJob createPersistentUrmJob(String externalID) {

		UrmJobEntity retVal = new UrmJobEntity(externalID);
		em.persist(retVal);
		assert retVal.isPersistent();

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

	@TransactionAttribute(SUPPORTS)
	@Override
	public BatchJob findUrmJob(long id) {
		UrmJobEntity job = em.find(UrmJobEntity.class, id);
		return job;
	}

	@TransactionAttribute(SUPPORTS)
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

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchJob> findAllLinkedByUrmId(long urmId) {
		Query query = em.createNamedQuery(UrmJobJPA.QN_URM_FIND_ALL_BY_URM_ID);
		query.setParameter(UrmJobJPA.PN_URM_FIND_ALL_BY_URM_ID_URMID, urmId);
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
			UrmJobEntity bean = em.find(UrmJobEntity.class, urmJob.getId());
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

}
