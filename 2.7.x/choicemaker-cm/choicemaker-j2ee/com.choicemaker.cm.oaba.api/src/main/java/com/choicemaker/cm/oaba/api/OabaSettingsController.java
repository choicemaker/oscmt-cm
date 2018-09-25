/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.util.List;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.core.ImmutableProbabilityModel;

/**
 * Manages a database of ABA and OABA settings.
 * 
 * @author rphall
 *
 */
public interface OabaSettingsController {

	AbaSettings save(AbaSettings settings);

	OabaSettings save(OabaSettings settings);

	AbaSettings findAbaSettings(long id);

	OabaSettings findOabaSettings(long id);

	OabaSettings findOabaSettingsByJobId(long jobId);

	DefaultSettings setDefaultAbaConfiguration(ImmutableProbabilityModel model,
			String databaseConfiguration, String blockingConfiguration,
			AbaSettings aba);

	DefaultSettings setDefaultOabaConfiguration(ImmutableProbabilityModel model,
			String databaseConfiguration, String blockingConfiguration,
			OabaSettings oaba);

	AbaSettings findDefaultAbaSettings(String modelConfigurationId,
			String databaseConfiguration, String blockingConfiguration);

	// AbaSettings findDefaultAbaSettings(ImmutableProbabilityModel model,
	// String databaseConfigurationName, String blockingConfigurationName);

	OabaSettings findDefaultOabaSettings(String modelConfigurationId,
			String databaseConfiguration, String blockingConfiguration);

	// OabaSettings findDefaultOabaSettings(ImmutableProbabilityModel model,
	// String databaseConfigurationName, String blockingConfigurationName);

	List<AbaSettings> findAllAbaSettings();

	List<OabaSettings> findAllOabaSettings();

	List<DefaultSettings> findAllDefaultAbaSettings();

	List<DefaultSettings> findAllDefaultOabaSettings();

}
