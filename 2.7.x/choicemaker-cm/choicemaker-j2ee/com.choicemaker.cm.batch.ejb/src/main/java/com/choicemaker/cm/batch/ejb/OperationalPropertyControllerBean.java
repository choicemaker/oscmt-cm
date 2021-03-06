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
package com.choicemaker.cm.batch.ejb;

import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.PN_OPPROP_DELETE_BY_JOB_P1;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.PN_OPPROP_FINDALL_BY_JOB_P1;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.PN_OPPROP_FIND_BY_JOB_PNAME_P1;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.PN_OPPROP_FIND_BY_JOB_PNAME_P2;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_DELETE_BY_JOB;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_FINDALL;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_FINDALL_BY_JOB;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_FIND_BY_JOB_PNAME;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.OperationalProperty;
import com.choicemaker.cm.batch.api.OperationalPropertyController;

@Stateless
@TransactionAttribute(REQUIRED)
public class OperationalPropertyControllerBean
		implements OperationalPropertyController {

	private static final Logger logger =
		Logger.getLogger(OperationalPropertyControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Override
	public void setJobProperty(BatchJob job, String pn, String pv) {
		if (job == null || pn == null || pv == null) {
			throw new IllegalArgumentException("null argument");
		}
		OperationalProperty op = new OperationalPropertyEntity(job, pn, pv);
		save(op);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public String getOperationalPropertyValue(BatchJob job, String pn) {
		OperationalProperty op = findOperationalProperty(job, pn);
		String retVal = op == null ? null : op.getValue();
		return retVal;
	}

	@Override
	public OperationalProperty save(OperationalProperty p) {
		logger.fine("Saving " + p);
		if (p == null) {
			throw new IllegalArgumentException("null settings");
		}
		// Have the settings already been persisted?
		final long pid = p.getId();
		OperationalPropertyEntity retVal = null;
		if (p.isPersistent()) {
			// Settings appear to be persistent -- check them against the DB
			retVal = findInternal(pid);
			if (retVal == null) {
				String msg = "The specified property (" + pid
						+ ") is missing in the DB. "
						+ "The search will continue by job id and name.";
				logger.fine(msg);
				retVal = null;
			} else if (!retVal.equals(p)) {
				String msg = "The specified property (" + p
						+ ") is different in the DB. "
						+ "The DB value will be updated from '"
						+ retVal.getValue() + "' to '" + p.getValue() + "'";
				logger.fine(msg);
				retVal = updateInternal(p);
			}
		}
		if (retVal == null) {
			retVal = findInternal(p.getJobId(), p.getName());
			if (retVal == null) {
				String msg = "The specified property (jobId: " + p.getJobId()
						+ ", name: " + p.getName() + ") is missing in the DB. "
						+ "A new entry will be created with the value '"
						+ p.getValue() + "'.";
				logger.fine(msg);
				retVal = null;
			} else if (!retVal.equals(p)) {
				String msg = "The specified property (" + p
						+ ") is different in the DB. "
						+ "The DB value will be updated from '"
						+ retVal.getValue() + "' to '" + p.getValue() + "'";
				logger.fine(msg);
				retVal.updateValue(p.getValue());
				retVal = updateInternal(retVal);
			}
		}
		if (retVal == null) {
			retVal = saveInternal(p);
		}
		assert retVal != null;
		assert retVal.isPersistent();
		logger.fine("Saved " + retVal);

		return retVal;
	}

	protected OperationalPropertyEntity saveInternal(OperationalProperty p) {
		assert p != null;
		assert !p.isPersistent();

		// Save the specified property to the DB
		OperationalPropertyEntity retVal;
		if (p instanceof OperationalPropertyEntity) {
			retVal = (OperationalPropertyEntity) p;
		} else {
			retVal = new OperationalPropertyEntity(p);
		}
		assert !retVal.isPersistent();
		em.persist(retVal);
		assert retVal.isPersistent();
		String msg = "Persistent: " + retVal;
		logger.fine(msg);
		return retVal;
	}

	protected OperationalPropertyEntity updateInternal(OperationalProperty p) {
		assert p != null;
		assert p.isPersistent();

		final long pid = p.getId();
		OperationalPropertyEntity ope =
			em.find(OperationalPropertyEntity.class, pid);
		logger.finer("DB version before update: " + ope);
		ope.updateValue(p.getValue());
		logger.finer("DB version before merge: " + ope);
		em.merge(ope);
		logger.finer("DB version after merge: " + ope);
		em.flush();
		logger.finer("DB version after flush: " + ope);
		return ope;
	}

	@Override
	public void remove(OperationalProperty property) {
		if (property != null) {
			OperationalPropertyEntity ope = findInternal(property.getId());
			if (ope != null) {
				em.remove(ope);
			}
		}
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public OperationalProperty findOperationalProperty(long propertyId) {
		return findInternal(propertyId);
	}

	protected OperationalPropertyEntity findInternal(long propertyId) {
		OperationalPropertyEntity retVal = null;
		if (AbstractPersistentObject.isPersistentId(propertyId)) {
			retVal = em.find(OperationalPropertyEntity.class, propertyId);
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public OperationalProperty findOperationalProperty(final BatchJob job,
			final String name) {
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		return findInternal(job.getId(), name);
	}

	protected OperationalPropertyEntity findInternal(final long jobId,
			final String name) {
		if (name == null || !name.equals(name.trim()) || name.isEmpty()) {
			throw new IllegalArgumentException(
					"invalid property name: '" + name + "'");
		}

		final String stdName = name.toUpperCase();
		if (!name.equals(stdName)) {
			logger.fine("Converting property name '" + name
					+ "' to upper-case '" + stdName + "'");
		}

		OperationalPropertyEntity retVal = null;
		if (AbstractPersistentObject.isPersistentId(jobId)) {
			Query query = em.createNamedQuery(QN_OPPROP_FIND_BY_JOB_PNAME);
			query.setParameter(PN_OPPROP_FIND_BY_JOB_PNAME_P1, jobId);
			query.setParameter(PN_OPPROP_FIND_BY_JOB_PNAME_P2, stdName);
			@SuppressWarnings("unchecked")
			final List<OperationalPropertyEntity> beans = query.getResultList();
			if (beans != null && beans.size() == 1) {
				retVal = beans.get(0);
			} else if (beans != null && beans.size() > 1) {
				String msg = "Integrity constraint violated: "
						+ "multiple values for the same job/property-name: "
						+ beans;
				throw new IllegalStateException(msg);
			}
		}
		assert retVal == null || retVal.isPersistent();

		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<OperationalProperty> findOperationalProperties(BatchJob job) {
		List<OperationalProperty> retVal = new LinkedList<>();
		final long jobId = job.getId();
		if (job.isPersistent()) {
			Query query = em.createNamedQuery(QN_OPPROP_FINDALL_BY_JOB);
			query.setParameter(PN_OPPROP_FINDALL_BY_JOB_P1, jobId);
			@SuppressWarnings("unchecked")
			final List<OperationalPropertyEntity> beans = query.getResultList();
			assert beans != null;
			for (OperationalPropertyEntity bean : beans) {
				retVal.add(bean);
			}
		}
		return retVal;
	}

	@Override
	public int deleteOperationalPropertiesByJobId(long jobId) {
		int retVal = 0;
		if (AbstractPersistentObject.isPersistentId(jobId)) {
			Query query = em.createNamedQuery(QN_OPPROP_DELETE_BY_JOB);
			query.setParameter(PN_OPPROP_DELETE_BY_JOB_P1, jobId);
			int deletedCount = query.executeUpdate();
			return deletedCount;
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<OperationalProperty> findAllOperationalProperties() {
		List<OperationalProperty> retVal = new LinkedList<>();
		Query query = em.createNamedQuery(QN_OPPROP_FINDALL);
		@SuppressWarnings("unchecked")
		final List<OperationalPropertyEntity> beans = query.getResultList();
		assert beans != null;
		for (OperationalProperty bean : beans) {
			retVal.add(bean);
		}
		return retVal;
	}

}
