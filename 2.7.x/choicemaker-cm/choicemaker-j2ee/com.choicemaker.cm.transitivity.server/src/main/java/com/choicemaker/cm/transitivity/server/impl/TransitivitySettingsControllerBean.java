/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.server.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaSettingsControllerBean;
import com.choicemaker.cm.transitivity.server.ejb.TransitivityJobController;
import com.choicemaker.cm.transitivity.server.ejb.TransitivitySettingsController;

@Stateless
public class TransitivitySettingsControllerBean extends OabaSettingsControllerBean implements TransitivitySettingsController {

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityJobController jobController;

	@Override
	public OabaSettings findSettingsByTransitivityJobId(long jobId) {
		OabaSettings retVal = null;
		BatchJob batchJob = jobController.findTransitivityJob(jobId);
		if (batchJob != null) {
			long settingsId = batchJob.getSettingsId();
			retVal = findOabaSettings(settingsId);
		}
		return retVal;
	}

}
