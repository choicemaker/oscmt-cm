package com.choicemaker.cm.batch.api;

public interface ProcessController {
	void abortBatchJob(BatchJob batchJob);
	void restartBatchJob(BatchJob batchJob);
}
