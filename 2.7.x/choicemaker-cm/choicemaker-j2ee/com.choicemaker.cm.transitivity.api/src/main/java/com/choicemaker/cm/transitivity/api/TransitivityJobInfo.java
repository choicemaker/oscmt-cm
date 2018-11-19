package com.choicemaker.cm.transitivity.api;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJobInfo;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;

public interface TransitivityJobInfo extends BatchJobInfo {

	long getUrmJobId();

	OabaJobInfo getOabaJobInfo();

	TransitivityParameters getTransitityParameters();

	OabaSettings getOabaSettings();

	ServerConfiguration getServerConfiguration();

	String getWorkingDirectory();

	MatchPairInfo getMatchPairInfo();

	TransitiveGroupInfo getTransitiveGroupInfo();

}
