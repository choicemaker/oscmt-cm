/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.api;

import javax.ejb.Local;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;

/**
 * This session bean allows the user to start, query, and get result from the
 * Transitivity Engine. It is to be used with the Offline Automated Blocking
 * Algorithm (OABA).
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
	 * 
	 * See
	 * {@link #startTransitivity(String, TransitivityParameters, BatchJob, OabaSettings, ServerConfiguration, BatchJob, RecordMatchingMode)
	 * below} for explanation of the parameters, return value, and thrown
	 * exception.
	 * 
	 */
	long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob oabaJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException;

	/**
	 * This method starts transitivity analysis of the specified OABA job, but
	 * allows a different record-matching mode to be specified.
	 * 
	 * @param externalID
	 *            an optional String used to tag a batch job for external
	 *            tracking.
	 * @param batchParams
	 *            a required reference to commonly adjusted parameters that
	 *            control transitive analysis.
	 * @param oabaJob
	 *            a required reference to the OABA job that will be analyzed for
	 *            transitive groupings.
	 * @param settings
	 *            a required reference to less commonly adjusted parameters that
	 *            control transitive analysis.
	 * @param serverConfiguration
	 *            a required reference to host-specific parameters that control
	 *            how transitive analysis is performed
	 * @param urmJob
	 *            a required reference to the URM job that initiated transitive
	 *            analysis.
	 * @return a identifier use to track the transitive analysis job
	 * @throws ServerConfigurationException
	 */
	long startTransitivity(String externalID,
			TransitivityParameters batchParams, BatchJob oabaJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob, RecordMatchingMode mode)
			throws ServerConfigurationException;

	/**
	 * Returns a reference to the transitive analysis job identified by the
	 * specified id. The return value is null if the identifier does not
	 * correspond to a known job.
	 */
	public BatchJob getTransitivityJob(long jobId);

}
