package com.choicemaker.cm.batch.api;

/**
 * Provides information about a a batch job.
 * 
 * @author rphall
 */
// @Local
public interface BatchJobMonitor {

	BatchJobInfo getBatchJobInfo(BatchJob batchJob);

}
