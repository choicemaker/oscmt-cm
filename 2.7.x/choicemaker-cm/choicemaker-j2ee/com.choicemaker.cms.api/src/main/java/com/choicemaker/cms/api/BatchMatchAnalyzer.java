/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.api;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
// FIXME import com.choicemaker.cm.oaba.server.ejb.OabaService;
// FIXME import com.choicemaker.cm.oaba.server.ejb.ServerConfigurationException;
// FIXME import com.choicemaker.cm.transitivity.server.ejb.TransitivityService;

/**
 * Allows a client application to match record collections and perform
 * transitivity analysis in a batch (asynchronous) mode.
 * 
 * @author emoussikaev (original URM)
 * @author rphall (URM2)
 */
public interface BatchMatchAnalyzer /* FIXME extends OabaService, TransitivityService */ {

	public long startDeduplicationAndAnalysis(String externalID,
			TransitivityParameters tp, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration)
			throws /* FIXME ServerConfiguration */Exception;

	public long startLinkageAndAnalysis(String externalID,
			TransitivityParameters tp, OabaSettings oabaSettings,
			ServerConfiguration serverConfiguration)
			throws /* FIXME ServerConfiguration */Exception;

}
