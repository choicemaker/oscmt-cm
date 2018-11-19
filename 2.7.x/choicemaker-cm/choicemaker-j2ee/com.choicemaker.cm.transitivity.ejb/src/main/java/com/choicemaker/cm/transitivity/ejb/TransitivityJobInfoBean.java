package com.choicemaker.cm.transitivity.ejb;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.ejb.BatchJobInfoBean;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.transitivity.api.TransitiveGroupInfo;
import com.choicemaker.cm.transitivity.api.TransitivityJobInfo;

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

	public TransitivityJobInfoBean(long jobId, String externalId,
			String description, BatchJobStatus jobStatus, long urmJobId,
			OabaJobInfo oabaJobInfo,
			TransitivityParameters transitityParameters,
			OabaSettings oabaSettings, ServerConfiguration serverConfiguration,
			String workingDirectory, MatchPairInfo matchPairInfo,
			TransitiveGroupInfo transitiveGroupInfo) {
		super(jobId, externalId, description, jobStatus);
		this.urmJobId = urmJobId;
		this.oabaJobInfo = oabaJobInfo;
		this.transitityParameters = transitityParameters;
		this.oabaSettings = oabaSettings;
		this.serverConfiguration = serverConfiguration;
		this.workingDirectory = workingDirectory;
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
