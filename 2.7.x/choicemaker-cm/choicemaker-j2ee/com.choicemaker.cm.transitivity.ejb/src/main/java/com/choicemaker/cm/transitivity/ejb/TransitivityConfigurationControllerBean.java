/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationControllerBean;
import com.choicemaker.cm.transitivity.api.TransitivityConfigurationController;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;

@Stateless
@TransactionAttribute(REQUIRED)
public class TransitivityConfigurationControllerBean
		extends ServerConfigurationControllerBean
		implements TransitivityConfigurationController {

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityJobManager jobManager;

	@TransactionAttribute(SUPPORTS)
	@Override
	public ServerConfiguration findConfigurationByTransitivityJobId(
			long jobId) {
		ServerConfiguration retVal = null;
		BatchJob batchJob = jobManager.findTransitivityJob(jobId);
		if (batchJob != null) {
			long serverId = batchJob.getServerId();
			retVal = findServerConfiguration(serverId);
		}
		return retVal;
	}

}
