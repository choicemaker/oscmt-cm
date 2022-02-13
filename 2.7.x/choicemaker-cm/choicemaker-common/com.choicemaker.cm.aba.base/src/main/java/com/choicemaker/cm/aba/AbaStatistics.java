/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba;

import java.io.IOException;

/**
 * Computes and sets the number of occurrences ('counts') of blocking values for
 * a particular blocking configuration.
 */
public interface AbaStatistics {

	/**
	 * API BUG. Blocking values are tied to a particular blocking configuration.
	 * Stats are implicitly tied to a particular blocking configuration. Hence,
	 * the parameter for blocking configuration is never used by any
	 * implementation
	 * 
	 * @param configuration
	 *            unused and unnecessary
	 * @param blockingValues
	 *            non-null array of blocking values to be used to compute counts
	 * @return ?
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	long computeBlockingValueCounts(IBlockingConfiguration configuration,
			IBlockingValue[] blockingValues) throws IOException;

	/**
	 * @param configuration
	 *            unused and unnecessary
	 * @param blockingValues
	 *            non-null array of blocking values to be used to compute counts
	 * @return ?
	 */
	long computeBlockingValueCounts(IBlockingValue[] blockingValues);

}
