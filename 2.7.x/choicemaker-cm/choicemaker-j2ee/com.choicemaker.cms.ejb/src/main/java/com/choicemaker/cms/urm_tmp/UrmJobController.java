package com.choicemaker.cms.urm_tmp;

import java.util.List;

import javax.ejb.Local;

import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.BatchJobController;

@Local
public interface UrmJobController extends BatchJobController {

	BatchJob createPersistentUrmJob(String externalID);

	List<BatchJob> findAllLinkedByUrmId(long oabaJobId);

	BatchJob findUrmJob(long id);

	List<BatchJob> findAllUrmJobs();

}