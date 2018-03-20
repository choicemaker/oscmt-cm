package com.choicemaker.cms.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.oaba.ejb.OabaJobEntity;
import com.choicemaker.cm.oaba.ejb.OabaProcessControllerBean;
import com.choicemaker.cm.transitivity.ejb.TransitivityJobEntity;
import com.choicemaker.cm.transitivity.ejb.TransitivityProcessControllerBean;
import com.choicemaker.cms.api.UrmJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class UrmProcessControllerBean implements ProcessController {

	private static final Logger logger =
		Logger.getLogger(UrmProcessControllerBean.class.getName());

	@EJB
	UrmJobManager urmJobManager;

	@EJB(lookup = "java:app/com.choicemaker.cm.oaba.ejb/OabaProcessControllerBean!com.choicemaker.cm.batch.api.ProcessController")
	private OabaProcessControllerBean oabaProcessController;

	@EJB(lookup = "java:app/com.choicemaker.cm.transitivity.ejb/TransitivityProcessControllerBean!com.choicemaker.cm.batch.api.ProcessController")
	private TransitivityProcessControllerBean transitivityProcessController;

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertBoolean("not an OABA Job entity",
				batchJob instanceof UrmJobEntity);

		long urmId = batchJob.getId();
		logger.finer("UrmAbort: urm job id: " + urmId);
		List<Long> unexpectedJobs = new ArrayList<>();
		List<BatchJob> delegates = urmJobManager.findAllLinkedByUrmId(urmId);
		for (BatchJob delegate : delegates) {
			assert delegate != null;
			logger.finer("UrmAbort: delegate job id: " + delegate.getId() + "("
					+ delegate.getClass() + ")");
			if (delegate instanceof OabaJobEntity) {
				oabaProcessController.abortBatchJob(delegate);
			} else if (delegate instanceof TransitivityJobEntity) {
				transitivityProcessController.abortBatchJob(delegate);
			} else {
				unexpectedJobs.add(delegate.getId());
				String msg = "UrmAbort: unexpected delegate type: "
						+ delegate.getClass();
				logger.warning(msg);
				// Keep processing...
			}
		}

		if (unexpectedJobs.size() > 0) {
			String msg = "Unable to abort jobs: " + unexpectedJobs.toString();
			logger.severe(msg);
			throw new Error(msg);
		}
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		throw new Error("not yet implemented");
	}

}
