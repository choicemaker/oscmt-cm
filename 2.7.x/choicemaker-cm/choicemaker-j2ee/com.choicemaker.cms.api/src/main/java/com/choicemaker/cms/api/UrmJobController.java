package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobController;

public interface UrmJobController extends BatchJobController {

	BatchJob createPersistentUrmJob(String externalID);

	List<BatchJob> findAllLinkedByUrmId(long oabaJobId);

	BatchJob findUrmJob(long id);

	List<BatchJob> findAllUrmJobs();

}
