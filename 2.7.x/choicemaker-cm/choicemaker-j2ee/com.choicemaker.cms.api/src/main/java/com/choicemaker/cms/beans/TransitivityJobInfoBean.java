package com.choicemaker.cms.beans;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cms.api.MatchPairInfo;
import com.choicemaker.cms.api.OabaJobInfo;
import com.choicemaker.cms.api.TransitiveGroupInfo;
import com.choicemaker.cms.api.TransitivityJobInfo;
import com.choicemaker.cms.api.UrmJobInfo;

public class TransitivityJobInfoBean extends BatchJobInfoBean
		implements TransitivityJobInfo {

	private final UrmJobInfo urmJobInfo;
	private final OabaJobInfo oabaJobInfo;
	private final TransitivityParameters transitityParameters;
	private final OabaSettings oabaSettings;
	private final ServerConfiguration serverConfiguration;
	private final String workingDirectory;
	private final MatchPairInfo matchPairInfo;
	private final TransitiveGroupInfo transitiveGroupInfo;

	public TransitivityJobInfoBean(long jobId, String externalId,
			String description, BatchJobStatus jobStatus, UrmJobInfo urmJobInfo,
			OabaJobInfo oabaJobInfo,
			TransitivityParameters transitityParameters,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			String workingDirectory, MatchPairInfo matchPairInfo,
			TransitiveGroupInfo transitiveGroupInfo) {
		super(jobId, externalId, description, jobStatus);
		this.urmJobInfo = urmJobInfo;
		this.oabaJobInfo = oabaJobInfo;
		this.transitityParameters = transitityParameters;
		this.oabaSettings = oabaSettings;
		this.serverConfiguration = serverConfiguration;
		this.workingDirectory = workingDirectory;
		this.matchPairInfo = matchPairInfo;
		this.transitiveGroupInfo = transitiveGroupInfo;
	}

	@Override
	public UrmJobInfo getUrmJobInfo() {
		return urmJobInfo;
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
