package com.choicemaker.cms.api;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;

public abstract class OnlineContext {
	OnlineParameters abaParameters;
	AbaSettings abaSettings;
	TransitivityParameters tp;
	OabaSettings oabaSettings;
	OnlineServerConfiguration serverConfiguration;
}
