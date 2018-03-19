package com.choicemaker.cm.transitivity.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class TransitivityProcessControllerBean implements ProcessController {

	@EJB
	TransitivityJobManager transitivityJobManager;

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertBoolean("not an OABA Job entity",
				batchJob instanceof UrmJobEntity);
		batchJob.markAsAbortRequested();
		batchJob.markAsAborted();
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		throw new Error("not yet implemented");
	}

}
