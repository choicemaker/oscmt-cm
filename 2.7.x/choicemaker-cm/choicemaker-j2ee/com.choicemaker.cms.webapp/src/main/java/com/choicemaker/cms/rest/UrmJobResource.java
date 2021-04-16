package com.choicemaker.cms.rest;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.webapp.model.UrmBatchModel;

@Path("urmjob/{id}")
public class UrmJobResource {

	 @EJB
	 private UrmBatchController ubc;

	@GET
	@Produces("application/json") 
	public UrmBatchModel getUrmJob(@PathParam("id") long id) {
		BatchJob urmJob = ubc.findUrmJob(id);
		UrmBatchModel retVal = new UrmBatchModel(urmJob);
		return retVal;
	}

}

