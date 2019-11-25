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