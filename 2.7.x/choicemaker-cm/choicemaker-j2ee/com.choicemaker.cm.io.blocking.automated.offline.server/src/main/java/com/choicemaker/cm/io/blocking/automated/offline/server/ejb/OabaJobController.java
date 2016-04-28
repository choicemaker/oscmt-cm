/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.ejb;

import javax.ejb.Local;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.BatchJobController;

@Local
public interface OabaJobController extends BatchJobController {

	BatchJob createPersistentOabaJob(String externalID, OabaParameters params,
			OabaSettings settings, ServerConfiguration sc)
			throws ServerConfigurationException;

	BatchJob createPersistentOabaJob(String externalID,
			OabaParameters batchParams, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration, BatchJob urmJob)
			throws ServerConfigurationException;

	BatchJob findOabaJob(long id);

	// OabaJobEntity save(OabaJobEntity job);

}