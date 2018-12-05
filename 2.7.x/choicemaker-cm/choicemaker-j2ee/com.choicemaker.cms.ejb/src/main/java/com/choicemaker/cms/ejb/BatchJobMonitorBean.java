package com.choicemaker.cm.batch.ejb;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobInfo;
import com.choicemaker.cm.batch.api.BatchJobMonitor;
import com.choicemaker.cm.batch.api.BatchJobStatus;

@Stateless
public class BatchJobMonitorBean implements BatchJobMonitor {

	private static final Logger logger =
		Logger.getLogger(BatchJobMonitorBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Override
	public BatchJobInfo getBatchJobInfo(BatchJob batchJob) {
		BatchJobInfo retVal = null;
		if (batchJob != null) {
			long jobId = batchJob.getId();
			String externalId = batchJob.getExternalId();
			String description = batchJob.getDescription();
			BatchJobStatus jobStatus = batchJob.getStatus();
			retVal = new BatchJobInfoBean(jobId, externalId, description,
					jobStatus);
		}
		return retVal;
	}

}
