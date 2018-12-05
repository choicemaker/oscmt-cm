package com.choicemaker.cm.batch.ejb;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobInfo;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.util.Precondition;

public class BatchJobInfoBean implements BatchJobInfo {
	
	private final long jobId;
	private final String externalId;
	private final String batchJobType;
	private final String description;
	private final BatchJobStatus jobStatus;

	public BatchJobInfoBean(BatchJob batchJob) {
		this(batchJob.getId(),batchJob.getExternalId(),batchJob.getBatchJobType(),
				batchJob.getDescription(), batchJob.getStatus());
	}

	public BatchJobInfoBean(long jobId, String externalId, String batchJobType, String description,
			BatchJobStatus jobStatus) {
		Precondition.assertNonNullArgument("null job status", jobStatus);
		this.jobId = jobId;
		this.externalId = externalId;
		this.batchJobType = batchJobType;
		this.description = description;
		this.jobStatus = jobStatus;
	}

	@Override
	public long getJobId() {
		return jobId;
	}

	@Override
	public String getExternalId() {
		return externalId;
	}

	@Override
	public String getType() {
		return batchJobType;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public BatchJobStatus getJobStatus() {
		return jobStatus;
	}

}
