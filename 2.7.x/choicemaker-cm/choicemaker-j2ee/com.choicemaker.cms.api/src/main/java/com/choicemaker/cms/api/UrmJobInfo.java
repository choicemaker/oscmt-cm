package com.choicemaker.cms.api;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJobInfo;

public interface UrmJobInfo extends BatchJobInfo {
	
	long getUrmJobId();

//	OabaParameters getOabaParameters();
//	
//	OabaSettings getOabaSettings();
//
//	ServerConfiguration getServerConfiguration();
//	
//	String getWorkingDirectory();
//	
//	MatchPairInfo getMatchPairInfo();

}
