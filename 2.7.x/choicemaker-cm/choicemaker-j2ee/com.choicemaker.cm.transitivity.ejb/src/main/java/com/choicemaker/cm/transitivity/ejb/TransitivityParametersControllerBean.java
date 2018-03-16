/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;

/**
 * An EJB used to test TransitivityParameter beans within container-defined
 * transactions; see {@link TransitivityJobManagerBean} as an example of a
 * similar controller.
 *
 * @author rphall
 */
@Stateless
public class TransitivityParametersControllerBean implements
		TransitivityParametersController {

	private static final Logger logger = Logger
			.getLogger(TransitivityParametersControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityJobManager jobManager;

	protected TransitivityJobManager getTransJobController() {
		return jobManager;
	}

	protected TransitivityParametersEntity getBean(TransitivityParameters p) {
		TransitivityParametersEntity retVal = null;
		if (p != null) {
			final long jobId = p.getId();
			if (p instanceof TransitivityParametersEntity) {
				retVal = (TransitivityParametersEntity) p;
			} else {
				if (p.isPersistent()) {
					retVal = em.find(TransitivityParametersEntity.class, jobId);
					if (retVal == null) {
						String msg =
							"Unable to find persistent Transitivity job: "
									+ jobId;
						logger.warning(msg);
					}
				}
			}
			if (retVal == null) {
				retVal = new TransitivityParametersEntity(p);
			}
		}
		return retVal;
	}

	@Override
	public TransitivityParameters save(TransitivityParameters p) {
		return save(getBean(p));
	}

	TransitivityParametersEntity save(TransitivityParametersEntity p) {
		logger.fine("Saving " + p);
		if (p.getId() == 0) {
			em.persist(p);
			logger.fine("Saved " + p);
		} else {
			p = em.merge(p);
			em.flush();
			logger.fine("Merged " + p);
		}
		return p;
	}

	@Override
	public TransitivityParameters findTransitivityParameters(long id) {
		TransitivityParametersEntity p =
			em.find(TransitivityParametersEntity.class, id);
		return p;
	}

	@Override
	public TransitivityParameters findTransitivityParametersByBatchJobId(long jobId) {
		TransitivityParameters retVal = null;
		BatchJob job =
			getTransJobController().findTransitivityJob(jobId);
		if (job != null) {
			long paramsId = job.getParametersId();
			retVal = findTransitivityParameters(paramsId);
		}
		return retVal;
	}

	@Override
	public List<TransitivityParameters> findAllTransitivityParameters() {
		Query query =
			em.createNamedQuery(TransitivityParametersJPA.QN_TRANSPARAMETERS_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<TransitivityParameters> entries = query.getResultList();
		if (entries == null) {
			entries = new ArrayList<TransitivityParameters>();
		}
		return entries;
	}

	@Override
	public void delete(TransitivityParameters p) {
		if (p.isPersistent()) {
			TransitivityParametersEntity bean = getBean(p);
			bean = em.merge(bean);
			em.remove(bean);
			em.flush();
		}
	}

	@Override
	public void detach(TransitivityParameters p) {
		if (p.isPersistent()) {
			TransitivityParametersEntity bean = getBean(p);
			bean = em.merge(bean);
			em.detach(p);
		}
	}

}
