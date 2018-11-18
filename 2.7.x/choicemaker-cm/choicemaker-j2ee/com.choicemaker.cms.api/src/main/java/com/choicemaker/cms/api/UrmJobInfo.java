package com.choicemaker.cms.api;

public interface UrmJobInfo extends BatchJobInfo {
	
	OabaJobInfo getOabaJobInfo();
	
	TransitivityJobInfo getTransitivityJobInfo();

}
