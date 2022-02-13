/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.util.List;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;

/**
 * Creates, saves, deletes and finds Offline Automated Batch Algorithm (OABA)
 * jobs.
 */
public interface OabaJobManager extends BatchJobManager {

	BatchJob createPersistentOabaJob(String externalID, OabaParameters params,
			OabaSettings settings, ServerConfiguration sc)
			throws ServerConfigurationException;

	BatchJob createPersistentOabaJob(String externalID,
			OabaParameters batchParams, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration, BatchJob urmJob)
			throws ServerConfigurationException;

	BatchJob findOabaJob(long id);

	List<BatchJob> findAllOabaJobs();

}
