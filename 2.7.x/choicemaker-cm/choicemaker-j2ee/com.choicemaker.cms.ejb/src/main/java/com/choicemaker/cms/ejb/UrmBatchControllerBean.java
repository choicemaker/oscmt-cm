package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.api.UrmJobManager;
import com.choicemaker.cms.api.remote.UrmBatchControllerRemote;

@Stateless
@Local(UrmBatchController.class)
@Remote(UrmBatchControllerRemote.class)
public class UrmBatchControllerBean implements UrmBatchController {

	@EJB
	UrmJobManager urmJobManager;

	// @EJB(lookup =
	// "java:app/com.choicemaker.cms.ejb/UrmProcessControllerBean!com.choicemaker.cm.batch.api.ProcessController")
	// @EJB(lookup = "java:app/com.choicemaker.cms.ejb/UrmProcessControllerBean")
	@EJB (beanName = "UrmProcessControllerBean")
	ProcessController urmProcessController;

	// @EJB(lookup =
	// "java:app/com.choicemaker.cms.ejb/UrmResultsManagerBean!com.choicemaker.cm.batch.api.BatchResultsManager")
	// @EJB(lookup = "java:app/com.choicemaker.cms.ejb/UrmResultsManagerBean")
	@EJB (beanName = "UrmResultsManagerBean")
	BatchResultsManager urmResultsManager;

	@Override
	public void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException {
		urmResultsManager.exportResults(batchJob, container);
	}

	@Override
	public BatchJob createPersistentUrmJob(String externalID) {
		return urmJobManager.createPersistentUrmJob(externalID);
	}

	@Override
	public List<BatchJob> findAllLinkedByUrmId(long urmId) {
		return urmJobManager.findAllLinkedByUrmId(urmId);
	}

	@Override
	public BatchJob findUrmJob(long id) {
		return urmJobManager.findUrmJob(id);
	}

	@Override
	public List<BatchJob> findAllUrmJobs() {
		return urmJobManager.findAllUrmJobs();
	}

	@Override
	public void delete(BatchJob batchJob) {
		urmJobManager.delete(batchJob);
	}

	@Override
	public void detach(BatchJob transitivityJob) {
		urmJobManager.detach(transitivityJob);
	}

	@Override
	public BatchJob findBatchJob(long id) {
		return urmJobManager.findBatchJob(id);
	}

	@Override
	public List<BatchJob> findAll() {
		return urmJobManager.findAll();
	}

	@Override
	public BatchJob save(BatchJob batchJob) {
		return urmJobManager.save(batchJob);
	}

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		urmProcessController.abortBatchJob(batchJob);
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		urmProcessController.restartBatchJob(batchJob);
	}

}
