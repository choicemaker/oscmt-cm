package com.choicemaker.cm.batch.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_DELETE_BY_JOB_PNAME_P1;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_DELETE_BY_JOB_PNAME_P2;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P1;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P2;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P3;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.QN_IDXPROP_DELETE_BY_JOB_PNAME;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.QN_IDXPROP_FIND_BY_JOB_PNAME_INDEX;

import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_FIND_BY_JOB_PNAME_P1;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.PN_IDXPROP_FIND_BY_JOB_PNAME_P2;
import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.QN_IDXPROP_FIND_BY_JOB_PNAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.IndexedProperty;
import com.choicemaker.cm.batch.api.IndexedPropertyController;
import com.choicemaker.util.Precondition;

@Stateless
@TransactionAttribute(REQUIRED)
public class IndexedPropertyControllerBean
		implements IndexedPropertyController {

	private static final Logger logger =
		Logger.getLogger(IndexedPropertyControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Override
	public int deleteIndexedPropertiesByJobIdName(long jobId, String name) {
		Precondition.assertNonEmptyString(name);
		int retVal = 0;
		if (AbstractPersistentObject.isPersistentId(jobId)) {
			Query query = em.createNamedQuery(QN_IDXPROP_DELETE_BY_JOB_PNAME);
			query.setParameter(PN_IDXPROP_DELETE_BY_JOB_PNAME_P1, jobId);
			query.setParameter(PN_IDXPROP_DELETE_BY_JOB_PNAME_P2, name);
			int deletedCount = query.executeUpdate();
			return deletedCount;
		}
		return retVal;
	}

	@Override
	public IndexedProperty find(BatchJob job, String name, int index) {
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		return findInternal(job.getId(), name, index);
	}

	@Override
	public Map<Integer, String> find(BatchJob job, String name) {
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		Precondition.assertNonEmptyString(name);
		Map<Integer, String> retVal = new HashMap<>();
		final long jobId = job.getId();
		Query query = em.createNamedQuery(QN_IDXPROP_FIND_BY_JOB_PNAME);
		query.setParameter(PN_IDXPROP_FIND_BY_JOB_PNAME_P1, jobId);
		query.setParameter(PN_IDXPROP_FIND_BY_JOB_PNAME_P2, name);
		@SuppressWarnings("unchecked")
		final List<IndexedPropertyEntity> beans = query.getResultList();
		assert beans != null;
		for (IndexedPropertyEntity bean : beans) {
			String previous = retVal.put(bean.getIndex(), bean.getValue());
			if (previous != null) {
				String msg0 = "Multiple values for Indexed property [jobId=%d, "
						+ "name=%s, index=%d]: %s, %s";
				String msg =
					String.format(msg0, bean.getJobId(), bean.getName(),
							bean.getIndex(), previous, bean.getValue());
				logger.warning(msg);
			}
		}
		return retVal;
	}

	@Override
	public IndexedProperty find(long propertyId) {
		return findInternal(propertyId);
	}

	protected IndexedPropertyEntity findInternal(long propertyId) {
		IndexedPropertyEntity retVal = null;
		if (AbstractPersistentObject.isPersistentId(propertyId)) {
			retVal = em.find(IndexedPropertyEntity.class, propertyId);
		}
		return retVal;
	}

	protected IndexedPropertyEntity findInternal(final long jobId,
			final String name, int index) {
		if (name == null || !name.equals(name.trim()) || name.isEmpty()) {
			throw new IllegalArgumentException(
					"invalid property name: '" + name + "'");
		}

		final String stdName = name.toUpperCase();
		if (!name.equals(stdName)) {
			logger.fine("Converting property name '" + name
					+ "' to upper-case '" + stdName + "'");
		}

		IndexedPropertyEntity retVal = null;
		if (AbstractPersistentObject.isPersistentId(jobId)) {
			Query query =
				em.createNamedQuery(QN_IDXPROP_FIND_BY_JOB_PNAME_INDEX);
			query.setParameter(PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P1, jobId);
			query.setParameter(PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P2, stdName);
			query.setParameter(PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P3, index);
			@SuppressWarnings("unchecked")
			final List<IndexedPropertyEntity> beans = query.getResultList();
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

	@Override
	public String getIndexedPropertyValue(BatchJob job, String pn, int index) {
		IndexedProperty op = find(job, pn, index);
		String retVal = op == null ? null : op.getValue();
		return retVal;
	}

	@Override
	public void remove(IndexedProperty property) {
		if (property != null) {
			IndexedPropertyEntity p = findInternal(property.getId());
			if (p != null) {
				em.remove(p);
			}
		}
	}

	@Override
	public IndexedProperty save(IndexedProperty p) {
		logger.fine("Saving " + p);
		if (p == null) {
			throw new IllegalArgumentException("null property");
		}
		// Have the settings already been persisted?
		final long pid = p.getId();
		IndexedPropertyEntity retVal = null;
		if (p.isPersistent()) {
			// Settings appear to be persistent -- check them against the DB
			retVal = findInternal(pid);
			if (retVal == null) {
				String msg = "The specified property (" + pid
						+ ") is missing in the DB. "
						+ "The search will continue by job id, property name "
						+ "and index.";
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
			retVal = findInternal(p.getJobId(), p.getName(), p.getIndex());
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

	protected IndexedPropertyEntity saveInternal(IndexedProperty p) {
		assert p != null;
		assert !p.isPersistent();

		// Save the specified property to the DB
		IndexedPropertyEntity retVal;
		if (p instanceof IndexedPropertyEntity) {
			retVal = (IndexedPropertyEntity) p;
		} else {
			retVal = new IndexedPropertyEntity(p);
		}
		assert !retVal.isPersistent();
		em.persist(retVal);
		assert retVal.isPersistent();
		String msg = "Persistent: " + retVal;
		logger.fine(msg);
		return retVal;
	}

	@Override
	public void setIndexedPropertyValue(BatchJob job, String pn, int index,
			String pv) {
		if (job == null || pn == null || pv == null) {
			throw new IllegalArgumentException("null argument");
		}
		IndexedProperty ip = new IndexedPropertyEntity(job, pn, index, pv);
		save(ip);
	}

	protected IndexedPropertyEntity updateInternal(IndexedProperty p) {
		assert p != null;
		assert p.isPersistent();

		final long pid = p.getId();
		IndexedPropertyEntity ipe = em.find(IndexedPropertyEntity.class, pid);
		logger.finer("DB version before update: " + ipe);
		ipe.updateValue(p.getValue());
		logger.finer("DB version before merge: " + ipe);
		em.merge(ipe);
		logger.finer("DB version after merge: " + ipe);
		em.flush();
		logger.finer("DB version after flush: " + ipe);
		return ipe;
	}

}
