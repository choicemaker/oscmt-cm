package com.choicemaker.cms.api;

import com.choicemaker.cm.batch.api.BatchJobInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.transitivity.api.TransitivityJobInfo;

public interface UrmJobInfo extends BatchJobInfo {
	
	OabaJobInfo getOabaJobInfo();

	TransitivityJobInfo getTransitivityJobInfo();

}
