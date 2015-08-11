/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

public interface AbaStatisticsCache {

	public void putStatistics(ImmutableProbabilityModel model,
			AbaStatistics counts);

	public AbaStatistics getStatistics(ImmutableProbabilityModel model);

}
