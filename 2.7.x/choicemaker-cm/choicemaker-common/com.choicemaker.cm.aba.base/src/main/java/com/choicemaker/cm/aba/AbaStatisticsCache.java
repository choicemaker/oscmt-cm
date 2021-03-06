/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

public interface AbaStatisticsCache {

	public String computeBlockingConfigurationId(ImmutableProbabilityModel m,
			String blockingConfiguration, String databaseConfiguration);

	public void putStatistics(String blockingConfigurationId,
			AbaStatistics counts);

	public void putStatistics(IBlockingConfiguration bc, AbaStatistics counts);

	public AbaStatistics getStatistics(String bcId);

	public AbaStatistics getStatistics(IBlockingConfiguration bc);

}
