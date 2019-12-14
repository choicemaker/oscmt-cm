package com.choicemaker.cm.oaba.ejb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.OabaBatchController;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;

@Stateless
public class OabaBatchControllerBean implements OabaBatchController {

	@EJB
	OabaJobManager oabaJobManager;

	@EJB(lookup = "java:app/com.choicemaker.cm.oaba.ejb/OabaProcessControllerBean!com.choicemaker.cm.batch.api.ProcessController")
	OabaProcessControllerBean oabaProcessController;

	@EJB(lookup = "java:app/com.choicemaker.cm.oaba.ejb/OabaResultsManagerBean!com.choicemaker.cm.batch.api.BatchResultsManager")
	OabaResultsManagerBean oabaResultsManager;

	@Override
	public void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException {
		oabaResultsManager.exportResults(batchJob, container);
	}

	@Override
	public BatchJob createPersistentOabaJob(String externalID,
			OabaParameters params, OabaSettings settings,
			ServerConfiguration sc) throws ServerConfigurationException {
		return oabaJobManager.createPersistentOabaJob(externalID, params,
				settings, sc);
	}

	@Override
	public BatchJob createPersistentOabaJob(String externalID,
			OabaParameters batchParams, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration, BatchJob urmJob)
			throws ServerConfigurationException {
		return oabaJobManager.createPersistentOabaJob(externalID, batchParams,
				oabaSettings, serverConfiguration, urmJob);
	}

	@Override
	public BatchJob findOabaJob(long id) {
		return oabaJobManager.findBatchJob(id);
	}

	@Override
	public void delete(BatchJob batchJob) {
		oabaJobManager.delete(batchJob);
	}

	@Override
	public void detach(BatchJob oabaJob) {
		oabaJobManager.detach(oabaJob);
	}

	@Override
	public BatchJob findBatchJob(long id) {
		return oabaJobManager.findBatchJob(id);
	}

	@Override
	public List<BatchJob> findAll() {
		return oabaJobManager.findAll();
	}

	@Override
	public BatchJob save(BatchJob batchJob) {
		return oabaJobManager.save(batchJob);
	}

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		oabaProcessController.abortBatchJob(batchJob);
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		oabaProcessController.restartBatchJob(batchJob);
	}

	@Override
	public List<BatchJob> findAllOabaJobs() {
		return oabaJobManager.findAllOabaJobs();
	}

}
