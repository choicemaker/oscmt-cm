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

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.api.DefaultSettings;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaSettingsController;

@Stateless
@TransactionAttribute(REQUIRED)
public class OabaSettingsControllerBean implements OabaSettingsController {

	private static final Logger logger =
		Logger.getLogger(OabaSettingsControllerBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaJobManager jobManager;

	@Override
	public AbaSettings save(final AbaSettings settings) {
		logger.fine("Saving " + settings);
		if (settings == null) {
			throw new IllegalArgumentException("null settings");
		}
		// Have the settings already been persisted?
		final long settingsId = settings.getId();
		AbaSettingsEntity retVal = null;
		if (AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID != settingsId) {
			// Settings appear to be persistent -- check them against the DB
			retVal = findAbaSettingsInternal(settingsId);
			if (retVal == null) {
				String msg = "The specified settings (" + settingsId
						+ ") are missing in the DB. "
						+ "A new copy will be persisted.";
				logger.warning(msg);
				retVal = null;
			} else if (!retVal.equals(settings)) {
				String msg = "The specified settings (" + settingsId
						+ ") are different in the DB. "
						+ "The DB values will be used instead of the specified values.";
				logger.warning(msg);
			}
		}
		if (retVal == null) {
			// Save the specified settings to the DB
			retVal = new AbaSettingsEntity(settings);
			assert retVal.getId() == AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;
			em.persist(retVal);
			assert retVal.getId() != AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;
			String msg =
				"The specified settings were persisted in the database with settings id = "
						+ retVal.getId();
			logger.info(msg);
		}
		assert retVal != null;
		assert retVal.getId() != AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;
		logger.fine("Saved " + retVal);

		return retVal;
	}

	@Override
	public OabaSettings save(final OabaSettings settings) {
		if (settings == null) {
			throw new IllegalArgumentException("null settings");
		}
		// Have the settings already been persisted?
		final long settingsId = settings.getId();
		OabaSettingsEntity retVal = null;
		if (AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID != settingsId) {
			// Settings appear to be persistent -- check them against the DB
			retVal = findOabaSettingsInternal(settingsId);
			if (retVal == null) {
				String msg = "The specified settings (" + settingsId
						+ ") are missing in the DB. "
						+ "A new copy will be persisted.";
				logger.warning(msg);
				retVal = null;
			} else if (!retVal.equals(settings)) {
				String msg = "The specified settings (" + settingsId
						+ ") are different in the DB. "
						+ "The DB values will be used instead of the specified values.";
				logger.warning(msg);
			}
		}
		if (retVal == null) {
			// Save the specified settings to the DB
			retVal = new OabaSettingsEntity(settings);
			assert retVal.getId() == AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;
			em.persist(retVal);
			assert retVal.getId() != AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;
			String msg =
				"The specified settings were persisted in the database with settings id = "
						+ retVal.getId();
			logger.info(msg);
		}
		assert retVal != null;
		assert retVal.getId() != AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public AbaSettings findAbaSettings(long id) {
		return findAbaSettingsInternal(id);
	}

	protected AbaSettingsEntity findAbaSettingsInternal(long id) {
		return em.find(AbaSettingsEntity.class, id);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public OabaSettings findOabaSettings(long id) {
		return findOabaSettingsInternal(id);
	}

	protected OabaSettingsEntity findOabaSettingsInternal(long id) {
		return em.find(OabaSettingsEntity.class, id);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public OabaSettings findOabaSettingsByJobId(long jobId) {
		OabaSettings retVal = null;
		BatchJob batchJob = jobManager.findBatchJob(jobId);
		if (batchJob != null) {
			long settingsId = batchJob.getSettingsId();
			retVal = findOabaSettings(settingsId);
		}
		return retVal;
	}

	@Override
	public DefaultSettingsEntity setDefaultAbaConfiguration(
			ImmutableProbabilityModel model, String databaseConfiguration,
			String blockingConfiguration, AbaSettings settings) {
		if (settings == null) {
			throw new IllegalArgumentException("null settings");
		}

		// Create a primary key for the default (validates other arguments)
		final DefaultSettingsPKBean pk = new DefaultSettingsPKBean(
				model.getModelName(), AbaSettingsJPA.DISCRIMINATOR_VALUE,
				databaseConfiguration, blockingConfiguration);

		// Remove the existing default if it is different
		AbaSettings aba = null;
		DefaultSettingsEntity old = em.find(DefaultSettingsEntity.class, pk);
		if (old != null) {
			aba = findAbaSettings(old.getSettingsId());
			if (!settings.equals(aba)) {
				aba = null;
				em.remove(old);
			}
		}

		// Conditionally save the specified settings as the new default
		DefaultSettingsEntity retVal = null;
		if (aba == null) {
			aba = save(settings);
			retVal = new DefaultSettingsEntity(pk, aba.getId());
			em.persist(retVal);
		}
		assert retVal != null;
		assert aba != null;
		assert aba.getId() != AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;

		return retVal;
	}

	@Override
	public DefaultSettingsEntity setDefaultOabaConfiguration(
			ImmutableProbabilityModel model, String databaseConfiguration,
			String blockingConfiguration, OabaSettings settings) {
		if (settings == null) {
			throw new IllegalArgumentException("null settings");
		}

		// Create a primary key for the default (validates other arguments)
		final DefaultSettingsPKBean pk = new DefaultSettingsPKBean(
				model.getModelName(), OabaSettingsJPA.DISCRIMINATOR_VALUE,
				databaseConfiguration, blockingConfiguration);

		// Remove the existing default if it is different
		OabaSettings oaba = null;
		DefaultSettingsEntity old = em.find(DefaultSettingsEntity.class, pk);
		if (old != null) {
			oaba = findOabaSettings(old.getSettingsId());
			if (!settings.equals(oaba)) {
				oaba = null;
				em.remove(old);
			}
		}

		// Conditionally save the specified settings as the new default
		DefaultSettingsEntity retVal = null;
		if (oaba == null) {
			oaba = save(settings);
			retVal = new DefaultSettingsEntity(pk, oaba.getId());
			em.persist(retVal);
		}
		assert retVal != null;
		assert oaba != null;
		assert oaba.getId() != AbaSettings.NONPERSISTENT_ABA_SETTINGS_ID;

		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public AbaSettings findDefaultAbaSettings(String modelConfigurationId,
			String databaseConfiguration, String blockingConfiguration) {
		final DefaultSettingsPKBean pk = new DefaultSettingsPKBean(
				modelConfigurationId, AbaSettingsJPA.DISCRIMINATOR_VALUE,
				databaseConfiguration, blockingConfiguration);
		DefaultSettingsEntity dsb = em.find(DefaultSettingsEntity.class, pk);
		AbaSettings retVal = null;
		if (dsb != null) {
			final long settingsId = dsb.getSettingsId();
			retVal = findAbaSettings(settingsId);
			if (retVal == null) {
				String msg = "Invalid settings identifier for " + pk.toString()
						+ ": " + settingsId;
				logger.severe(msg);
			}
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public OabaSettings findDefaultOabaSettings(String modelConfigurationId,
			String databaseConfiguration, String blockingConfiguration) {
		final DefaultSettingsPKBean pk = new DefaultSettingsPKBean(
				modelConfigurationId, OabaSettingsJPA.DISCRIMINATOR_VALUE,
				databaseConfiguration, blockingConfiguration);
		DefaultSettingsEntity dsb = em.find(DefaultSettingsEntity.class, pk);
		OabaSettings retVal = null;
		if (dsb != null) {
			final long settingsId = dsb.getSettingsId();
			retVal = findOabaSettings(settingsId);
			if (retVal == null) {
				String msg = "Invalid settings identifier for " + pk.toString()
						+ ": " + settingsId;
				logger.severe(msg);
			}
		}
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<AbaSettings> findAllAbaSettings() {
		Query query = em.createNamedQuery(AbaSettingsJPA.QN_ABA_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<AbaSettings> retVal = query.getResultList();
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<OabaSettings> findAllOabaSettings() {
		Query query = em.createNamedQuery(OabaSettingsJPA.QN_OABA_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<OabaSettings> retVal = query.getResultList();
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<DefaultSettings> findAllDefaultAbaSettings() {
		Query query =
			em.createNamedQuery(DefaultSettingsJPA.QN_DSET_FIND_ALL_ABA);
		@SuppressWarnings("unchecked")
		List<DefaultSettings> retVal = query.getResultList();
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<DefaultSettings> findAllDefaultOabaSettings() {
		Query query =
			em.createNamedQuery(DefaultSettingsJPA.QN_DSET_FIND_ALL_OABA);
		@SuppressWarnings("unchecked")
		List<DefaultSettings> retVal = query.getResultList();
		return retVal;
	}

}
