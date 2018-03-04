package com.choicemaker.cm.urm.ejb;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.api.UrmConfiguration;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;

/**
 * In addition to managing UrmConfiguration entities, this bean also lists
 * other named entities such as model configurations and server configurations.
 *
 * @author rphall
 *
 */
@Stateless
@Local(UrmConfigurationAdapter.class)
public class UrmConfigurationControllerBean implements UrmConfigurationAdapter {

	private static final Logger logger = Logger
			.getLogger(UrmConfigurationControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;
	
	@Override
	public UrmConfigurationEntity findUrmConfiguration(long id) {
		UrmConfigurationEntity retVal =
			em.find(UrmConfigurationEntity.class, id);
		return retVal;
	}

	@Override
	public UrmConfiguration findUrmConfigurationByName(
			String urmConfigurationName) {
		Query query =
			em.createNamedQuery(UrmConfigurationJPA.QN_URMCONFIG_FIND_BY_NAME);
		query.setParameter(
				UrmConfigurationJPA.PN_URMCONFIG_FIND_BY_NAME_P1,
				urmConfigurationName);
		@SuppressWarnings("unchecked")
		List<UrmConfigurationEntity> beans = query.getResultList();

		UrmConfigurationEntity retVal = null;
		if (beans.size() > 1) {
			String msg = "non-unique configuration name: " + urmConfigurationName;
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

	@Override
	public String getCmsConfigurationName(String urmConfigurationName)
			throws DatabaseException {
		String retVal = null;
		UrmConfiguration uc = findUrmConfigurationByName(urmConfigurationName);
		if (uc != null) {
			retVal = uc.getCmsConfigurationName();
		}
		return retVal;
	}

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
