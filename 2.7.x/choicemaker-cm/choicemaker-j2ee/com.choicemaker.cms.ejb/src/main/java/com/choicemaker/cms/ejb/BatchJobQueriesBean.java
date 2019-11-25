package com.choicemaker.cms.ejb;

import java.util.List;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cms.api.BatchJobQueries;

public class BatchJobQueriesBean implements BatchJobQueries {

	@Override
	public List<BatchJob> findAllTransitivityJobsByOabaJobId(long oabaJobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchJob findTransitivityJob(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BatchJob> findAllTransitivityJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BatchJob> findAllBatchJobsLinkedByUrmId(long urmJobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchJob findUrmJob(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BatchJob> findAllUrmJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchJob findOabaJob(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchJob findBatchJob(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BatchJob> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
