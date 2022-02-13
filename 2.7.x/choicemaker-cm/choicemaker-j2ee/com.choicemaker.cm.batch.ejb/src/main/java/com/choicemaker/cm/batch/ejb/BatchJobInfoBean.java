/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
