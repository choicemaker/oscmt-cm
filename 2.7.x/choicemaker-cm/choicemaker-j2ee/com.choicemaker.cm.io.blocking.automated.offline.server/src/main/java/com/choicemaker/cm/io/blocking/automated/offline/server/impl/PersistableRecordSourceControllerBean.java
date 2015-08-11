/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableFlatFileRecordSource;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.PersistableSqlRecordSource;
import com.choicemaker.cm.args.PersistableXmlRecordSource;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaJobController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaParametersController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.RecordSourceController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.SqlRecordSourceController;

@Stateless
public class PersistableRecordSourceControllerBean implements
		RecordSourceController {

	private static final Logger logger = Logger
			.getLogger(PersistableRecordSourceControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB(beanName = "OabaJobControllerBean")
	private OabaJobController jobController;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	private SqlRecordSourceController sqlRsController;

	@Override
	public ISerializableRecordSource getStageRs(OabaParameters params)
			throws Exception {
		ISerializableRecordSource retVal = null;
		if (params != null) {
			retVal =
				getRecordSource(params.getQueryRsId(), params.getQueryRsType());
		}
		return retVal;
	}

	@Override
	public ISerializableRecordSource getMasterRs(OabaParameters params)
			throws Exception {
		ISerializableRecordSource retVal = null;
		if (params != null) {
			retVal =
				getRecordSource(params.getReferenceRsId(),
						params.getReferenceRsType());
		}
		return retVal;
	}

	@Override
	public PersistableRecordSource save(final PersistableRecordSource psrs) {
		logger.fine("Saving " + psrs);
		if (psrs == null) {
			throw new IllegalArgumentException("null settings");
		}
		final String type = psrs.getType();
		PersistableRecordSource retVal = null;
		if (PersistableSqlRecordSource.TYPE.equals(type)) {
			assert psrs instanceof PersistableSqlRecordSource;
			PersistableSqlRecordSource sqlRs =
				(PersistableSqlRecordSource) psrs;
			retVal = sqlRsController.save(sqlRs);
			logger.fine("Saved " + retVal);
		} else if (PersistableFlatFileRecordSource.TYPE.equals(type)) {
			throw new Error("not yet implemented for record source type: '"
					+ type + "'");
		} else if (PersistableXmlRecordSource.TYPE.equals(type)) {
			throw new Error("not yet implemented for record source type: '"
					+ type + "'");
		} else {
			throw new IllegalStateException("unknown record source type: '"
					+ type + "'");
		}
		assert retVal != null;
		return retVal;
	}

	@Override
	public PersistableRecordSource find(Long id, String type) {
		PersistableRecordSource retVal = null;
		if (id != null) {
			// The typical case will be a SQL record source, so check it first
			retVal = sqlRsController.find(id, type);
			if (retVal == null) {
				// Here's where flatfile and XML sources would be checked
				logger.warning("Skipping flatfile and XML record sources");
			}
			if (retVal == null) {
				logger.warning("Record source " + id + " not found");
			}
		}
		return retVal;
	}

	@Override
	public ISerializableRecordSource getRecordSource(Long id, String type)
			throws Exception {
		ISerializableRecordSource retVal;
		if (id != null) {
			if (type == null) {
				throw new IllegalArgumentException("null type for id '" + id
						+ "'");
			}
			// FIXME replace hard-coded decision tree with plugins
			//
			// The usual case will be a SQL record source, so check this first
			retVal = sqlRsController.getRecordSource(id, type);
			if (retVal == null) {
				// Here's where flatfile and XML record sources should be
				// checked
				logger.warning("Skipping flatfile and XML record sources");
			}
			if (retVal == null) {
				logger.warning("Record source " + id + " not found");
			}
		} else {
			retVal = null;
			logger.fine("returning null record source for null id");
		}
		return retVal;
	}

	@Override
	public List<PersistableRecordSource> findAll() {
		List<PersistableRecordSource> retVal = new ArrayList<>();
		List<PersistableRecordSource> l0 = sqlRsController.findAll();
		if (l0 != null) {
			retVal.addAll(l0);
		}
		// Here's where flatfile and XML record sources should be added
		logger.warning("Skipping flatfile and XML record sources");
		return retVal;
	}

}
