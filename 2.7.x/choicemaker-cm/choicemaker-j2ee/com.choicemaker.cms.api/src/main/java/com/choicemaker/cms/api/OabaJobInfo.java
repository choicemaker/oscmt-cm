package com.choicemaker.cms.api;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;

public interface OabaJobInfo extends BatchJobInfo {
	
	UrmJobInfo getUrmJobInfo();

	OabaParameters getOabaParameters();
	
	OabaSettings getOabaSettings();

	ServerConfiguration getServerConfiguration();
	
	String getWorkingDirectory();
	
	MatchPairInfo getMatchPairInfo();

}
