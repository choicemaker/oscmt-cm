/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.PersistableSqlRecordSource;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.oaba.api.SqlRecordSourceController;

@Stateless
@TransactionAttribute(REQUIRED)
public class SqlRecordSourceControllerBean
		implements SqlRecordSourceController {

	private static final Logger logger =
		Logger.getLogger(SqlRecordSourceControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Override
	public PersistableSqlRecordSource save(final PersistableRecordSource rs) {
		logger.fine("Saving " + rs);
		if (rs == null) {
			throw new IllegalArgumentException("null settings");
		}
		if (!(rs instanceof PersistableSqlRecordSource)) {
			throw new IllegalArgumentException(
					"invalid type: " + rs.getClass().getName());
		}
		final String type = rs.getType();
		assert PersistableSqlRecordSource.TYPE.equals(type);

		// Has the record source already been persisted?
		final long rsId = rs.getId();
		SqlRecordSourceEntity retVal = null;
		if (rs.isPersistent()) {
			// The record source appears to be persistent -- check the DB
			retVal = findInternal(rsId);
			if (retVal == null) {
				String msg = "The specified record source (" + rsId
						+ ") is missing in the DB. "
						+ "A new copy will be persisted.";
				logger.warning(msg);
				retVal = null;
			} else if (!retVal.equals(rs)) {
				String msg = "The specified record source (" + rsId
						+ ") is different in the DB. The DB copy will be "
						+ "used instead of the specified record source.";
				logger.warning(msg);
			}
		}

		// Conditionally save the record to the DB
		if (retVal == null) {
			// Save the specified settings to the DB
			PersistableSqlRecordSource psrs = (PersistableSqlRecordSource) rs;
			retVal = new SqlRecordSourceEntity(psrs);
			assert !retVal.isPersistent();
			em.persist(retVal);
			assert retVal.isPersistent();
			String msg = "Saved record source in the database with id = "
					+ retVal.getId();
			logger.info(msg);
		}
		assert retVal != null;
		assert retVal.isPersistent();
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public PersistableSqlRecordSource find(Long id, String type) {
		PersistableSqlRecordSource retVal = null;
		if (id != null && PersistableSqlRecordSource.TYPE.equals(type)) {
			retVal = findInternal(id.longValue());
		}
		return retVal;
	}

	protected SqlRecordSourceEntity findInternal(long id) {
		return em.find(SqlRecordSourceEntity.class, id);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public ISerializableRecordSource getRecordSource(Long rsId, String type)
			throws Exception {
		ISerializableRecordSource retVal = null;
		if (rsId != null && PersistableSqlRecordSource.TYPE.equals(type)) {
			PersistableSqlRecordSource psrs = findInternal(rsId.longValue());
			if (psrs != null) {
				Class<?> c = Class.forName(psrs.getDatabaseReader());
				Constructor<?> ctor = c.getConstructor(String.class,
						String.class, String.class, String.class);
				retVal = (ISerializableRecordSource) ctor.newInstance(
						psrs.getDataSource(), psrs.getModelId(),
						psrs.getDatabaseConfiguration(),
						psrs.getSqlSelectStatement());
			}
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<PersistableRecordSource> findAll() {
		Query query = em.createNamedQuery(SqlRecordSourceJPA.QN_SQLRS_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<PersistableRecordSource> retVal = query.getResultList();
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public DataSource getStageDataSource(OabaParameters params)
			throws BlockingException {
		DataSource retVal = getDataSource(params.getQueryRsId());
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public DataSource getMasterDataSource(OabaParameters params)
			throws BlockingException {
		DataSource retVal = getDataSource(params.getReferenceRsId());
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public DataSource getDataSource(Long id) throws BlockingException {
		DataSource retVal = null;
		if (id != null) {
			PersistableRecordSource prs =
				find(id, PersistableSqlRecordSource.TYPE);
			if (prs != null) {
				assert prs instanceof PersistableSqlRecordSource;
				PersistableSqlRecordSource sqlrs =
					(PersistableSqlRecordSource) prs;
				String jndiName = sqlrs.getDataSource();
				retVal = getDataSource(jndiName);
			}
		} else {
			retVal = null;
			logger.fine("returning null DataSource for null id");
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public DataSource getDataSource(String jndiName) throws BlockingException {
		if (jndiName == null || !jndiName.trim().equals(jndiName)
				|| jndiName.isEmpty()) {
			String msg = "Invalid JNDI name '" + jndiName + "'";
			throw new IllegalArgumentException(msg);
		}
		DataSource retVal = null;
		try {
			Context ctx = new InitialContext();
			retVal = (DataSource) ctx.lookup(jndiName);
		} catch (NamingException ex) {
			String msg =
				"Unable to locate DataSource '" + jndiName + "': " + ex;
			logger.severe(ex.toString());
			throw new BlockingException(msg, ex);
		}
		assert retVal != null;

		return retVal;
	}

}
