package com.choicemaker.cm.batch.impl;

import static com.choicemaker.cm.batch.OperationalProperty.INVALID_ID;
import static com.choicemaker.cm.batch.impl.OperationalPropertyJPA.PN_OPPROP_FINDALL_BY_JOB_P1;
import static com.choicemaker.cm.batch.impl.OperationalPropertyJPA.PN_OPPROP_FIND_BY_JOB_PNAME_P1;
import static com.choicemaker.cm.batch.impl.OperationalPropertyJPA.PN_OPPROP_FIND_BY_JOB_PNAME_P2;
import static com.choicemaker.cm.batch.impl.OperationalPropertyJPA.QN_OPPROP_FINDALL_BY_JOB;
import static com.choicemaker.cm.batch.impl.OperationalPropertyJPA.QN_OPPROP_FIND_BY_JOB_PNAME;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.OperationalProperty;
import com.choicemaker.cm.batch.OperationalPropertyController;

@Stateless
public class OperationalPropertyControllerBean implements
		OperationalPropertyController {

	private static final Logger logger = Logger
			.getLogger(OperationalPropertyControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Override
	public OperationalProperty save(OperationalProperty p) {
		if (p == null) {
			throw new IllegalArgumentException("null settings");
		}
		// Have the settings already been persisted?
		final long pid = p.getId();
		OperationalPropertyEntity retVal = null;
		if (INVALID_ID != pid) {
			// Settings appear to be persistent -- check them against the DB
			retVal = findInternal(pid);
			if (retVal == null) {
				String msg =
					"The specified property (" + pid
							+ ") is missing in the DB. "
							+ "The search will continue by job id and name.";
				logger.warning(msg);
				retVal = null;
			} else if (!retVal.equals0(p)) {
				String msg =
					"The specified property ("
							+ pid
							+ ") is different in the DB. "
							+ "The DB value will be updated to the specified value.";
				logger.info(msg);
				retVal = updateInternal(p);
			}
		}
		if (retVal == null) {
			retVal = findInternal(p.getJobId(), p.getName());
			if (retVal == null) {
				String msg =
					"The specified property (jobId: " + p.getJobId()
							+ ", name: " + p.getName()
							+ ") is missing in the DB. "
							+ "A new copy will be persisted.";
				logger.info(msg);
				retVal = null;
			} else if (!retVal.equals0(p)) {
				String msg =
					"The specified property (jobId: "
							+ p.getJobId()
							+ ", name: "
							+ p.getName()
							+ ") is different in the DB. "
							+ "The DB value will be updated to the specified value.";
				logger.info(msg);
				retVal = updateInternal(p);
			}
		}
		if (retVal == null) {
			retVal = saveInternal(p);
		}
		assert retVal != null;
		assert retVal.getId() != INVALID_ID;

		return retVal;
	}

	protected OperationalPropertyEntity saveInternal(OperationalProperty p) {
		assert p != null;
		assert p.getId() == INVALID_ID;

		// Save the specified property to the DB
		OperationalPropertyEntity retVal = new OperationalPropertyEntity(p);
		assert retVal.getId() == INVALID_ID;
		em.persist(retVal);
		assert retVal.getId() != INVALID_ID;
		String msg = "Persistent: " + retVal;
		logger.info(msg);
		return retVal;
	}

	@Override
	public OperationalProperty update(OperationalProperty property) {
		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

	protected OperationalPropertyEntity updateInternal(OperationalProperty p) {
		assert p != null;
		assert p.getId() != INVALID_ID;

		final long pid = p.getId();
		OperationalPropertyEntity ope =
			em.find(OperationalPropertyEntity.class, pid);
		logger.finer("DB version before merge: " + ope);
		OperationalPropertyEntity retVal = new OperationalPropertyEntity(p);
		logger.finer("New version before merge: " + retVal);
		em.merge(retVal);
		logger.finer("New version after merge: " + retVal);
		em.persist(retVal);
		logger.finer("DB version after merge: " + retVal);
		return retVal;
	}

	@Override
	public void remove(OperationalProperty property) {
		if (property != null && property.getId() != INVALID_ID) {
			OperationalProperty ope = new OperationalPropertyEntity(property);
			em.remove(ope);
		}
	}

	@Override
	public void remove(OperationalPropertyEntity ope) {
		if (ope != null && ope.getId() != INVALID_ID) {
			em.remove(ope);
		}
	}

	@Override
	public OperationalProperty find(long propertyId) {
		return findInternal(propertyId);
	}

	protected OperationalPropertyEntity findInternal(long propertyId) {
		OperationalPropertyEntity retVal = null;
		if (propertyId != INVALID_ID) {
			retVal = em.find(OperationalPropertyEntity.class, propertyId);
		}
		return retVal;
	}

	@Override
	public OperationalProperty find(final BatchJob job, final String name) {
		if (job == null || !BatchJobEntity.isPersistent(job)) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		return findInternal(job.getId(),name);
	}

	protected OperationalPropertyEntity findInternal(final long jobId, final String name) {
		if (name == null || !name.equals(name.trim()) || name.isEmpty()) {
			throw new IllegalArgumentException("invalid property name: '"
					+ name + "'");
		}

		final String stdName = name.toUpperCase();
		if (!name.equals(stdName)) {
			logger.warning("Converting property name '" + name
					+ "' to upper-case '" + stdName + "'");
		}

		OperationalPropertyEntity retVal = null;
		if (jobId != INVALID_ID) {
			Query query = em.createNamedQuery(QN_OPPROP_FIND_BY_JOB_PNAME);
			query.setParameter(PN_OPPROP_FIND_BY_JOB_PNAME_P1, jobId);
			query.setParameter(PN_OPPROP_FIND_BY_JOB_PNAME_P2, stdName);
			@SuppressWarnings("unchecked")
			final List<OperationalPropertyEntity> beans = query.getResultList();
			if (beans != null && beans.size() == 1) {
				retVal = beans.get(0);
			} else if (beans != null && beans.size() > 1) {
				String msg = "FIXME";
				throw new IllegalStateException(msg);
			}
		}
		assert retVal == null || retVal.getJobId() != INVALID_ID;

		return retVal;
	}

	@Override
	public List<OperationalProperty> findAllByJob(BatchJob job) {
		List<OperationalProperty> retVal = new LinkedList<>();
		final long jobId = job.getId();
		if (jobId != INVALID_ID) {
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

}
