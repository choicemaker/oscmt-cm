/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.aba.AbaStatisticsCache;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.core.DatabaseException;

public interface AbaStatisticsController extends AbaStatisticsCache {

	void updateReferenceStatistics(OabaParameters params)
			throws DatabaseException;

	void updateReferenceStatistics(String urlString) throws DatabaseException;

}
