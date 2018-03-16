package com.choicemaker.cm.batch.api;

import java.util.List;

public interface BatchJobManager {

	void delete(BatchJob batchJob);

	void detach(BatchJob oabaJob);

	BatchJob findBatchJob(long id);

	List<BatchJob> findAll();

	BatchJob save(BatchJob batchJob);

}