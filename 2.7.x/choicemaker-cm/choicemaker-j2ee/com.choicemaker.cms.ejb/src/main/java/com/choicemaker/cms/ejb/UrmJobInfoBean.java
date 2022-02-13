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
package com.choicemaker.cms.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobEntity.getBatchJobType;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.ejb.BatchJobInfoBean;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.transitivity.api.TransitivityJobInfo;
import com.choicemaker.cms.api.UrmJobInfo;
import com.choicemaker.util.Precondition;

public class UrmJobInfoBean extends BatchJobInfoBean implements UrmJobInfo {

	private final OabaJobInfo oabaJobInfo;
	private final TransitivityJobInfo transitivityJobInfo;

	public UrmJobInfoBean(BatchJob batchJob, OabaJobInfo oabaJobInfo,
			TransitivityJobInfo transitivityJobInfo) {
		this(batchJob.getId(), batchJob.getExternalId(),
				batchJob.getDescription(), batchJob.getStatus(), oabaJobInfo,
				transitivityJobInfo);
		Precondition.assertBoolean("Must be an OabaJob",
				UrmUtils.isUrmJob(batchJob));
	}

	public UrmJobInfoBean(long jobId, String externalId, String description,
			BatchJobStatus jobStatus, OabaJobInfo oabaJobInfo,
			TransitivityJobInfo transitivityJobInfo) {
		super(jobId, externalId, getBatchJobType(UrmJobEntity.class),
				description, jobStatus);
		this.oabaJobInfo = oabaJobInfo;
		this.transitivityJobInfo = transitivityJobInfo;
	}

	@Override
	public OabaJobInfo getOabaJobInfo() {
		return oabaJobInfo;
	}

	@Override
	public TransitivityJobInfo getTransitivityJobInfo() {
		return transitivityJobInfo;
	}

}
