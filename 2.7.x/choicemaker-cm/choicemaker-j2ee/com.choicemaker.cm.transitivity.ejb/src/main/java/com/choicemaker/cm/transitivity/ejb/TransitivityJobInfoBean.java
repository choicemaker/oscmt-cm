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
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobEntity.getBatchJobType;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.ejb.BatchJobInfoBean;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.transitivity.api.TransitiveGroupInfo;
import com.choicemaker.cm.transitivity.api.TransitivityJobInfo;
import com.choicemaker.cm.transitivity.ejb.util.TransitivityUtils;
import com.choicemaker.util.Precondition;

public class TransitivityJobInfoBean extends BatchJobInfoBean
		implements TransitivityJobInfo {

	private final long urmJobId;
	private final OabaJobInfo oabaJobInfo;
	private final TransitivityParameters transitityParameters;
	private final OabaSettings oabaSettings;
	private final ServerConfiguration serverConfiguration;
	private final String workingDirectory;
	private final MatchPairInfo matchPairInfo;
	private final TransitiveGroupInfo transitiveGroupInfo;

	public TransitivityJobInfoBean(BatchJob batchJob, OabaJobInfo oabaJobInfo,
			TransitivityParameters transitityParameters,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			MatchPairInfo matchPairInfo,
			TransitiveGroupInfo transitiveGroupInfo) {
		this(batchJob.getId(), batchJob.getExternalId(),
				batchJob.getDescription(), batchJob.getStatus(),
				batchJob.getUrmId(), oabaJobInfo, transitityParameters,
				oabaSettings, serverConfiguration, matchPairInfo,
				transitiveGroupInfo);
		Precondition.assertBoolean("Must be an TransitivityJob",
				TransitivityUtils.isTransitivityJob(batchJob));
	}

	public TransitivityJobInfoBean(long jobId, String externalId,
			String description, BatchJobStatus jobStatus, long urmJobId,
			OabaJobInfo oabaJobInfo,
			TransitivityParameters transitityParameters,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			MatchPairInfo matchPairInfo,
			TransitiveGroupInfo transitiveGroupInfo) {
		super(jobId, externalId, getBatchJobType(TransitivityJobEntity.class),
				description, jobStatus);
		this.urmJobId = urmJobId;
		this.oabaJobInfo = oabaJobInfo;
		this.transitityParameters = transitityParameters;
		this.oabaSettings = oabaSettings;
		this.serverConfiguration = serverConfiguration;
		this.workingDirectory = serverConfiguration == null ? null
				: serverConfiguration.getWorkingDirectoryLocationUriString();
		this.matchPairInfo = matchPairInfo;
		this.transitiveGroupInfo = transitiveGroupInfo;
	}

	@Override
	public long getUrmJobId() {
		return urmJobId;
	}

	@Override
	public OabaJobInfo getOabaJobInfo() {
		return oabaJobInfo;
	}

	@Override
	public TransitivityParameters getTransitityParameters() {
		return transitityParameters;
	}

	@Override
	public OabaSettings getOabaSettings() {
		return oabaSettings;
	}

	@Override
	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	@Override
	public String getWorkingDirectory() {
		return workingDirectory;
	}

	@Override
	public MatchPairInfo getMatchPairInfo() {
		return matchPairInfo;
	}

	@Override
	public TransitiveGroupInfo getTransitiveGroupInfo() {
		return transitiveGroupInfo;
	}

}
