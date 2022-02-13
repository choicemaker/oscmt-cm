/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.cachecount;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.choicemaker.cm.aba.AbaStatistics;
import com.choicemaker.cm.aba.AbaStatisticsCache;
import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.aba.util.BlockingConfigurationUtils;
import com.choicemaker.cm.core.ImmutableProbabilityModel;

public class AbaStatisticsCacheImpl implements AbaStatisticsCache {

	// private static final Logger log =
	// Logger.getLogger(AbaStatisticsSingletonHACK.class.getName());

	// -- Accessors

	/** Map of model configuration names to ABA statistics */
	private Map<String, AbaStatistics> cachedStats = new ConcurrentHashMap<>();

	@Override
	public String computeBlockingConfigurationId(ImmutableProbabilityModel m,
			String blockingConfiguration, String databaseConfiguration) {
		return BlockingConfigurationUtils.createBlockingConfigurationId(m,
				blockingConfiguration, databaseConfiguration);
	}

	@Override
	public void putStatistics(String blockingConfigurationId,
			AbaStatistics counts) {
		this.cachedStats.put(blockingConfigurationId, counts);
	}

	@Override
	public void putStatistics(IBlockingConfiguration bc, AbaStatistics counts) {
		String blockingConfigurationId = bc.getBlockingConfiguationId();
		putStatistics(blockingConfigurationId, counts);
	}

	@Override
	public AbaStatistics getStatistics(String blockingConfigurationId) {
		AbaStatistics retVal = this.cachedStats.get(blockingConfigurationId);
		return retVal;
	}

	@Override
	public AbaStatistics getStatistics(IBlockingConfiguration bc) {
		String blockingConfigurationId = bc.getBlockingConfiguationId();
		return getStatistics(blockingConfigurationId);
	}

}
