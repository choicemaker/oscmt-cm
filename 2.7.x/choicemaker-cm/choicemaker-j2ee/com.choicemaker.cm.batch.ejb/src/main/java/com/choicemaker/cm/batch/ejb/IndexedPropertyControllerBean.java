package com.choicemaker.cm.batch.ejb;

import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.IndexedProperty;
import com.choicemaker.cm.batch.api.IndexedPropertyController;

@Stateless
public class IndexedPropertyControllerBean
		implements IndexedPropertyController {

	private static final Logger logger =
		Logger.getLogger(IndexedPropertyControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

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

	@Override
	public int deleteOperationalPropertiesByJobId(long jobId, String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IndexedProperty find(final BatchJob job, final String name) {
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		return findInternal(job.getId(), name);
	}

	@Override
	public Map<Integer, String> find(BatchJob job, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexedProperty find(BatchJob job, String name, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexedProperty find(long propertyId) {
		return findInternal(propertyId);
	}

	@Override
	public List<IndexedProperty> findAllByJob(BatchJob job) {
		List<IndexedProperty> retVal = new LinkedList<>();
		final long jobId = job.getId();
		if (job.isPersistent()) {
			Query query = em.createNamedQuery(QN_OPPROP_FINDALL_BY_JOB);
			query.setParameter(PN_OPPROP_FINDALL_BY_JOB_P1, jobId);
			@SuppressWarnings("unchecked")
			final List<IndexedPropertyEntity> beans = query.getResultList();
			assert beans != null;
			for (IndexedPropertyEntity bean : beans) {
				retVal.add(bean);
			}
		}
		return retVal;
	}

	@Override
	public List<IndexedProperty> findAllOperationalProperties() {
		List<IndexedProperty> retVal = new LinkedList<>();
		Query query = em.createNamedQuery(QN_OPPROP_FINDALL);
		@SuppressWarnings("unchecked")
		final List<IndexedPropertyEntity> beans = query.getResultList();
		assert beans != null;
		for (IndexedProperty bean : beans) {
			retVal.add(bean);
		}
		return retVal;
	}

	protected IndexedPropertyEntity findInternal(long propertyId) {
		IndexedPropertyEntity retVal = null;
		if (AbstractPersistentObject.isPersistentId(propertyId)) {
			retVal = em.find(IndexedPropertyEntity.class, propertyId);
		}
		return retVal;
	}

	protected IndexedPropertyEntity findInternal(final long jobId,
			final String name) {
		if (name == null || !name.equals(name.trim()) || name.isEmpty()) {
			throw new IllegalArgumentException(
					"invalid property name: '" + name + "'");
		}

		final String stdName = name.toUpperCase();
		if (!name.equals(stdName)) {
			logger.warning("Converting property name '" + name
					+ "' to upper-case '" + stdName + "'");
		}

		IndexedPropertyEntity retVal = null;
		if (AbstractPersistentObject.isPersistentId(jobId)) {
			Query query = em.createNamedQuery(QN_OPPROP_FIND_BY_JOB_PNAME);
			query.setParameter(PN_OPPROP_FIND_BY_JOB_PNAME_P1, jobId);
			query.setParameter(PN_OPPROP_FIND_BY_JOB_PNAME_P2, stdName);
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
	public String getJobProperty(BatchJob job, String pn) {
		IndexedProperty op = find(job, pn);
		String retVal = op == null ? null : op.getValue();
		return retVal;
	}

	@Override
	public void remove(IndexedProperty property) {
		if (property != null) {
			IndexedPropertyEntity ope = findInternal(property.getId());
			if (ope != null) {
				em.remove(ope);
			}
		}
	}

	@Override
	public IndexedProperty save(IndexedProperty p) {
		logger.fine("Saving " + p);
		if (p == null) {
			throw new IllegalArgumentException("null settings");
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
						+ "The search will continue by job id and name.";
				logger.warning(msg);
				retVal = null;
			} else if (!retVal.equals(p)) {
				String msg = "The specified property (" + p
						+ ") is different in the DB. "
						+ "The DB value will be updated from '"
						+ retVal.getValue() + "' to '" + p.getValue() + "'";
				logger.info(msg);
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
				logger.info(msg);
				retVal = null;
			} else if (!retVal.equals(p)) {
				String msg = "The specified property (" + p
						+ ") is different in the DB. "
						+ "The DB value will be updated from '"
						+ retVal.getValue() + "' to '" + p.getValue() + "'";
				logger.info(msg);
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
		logger.info(msg);
		return retVal;
	}

	@Override
	public void setJobProperty(BatchJob job, String pn, String pv) {
		if (job == null || pn == null || pv == null) {
			throw new IllegalArgumentException("null argument");
		}
		IndexedProperty op = new IndexedPropertyEntity(job, pn, pv);
		save(op);
	}

	protected IndexedPropertyEntity updateInternal(IndexedProperty p) {
		assert p != null;
		assert p.isPersistent();

		final long pid = p.getId();
		IndexedPropertyEntity ope =
			em.find(IndexedPropertyEntity.class, pid);
		logger.finer("DB version before update: " + ope);
		ope.updateValue(p.getValue());
		logger.finer("DB version before merge: " + ope);
		em.merge(ope);
		logger.finer("DB version after merge: " + ope);
		em.flush();
		logger.finer("DB version after flush: " + ope);
		return ope;
	}

}
