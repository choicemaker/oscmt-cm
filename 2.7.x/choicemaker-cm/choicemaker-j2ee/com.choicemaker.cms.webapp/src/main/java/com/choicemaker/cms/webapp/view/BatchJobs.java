package com.choicemaker.cms.webapp.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cms.api.UrmBatchController;

@Named
@RequestScoped
public class BatchJobs {

	@Inject
	private UrmBatchController urmBatchController;

	public List<BatchJob> getBatchJobs() {
		List<BatchJob> retVal = urmBatchController.findAllUrmJobs();
		return retVal;
	}

}
