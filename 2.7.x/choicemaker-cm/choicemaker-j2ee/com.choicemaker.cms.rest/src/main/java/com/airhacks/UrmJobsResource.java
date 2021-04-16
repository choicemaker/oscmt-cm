package com.airhacks;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cms.api.UrmBatchController;

/**
 * 
 *
 * @author rphall
 */
@Path("urmjobs")
public class UrmJobsResource {

	// @EJB
	// private UrmBatchController ubc;

	@GET
	@Produces("application/json") 
	public List<Long> getUrmJobsIds() {
		List<Long> retVal = new ArrayList<>();
		// List<BatchJob> urmJobs = ubc.findAllUrmJobs();
		// for (BatchJob urmJob : urmJobs) {
			// retVal.add(urmJob.getId());
		// }
		retVal.add(1L);
		retVal.add(4L);
		return retVal;
	}

}

