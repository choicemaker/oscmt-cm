package com.choicemaker.cms.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.webapp.model.NamedConfigurationBean;
import com.choicemaker.cms.webapp.model.UrmBatchModel;
import com.choicemaker.cms.webapp.view.BatchMatch;

@Path("urmjobs")
public class UrmJobsResource {

	private static final Logger logger =
		Logger.getLogger(UrmJobsResource.class.getName());

	@EJB
	private NamedConfigurationController ncController;

	@EJB
	private UrmBatchController ubc;

	@EJB
	private BatchMatching bma;

	private Random random = new Random();

	Long createRandomId() {
		Long retVal = Math.abs(random.nextLong());
		return retVal;
	}

	@GET
	@Produces("application/json")
	public List<UrmBatchModel> getUrmJobs() {
		List<UrmBatchModel> retVal = new ArrayList<>();
		List<BatchJob> urmJobs = ubc.findAllUrmJobs();
		for (BatchJob urmJob : urmJobs) {
			UrmBatchModel ubm = new UrmBatchModel(urmJob);
			retVal.add(ubm);
		}
		return retVal;
	}

	@POST
	@Path("matchAnalysis/{configId}")
	// public Response startMatchAnalyze(@Context UriInfo info) {
	// public Long startMatchAnalyze(@PathParam("id") long configId) {
	public Response startMatchAnalyze(@Context UriInfo info,
			@PathParam("configId") long configId) {

		NamedConfiguration nc = ncController.findNamedConfiguration(configId);
		NamedConfigurationBean ncb = new NamedConfigurationBean(nc);

		boolean doTransitivity = false;
		String extId = new Date().toString();

		Long jobId = null;
		try {
			jobId = BatchMatch.startJob(bma, ncb, extId, doTransitivity);
		} catch (NamingException | ServerConfigurationException
				| URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct the URI for the newly created resource and put in into the
		// Location header of the response (assumes that there is only one
		// occurrence of matchAnalysis in the request)
		String requestPath = info.getAbsolutePath().getRawPath();
		logger.info("UrmJobsResource.startMatchAnalyze requestPath: "
				+ requestPath);

		String responsePath = requestPath.replace(
				"urmjobs/matchAnalysis/" + configId, "urmjob/" + jobId);
		logger.info("UrmJobsResource.startMatchAnalyze responsePath: "
				+ responsePath);

		UriBuilder uriBuilder =
			info.getAbsolutePathBuilder().replacePath(responsePath);
		URI uri = uriBuilder.build();
		logger.info("UrmJobsResource.startMatchAnalyze uri: " + uri);

		Response retVal = Response.created(uri).build();
		logger.info("UrmJobsResource.startMatchAnalyze response: " + retVal);

		return retVal;
		// return jobId;
	}

}
