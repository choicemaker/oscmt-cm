/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobEntity.getBatchJobType;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.ejb.BatchJobInfoBean;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.util.Precondition;

public class OabaJobInfoBean extends BatchJobInfoBean implements OabaJobInfo {

	private final long urmJobid;
	private final OabaParameters oabaParameters;
	private final OabaSettings oabaSettings;
	private final ServerConfiguration serverConfiguration;
	private final String workingDirectory;
	private final MatchPairInfo matchPairInfo;

	public OabaJobInfoBean(BatchJob batchJob, OabaParameters oabaParameters,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			MatchPairInfo matchPairInfo) {
		this(batchJob.getId(), batchJob.getExternalId(),
				batchJob.getDescription(), batchJob.getStatus(),
				batchJob.getUrmId(), oabaParameters, oabaSettings,
				serverConfiguration, matchPairInfo);
		Precondition.assertBoolean("Must be an OabaJob",
				OabaEjbUtils.isOabaJob(batchJob));
	}

	public OabaJobInfoBean(long jobId, String externalId, String description,
			BatchJobStatus jobStatus, long urmJobId,
			OabaParameters oabaParameters, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration,
			MatchPairInfo matchPairInfo) {
		super(jobId, externalId, getBatchJobType(OabaJobEntity.class),
				description, jobStatus);
		this.urmJobid = urmJobId;
		this.oabaParameters = oabaParameters;
		this.oabaSettings = oabaSettings;
		this.serverConfiguration = serverConfiguration;
		this.workingDirectory = serverConfiguration == null ? null
				: serverConfiguration.getWorkingDirectoryLocationUriString();
		this.matchPairInfo = matchPairInfo;
	}

	@Override
	public long getUrmJobId() {
		return urmJobid;
	}

	@Override
	public OabaParameters getOabaParameters() {
		return oabaParameters;
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

}
