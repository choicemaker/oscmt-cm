package com.choicemaker.cm.oaba.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class OabaProcessControllerBean implements ProcessController {

	@EJB
	OabaJobManager oabaJobManager;

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertBoolean("not an OABA Job entity",
				batchJob instanceof OabaJobEntity);
		batchJob.markAsAbortRequested();
		batchJob.markAsAborted();
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		throw new Error("not yet implemented");
	}

}
