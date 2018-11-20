package com.choicemaker.cm.oaba.ejb;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.ejb.BatchJobInfoBean;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.oaba.ejb.util.OabaUtils;
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
		super(batchJob);
		Precondition.assertBoolean("Must be an OabaJob",
				OabaUtils.isOabaJob(batchJob));
		this.urmJobid = batchJob.getUrmId();
		this.oabaParameters = oabaParameters;
		this.oabaSettings = oabaSettings;
		this.serverConfiguration = serverConfiguration;
		this.workingDirectory = serverConfiguration == null ? null
				: serverConfiguration.getWorkingDirectoryLocationUriString();
		this.matchPairInfo = matchPairInfo;
	}

	public OabaJobInfoBean(long jobId, String externalId, String description,
			BatchJobStatus jobStatus, long urmJobId,
			OabaParameters oabaParameters, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration, String workingDirectory,
			MatchPairInfo matchPairInfo) {
		super(jobId, externalId, description, jobStatus);
		this.urmJobid = urmJobId;
		this.oabaParameters = oabaParameters;
		this.oabaSettings = oabaSettings;
		this.serverConfiguration = serverConfiguration;
		this.workingDirectory = workingDirectory;
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
