package com.choicemaker.cms.api;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;

public interface TransitivityJobInfo extends BatchJobInfo {

	UrmJobInfo getUrmJobInfo();

	OabaJobInfo getOabaJobInfo();

	TransitivityParameters getTransitityParameters();

	OabaSettings getOabaSettings();

	ServerConfiguration getServerConfiguration();

	String getWorkingDirectory();

	MatchPairInfo getMatchPairInfo();

	TransitiveGroupInfo getTransitiveGroupInfo();

}
