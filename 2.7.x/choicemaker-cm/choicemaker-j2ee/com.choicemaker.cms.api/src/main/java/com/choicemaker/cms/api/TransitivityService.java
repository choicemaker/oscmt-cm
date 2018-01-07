/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cms.api;

import javax.ejb.Local;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.io.blocking.automated.offline.core.RecordMatchingMode;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.ServerConfigurationException;

/**
 * This session bean allows the user to start, query, and get result from the
 * TE. It is to be used with the OABA.
 * 
 * @author pcheung
 *
 */
@Local
public interface TransitivityService {

	String DEFAULT_EJB_REF_NAME = "ejb/TransitivityService";
	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

	/**
	 * This method starts transitivity analysis of the specified OABA job. The
	 * OABA job must have completed successfully. Record matching is performed
	 * in the same mode as was used for the OABA job.
	 * @urmJob may be null
	 */
	long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException;

	/**
	 * This method starts transitivity analysis of the specified OABA job, but
	 * allows a different record-matching mode to be specified.
	 * @urmJob may be null
	 */
	long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob, RecordMatchingMode mode)
			throws ServerConfigurationException;

	public BatchJob getTransitivityJob(long jobId);

}
