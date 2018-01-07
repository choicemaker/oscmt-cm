package com.choicemaker.cms.api;

import com.choicemaker.cm.args.OabaLinkageType;

public interface OnlineParameters {

	String getModelConfigurationName();

	OabaLinkageType getOabaLinkageType();

	float getLowThreshold();

	float getHighThreshold();

	String getBlockingConfiguration();

	Long getReferenceRsId();

	String getReferenceRsType();
	
	String getReferenceRsDatabaseConfiguration();
	
	String getQueryToReferenceBlockingConfiguration();
	
}
