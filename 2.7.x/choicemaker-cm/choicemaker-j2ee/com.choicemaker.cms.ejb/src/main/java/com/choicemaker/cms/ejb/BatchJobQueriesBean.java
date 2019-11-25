package com.choicemaker.cms.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cms.api.BatchJobQueries;
import com.choicemaker.cms.api.UrmJobManager;
import com.choicemaker.cms.api.remote.BatchMatchingRemote;

@Stateless
@Local(BatchJobQueries.class)
@Remote(BatchMatchingRemote.class)
public class BatchJobQueriesBean implements BatchJobQueries {

	// private static final String SOURCE_CLASS =
	// BatchJobQueriesBean.class.getSimpleName();
	//
	// private static final Logger logger =
	// Logger.getLogger(BatchJobQueriesBean.class.getName());

	@EJB
	private UrmJobManager urmJobManager;

	@EJB
	private TransitivityJobManager transitivityJobManager;

	@EJB
	private OabaJobManager oabaJobManager;

	@EJB
	private BatchJobManager batchJobManager;

	// Top-level URM jobs

	@Override
	public List<BatchJob> findAllUrmJobs() {
		return urmJobManager.findAllUrmJobs();
	}

	@Override
	public BatchJob findUrmJob(long id) {
		return urmJobManager.findUrmJob(id);
	}

	@Override
	public List<BatchJob> findAllBatchJobsLinkedByUrmId(long urmJobId) {
		return urmJobManager.findAllLinkedByUrmId(urmJobId);
	}

	// Transitivity jobs

	@Override
	public List<BatchJob> findAllTransitivityJobs() {
		return transitivityJobManager.findAllTransitivityJobs();
	}

	@Override
	public BatchJob findTransitivityJob(long id) {
		return transitivityJobManager.findTransitivityJob(id);
	}

	@Override
	public List<BatchJob> findAllTransitivityJobsByOabaJobId(long oabaJobId) {
		return transitivityJobManager.findAllByOabaJobId(oabaJobId);
	}

	// OABA jobs

	@Override
	public List<BatchJob> findAllOabaJobs() {
		return oabaJobManager.findAllOabaJobs();
	}

	@Override
	public BatchJob findOabaJob(long id) {
		return oabaJobManager.findOabaJob(id);
	}

	// General batch jobs (URM, Transitivity, OABA, etc)

	@Override
	public List<BatchJob> findAll() {
		return batchJobManager.findAll();
	}

	@Override
	public BatchJob findBatchJob(long id) {
		return batchJobManager.findBatchJob(id);
	}

}
