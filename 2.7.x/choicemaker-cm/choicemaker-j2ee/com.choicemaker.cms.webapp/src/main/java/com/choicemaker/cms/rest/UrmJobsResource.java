/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.ExtensibleConfiguration;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.webapp.model.UrmBatchModel;
import com.choicemaker.cms.webapp.util.RestUtils;
import com.choicemaker.cms.webapp.view.BatchMatch;
import com.choicemaker.util.TypedValue;

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
	@Path("match/{configId}")
	public Response startMatch(@Context UriInfo info,
			@PathParam("configId") long configId) {

		logger.info("configId: " + configId);
		final MultivaluedMap<String, String> queryParams = info.getQueryParameters();
		final String METHOD = "match";
		RestUtils.logMultivaluedStringMap(logger, Level.INFO, METHOD, queryParams);

		// FIXME STUBBED IMPLEMENTATION
		Long jobId = 0L;
		
		// Construct the URI for the newly created resource and put in into the
		// Location header of the response (assumes that there is only one
		// occurrence of matchAnalysis in the request)
		String requestPath = info.getAbsolutePath().getRawPath();
		logger.info("UrmJobsResource.startMatch requestPath: "
				+ requestPath);

		String responsePath = requestPath.replaceAll(
				"urmjobs/match/.*$", "urmjob/" + jobId);
		logger.info("UrmJobsResource.startMatch responsePath: "
				+ responsePath);

		UriBuilder uriBuilder =
			info.getAbsolutePathBuilder().replacePath(responsePath);
		URI uri = uriBuilder.build();
		logger.info("UrmJobsResource.startMatch uri: " + uri);

		Response retVal = Response.created(uri).build();
		logger.info("UrmJobsResource.startMatch response: " + retVal);

		return retVal;
	}

	@POST
	@Path("analysis/{jobId}")
	public Response startAnalyze(@Context UriInfo info,
			@PathParam("jobId") long jobId) {

		logger.info("jobId: " + jobId);
		final MultivaluedMap<String, String> queryParams = info.getQueryParameters();
		final String METHOD = "analysis";
		RestUtils.logMultivaluedStringMap(logger, Level.INFO, METHOD, queryParams);

		// FIXME STUBBED IMPLEMENTATION
		Long analysisJobId = 0L;
		
		// Construct the URI for the newly created resource and put in into the
		// Location header of the response (assumes that there is only one
		// occurrence of matchAnalysis in the request)
		String requestPath = info.getAbsolutePath().getRawPath();
		logger.info("UrmJobsResource.startAnalyze requestPath: "
				+ requestPath);

		String responsePath = requestPath.replaceAll(
				"urmjobs/analysis/.*$", "urmjob/" + analysisJobId);
		logger.info("UrmJobsResource.startAnalyze responsePath: "
				+ responsePath);

		UriBuilder uriBuilder =
			info.getAbsolutePathBuilder().replacePath(responsePath);
		URI uri = uriBuilder.build();
		logger.info("UrmJobsResource.startAnalyze uri: " + uri);

		Response retVal = Response.created(uri).build();
		logger.info("UrmJobsResource.startAnalyze response: " + retVal);

		return retVal;
	}

	@POST
	@Path("matchAnalysis/{configId}")
	public Response startMatchAnalyze(@Context UriInfo info,
			@PathParam("configId") long configId) {

		logger.info("configId: " + configId);
		final MultivaluedMap<String, String> queryParams = info.getQueryParameters();
		final String METHOD = "matchAnalysis";
		RestUtils.logMultivaluedStringMap(logger, Level.INFO, METHOD, queryParams);

		NamedConfiguration nc = ncController.findNamedConfiguration(configId);
		ExtensibleConfiguration ec = new ExtensibleConfiguration(nc);

		boolean doTransitivity = true;
		if (!queryParams.isEmpty()) {
			String value = queryParams.getFirst("transitivity");
			if (value != null) {
				doTransitivity = Boolean.parseBoolean(value);
			}
			for (String key : queryParams.keySet()) {
				value = queryParams.getFirst(key);
				TypedValue<?> tv;
				if (ExtensibleConfiguration.isWellknownAttributeName(key)) {
					tv = ExtensibleConfiguration.createTypedValue(key, value);
				} else {
					tv = new TypedValue<String>(String.class,value);
				}
				ec.setTypedAttribute(key, tv);
			}
		}
		String extId = "REST: " + new Date().toString();

		Long jobId = null;
		try {
			jobId = BatchMatch.startJob(bma, ec, extId, doTransitivity);
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

		String responsePath = requestPath.replaceAll(
				"urmjobs/matchAnalysis/.*$", "urmjob/" + jobId);
		logger.info("UrmJobsResource.startMatchAnalyze responsePath: "
				+ responsePath);

		UriBuilder uriBuilder =
			info.getAbsolutePathBuilder().replacePath(responsePath);
		URI uri = uriBuilder.build();
		logger.info("UrmJobsResource.startMatchAnalyze uri: " + uri);

		Response retVal = Response.created(uri).build();
		logger.info("UrmJobsResource.startMatchAnalyze response: " + retVal);

		return retVal;
	}

}
