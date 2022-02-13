/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BatchJob;

public interface BatchJobQueries {

	// Top-level URM jobs

	/** Find all top-level URM jobs */
	List<BatchJob> findAllUrmJobs();

	/** Find a top-level URM job using the specified job id */
	BatchJob findUrmJob(long id);

	/**
	 * Find all batch jobs (OABA and transitivity) that are sub-jobs of the
	 * specified URM job
	 */
	List<BatchJob> findAllBatchJobsLinkedByUrmId(long urmJobId);

	// Transitivity analysis jobs
	
	/** Find all transitivity jobs */
	List<BatchJob> findAllTransitivityJobs();

	/** Find a transitivity job using the specified job id */
	BatchJob findTransitivityJob(long id);

	/** Find all transitivity jobs that analyze a given OABA job */
	List<BatchJob> findAllTransitivityJobsByOabaJobId(long oabaJobId);

	// -- Offline Automated Blocking Algorithm (OABA) jobs

	/** Find all transitivity jobs */
	List<BatchJob> findAllOabaJobs();

	/** Find an OABA batch job using the specified job id */
	BatchJob findOabaJob(long id);

	// -- Any batch job (URM, OABA, transitivity, etc)
	
	/**
	 * Find a batch job (URM, OABA, transitivity, etc) using the specified job id
	 */
	BatchJob findBatchJob(long id);

	/** Find all batch jobs (URM, OABA, transitivity, etc) */
	List<BatchJob> findAll();

}