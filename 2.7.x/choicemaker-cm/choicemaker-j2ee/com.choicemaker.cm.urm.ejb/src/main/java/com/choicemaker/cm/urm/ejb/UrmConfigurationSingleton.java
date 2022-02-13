/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.api.UrmConfiguration;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.exceptions.ConfigException;

@Singleton
@TransactionAttribute(REQUIRED)
public class UrmConfigurationSingleton implements UrmConfigurationAdapter {

	private static final Logger logger =
		Logger.getLogger(UrmConfigurationSingleton.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@TransactionAttribute(SUPPORTS)
	@Override
	public UrmConfigurationEntity findUrmConfiguration(long id) {
		UrmConfigurationEntity retVal =
			em.find(UrmConfigurationEntity.class, id);
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public UrmConfiguration findUrmConfigurationByName(
			String urmConfigurationName) {
		Query query =
			em.createNamedQuery(UrmConfigurationJPA.QN_URMCONFIG_FIND_BY_NAME);
		query.setParameter(UrmConfigurationJPA.PN_URMCONFIG_FIND_BY_NAME_P1,
				urmConfigurationName);
		@SuppressWarnings("unchecked")
		List<UrmConfigurationEntity> beans = query.getResultList();

		UrmConfigurationEntity retVal = null;
		if (beans.size() > 1) {
			String msg =
				"non-unique configuration name: " + urmConfigurationName;
			logger.severe(msg);
			throw new IllegalStateException(msg);
		} else if (beans.size() == 1) {
			retVal = beans.get(0);
		} else {
			assert beans == null || beans.size() == 0;
			assert retVal == null;
		}

		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public String getCmsConfigurationName(String urmConfigurationName)
			throws ConfigException, DatabaseException {
		String retVal = null;
		UrmConfiguration uc = findUrmConfigurationByName(urmConfigurationName);
		if (uc != null) {
			retVal = uc.getCmsConfigurationName();
		} else {
			String msg = "No CMS configuration mapped to URM configuration '"
					+ urmConfigurationName + "'";
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<UrmConfiguration> findAllUrmConfigurations() {
		Query query =
			em.createNamedQuery(UrmConfigurationJPA.QN_URMCONFIG_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<UrmConfigurationEntity> beans = query.getResultList();
		List<UrmConfiguration> retVal = new ArrayList<>();
		if (beans != null) {
			retVal.addAll(beans);
		}
		return retVal;
	}

}
