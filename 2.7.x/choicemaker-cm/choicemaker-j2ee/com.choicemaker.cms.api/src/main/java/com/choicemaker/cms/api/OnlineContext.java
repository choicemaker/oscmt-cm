package com.choicemaker.cms.api;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cms.args.AbaParameters;
import com.choicemaker.cms.args.AbaServerConfiguration;

public abstract class OnlineContext {
	AbaParameters abaParameters;
	AbaSettings abaSettings;
	TransitivityParameters tp;
	OabaSettings oabaSettings;
	AbaServerConfiguration serverConfiguration;
}
