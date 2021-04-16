package com.choicemaker.cm.batch.api;

/**
 * Aborts and restarts batch jobs
 */
public interface ProcessController {

	void abortBatchJob(BatchJob batchJob);

	void restartBatchJob(BatchJob batchJob);
}
