/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.ejb.OabaSettingsControllerBean;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivitySettingsController;

@Stateless
public class TransitivitySettingsControllerBean extends
		OabaSettingsControllerBean implements TransitivitySettingsController {

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityJobManager jobManager;

	@Override
	public OabaSettings findSettingsByTransitivityJobId(long jobId) {
		OabaSettings retVal = null;
		BatchJob batchJob = jobManager.findTransitivityJob(jobId);
		if (batchJob != null) {
			long settingsId = batchJob.getSettingsId();
			retVal = findOabaSettings(settingsId);
		}
		return retVal;
	}

}
