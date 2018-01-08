package com.choicemaker.cms.args;

import com.choicemaker.cm.args.OabaLinkageType;

public interface AbaParameters {

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
