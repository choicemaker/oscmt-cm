package com.choicemaker.cms.api;

import java.util.List;

import javax.ejb.Local;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobController;

@Local
public interface UrmJobController extends BatchJobController {

	BatchJob createPersistentUrmJob(String externalID);

	List<BatchJob> findAllLinkedByUrmId(long oabaJobId);

	BatchJob findUrmJob(long id);

	List<BatchJob> findAllUrmJobs();

}
