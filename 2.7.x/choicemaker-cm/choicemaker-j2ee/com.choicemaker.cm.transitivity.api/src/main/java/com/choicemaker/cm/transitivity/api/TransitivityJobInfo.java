/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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
