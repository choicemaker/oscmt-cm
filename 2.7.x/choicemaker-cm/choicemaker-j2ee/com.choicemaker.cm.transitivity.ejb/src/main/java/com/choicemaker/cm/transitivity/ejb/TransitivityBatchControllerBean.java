package com.choicemaker.cm.transitivity.ejb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.transitivity.api.TransitivityBatchController;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;

@Stateless
public class TransitivityBatchControllerBean
		implements TransitivityBatchController {

	@EJB
	TransitivityJobManager transitivityJobManager;

	@EJB(lookup = "java:app/com.choicemaker.cm.transitivity.ejb/TransitivityProcessControllerBean!com.choicemaker.cm.batch.api.ProcessController")
	TransitivityProcessControllerBean transitivityProcessController;

	@EJB(lookup = "java:app/com.choicemaker.cm.transitivity.ejb/TransitivityResultsManagerBean!com.choicemaker.cm.batch.api.BatchResultsManager")
	TransitivityResultsManagerBean transitivityResultsManager;

	@Override
	public void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException {
		transitivityResultsManager.exportResults(batchJob, container);
	}

	@Override
	public BatchJob createPersistentTransitivityJob(String externalID,
			TransitivityParameters params, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration sc)
			throws ServerConfigurationException {
		return transitivityJobManager.createPersistentTransitivityJob(
				externalID, params, batchJob, settings, sc);
	}

	@Override
	public BatchJob createPersistentTransitivityJob(String externalID,
			TransitivityParameters batchParams, BatchJob batchJob,
			OabaSettings settings, ServerConfiguration serverConfiguration,
			BatchJob urmJob) throws ServerConfigurationException {
		return transitivityJobManager.createPersistentTransitivityJob(
				externalID, batchParams, batchJob, settings,
				serverConfiguration, urmJob);
	}

	@Override
	public List<BatchJob> findAllByOabaJobId(long oabaJobId) {
		return transitivityJobManager.findAllByOabaJobId(oabaJobId);
	}

	@Override
	public List<BatchJob> findAllTransitivityJobs() {
		return transitivityJobManager.findAllTransitivityJobs();
	}

	@Override
	public BatchJob findTransitivityJob(long id) {
		return transitivityJobManager.findTransitivityJob(id);
	}

	@Override
	public void delete(BatchJob batchJob) {
		transitivityJobManager.delete(batchJob);
	}

	@Override
	public void detach(BatchJob transitivityJob) {
		transitivityJobManager.detach(transitivityJob);
	}

	@Override
	public BatchJob findBatchJob(long id) {
		return transitivityJobManager.findBatchJob(id);
	}

	@Override
	public List<BatchJob> findAll() {
		return transitivityJobManager.findAll();
	}

	@Override
	public BatchJob save(BatchJob batchJob) {
		return transitivityJobManager.save(batchJob);
	}

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		transitivityProcessController.abortBatchJob(batchJob);
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		transitivityProcessController.restartBatchJob(batchJob);
	}

}
