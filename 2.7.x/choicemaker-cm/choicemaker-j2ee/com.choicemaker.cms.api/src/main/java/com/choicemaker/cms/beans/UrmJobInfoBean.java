package com.choicemaker.cms.beans;

import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cms.api.OabaJobInfo;
import com.choicemaker.cms.api.TransitivityJobInfo;
import com.choicemaker.cms.api.UrmJobInfo;

public class UrmJobInfoBean extends BatchJobInfoBean implements UrmJobInfo {

	private final OabaJobInfo oabaJobInfo;
	private final TransitivityJobInfo transitivityJobInfo;
	
	public UrmJobInfoBean(long jobId, String externalId, String description,
			BatchJobStatus jobStatus, OabaJobInfo oabaJobInfo,
			TransitivityJobInfo transitivityJobInfo) {
		super(jobId, externalId, description, jobStatus);
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
