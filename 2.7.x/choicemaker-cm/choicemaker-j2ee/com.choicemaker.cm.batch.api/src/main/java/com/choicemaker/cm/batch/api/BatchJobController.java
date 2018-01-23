package com.choicemaker.cm.batch.api;

import java.util.List;

public interface BatchJobController {

	void delete(BatchJob batchJob);

	void detach(BatchJob oabaJob);

	BatchJob findBatchJob(long id);

	List<BatchJob> findAll();

	BatchJob save(BatchJob batchJob);

}