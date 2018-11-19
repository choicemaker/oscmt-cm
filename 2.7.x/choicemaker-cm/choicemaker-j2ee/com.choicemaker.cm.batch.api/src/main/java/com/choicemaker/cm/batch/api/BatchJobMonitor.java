package com.choicemaker.cm.batch.api;

/**
 * Provides information about a a batch job.
 * 
 * @author rphall
 */
// @Local
public interface BatchJobMonitor {

	BatchJobInfo getBatchJobInf(BatchJob batchJob);

	BatchJobInfo getBatchJobInf(long jobId);

}
