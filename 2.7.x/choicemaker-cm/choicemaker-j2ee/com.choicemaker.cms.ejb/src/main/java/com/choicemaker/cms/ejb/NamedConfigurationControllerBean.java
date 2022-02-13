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

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.api.remote.NamedConfigurationControllerRemote;

/**
 * In addition to managing NamedConfiguration entities, this bean also lists
 * other named entities such as model configurations and server configurations.
 *
 * @author rphall
 *
 */
@Stateless
@Local(NamedConfigurationController.class)
@Remote(NamedConfigurationControllerRemote.class)
@TransactionAttribute(REQUIRED)
public class NamedConfigurationControllerBean
		implements NamedConfigurationController {

	private static final Logger logger =
		Logger.getLogger(NamedConfigurationControllerBean.class.getName());

	public static int computeAvailableProcessors() {
		int retVal = Runtime.getRuntime().availableProcessors();
		return retVal;
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private ServerConfigurationController serverController;

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<String> findAllModelConfigurationNames() {
		ImmutableProbabilityModel[] models = PMManager.getModels();
		List<String> retVal = new ArrayList<>();
		for (ImmutableProbabilityModel model : models) {
			String name = model.getModelName();
			retVal.add(name);
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<String> findAllServerConfigurationNames() {
		List<ServerConfiguration> serverConfigurations =
			serverController.findAllServerConfigurations();
		List<String> retVal = new ArrayList<>();
		for (ServerConfiguration serverConfiguration : serverConfigurations) {
			String name = serverConfiguration.getName();
			retVal.add(name);
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public NamedConfigurationEntity findNamedConfiguration(long id) {
		NamedConfigurationEntity retVal =
			em.find(NamedConfigurationEntity.class, id);
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public NamedConfigurationEntity findNamedConfigurationByName(
			String configName) {
		Query query = em.createNamedQuery(
				NamedConfigurationJPA.QN_NAMEDCONFIG_FIND_BY_NAME);
		query.setParameter(NamedConfigurationJPA.PN_NAMEDCONFIG_FIND_BY_NAME_P1,
				configName);
		@SuppressWarnings("unchecked")
		List<NamedConfigurationEntity> beans = query.getResultList();

		NamedConfigurationEntity retVal = null;
		if (beans.size() > 1) {
			String msg = "non-unique configuration name: " + configName;
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
	public List<NamedConfiguration> findAllNamedConfigurations() {
		Query query =
			em.createNamedQuery(NamedConfigurationJPA.QN_NAMEDCONFIG_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<NamedConfigurationEntity> beans = query.getResultList();
		List<NamedConfiguration> retVal = new ArrayList<>();
		if (beans != null) {
			retVal.addAll(beans);
		}
		return retVal;
	}

	@Override
	public NamedConfiguration clone(NamedConfiguration sc) {
		NamedConfigurationEntity retVal = new NamedConfigurationEntity(sc);
		return retVal;
	}

	@Override
	public void remove(NamedConfiguration nc) {
		if (nc != null) {
			NamedConfigurationEntity nce = findNamedConfiguration(nc.getId());
			if (nce != null) {
				em.remove(nce);
			}
		}
	}

	@Override
	public NamedConfiguration save(NamedConfiguration nc) {
		if (nc == null) {
			throw new IllegalArgumentException("null configuration");
		}

		NamedConfigurationEntity nce = null;
		if (!(nc instanceof NamedConfigurationEntity)) {
			nce = new NamedConfigurationEntity(nc);
		} else {
			nce = (NamedConfigurationEntity) nc;
		}
		assert nce != null;

		NamedConfiguration retVal = null;
		if (PersistentObject.NONPERSISTENT_ID == nce.getId()) {
			em.persist(nce);
			retVal = nce;
		} else {
			retVal = em.merge(nce);
		}

		assert retVal != null;
		return retVal;
	}

}
