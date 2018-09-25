/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.api;

import java.util.List;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;

public interface TransitivityJobManager extends BatchJobManager {

	BatchJob createPersistentTransitivityJob(String externalID,
			TransitivityParameters params, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration sc)
			throws ServerConfigurationException;

	BatchJob createPersistentTransitivityJob(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException;

	List<BatchJob> findAllByOabaJobId(long oabaJobId);

	BatchJob findTransitivityJob(long id);

	List<BatchJob> findAllTransitivityJobs();

}
