/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.cachecount;

import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.IBlockingConfiguration;
import com.choicemaker.cm.io.blocking.automated.IBlockingField;
import com.choicemaker.cm.io.blocking.automated.IBlockingValue;
import com.choicemaker.cm.io.blocking.automated.ICountField;

/**
 * In-memory implementation of ABA statistics
 * @author    mbuechi (implemented as 'CacheCountSource')
 * @author    rphall (renamed 'AbaStatisticsImpl')
 */
public class AbaStatisticsImpl implements AbaStatistics {
	private int mainTableSize;
	private ICountField[] counts;

	public AbaStatisticsImpl(int mainTableSize, ICountField[] counts) {
		this.mainTableSize = mainTableSize;
		this.counts = counts;
	}

	// API BUG 2016-08-28 rphall
	// Blocking values are tied to a particular blocking configuration. Stats
	// also tied to a particular blocking configuration. Hence, the
	// parameter for blocking configuration is never used by any implementation
	public long computeBlockingValueCounts(
			IBlockingConfiguration unused_and_unnecessary,
			IBlockingValue[] blockingValues) {
	// END BUG
		for (int i = 0; i < blockingValues.length; ++i) {
			IBlockingValue bv = blockingValues[i];
			IBlockingField bf = bv.getBlockingField();
			int fieldNum = bf.getDbField().getNumber();
			if (fieldNum >= counts.length) {
				// conservative assumptions
				bv.setTableSize(mainTableSize);
				bv.setCount(mainTableSize);
			} else {
				ICountField f = counts[fieldNum];
				bv.setTableSize(f.getTableSize());
				Integer count = f.getCountForValue(bv.getValue());
				if (count != null) {
					bv.setCount(count.intValue());
				} else {
					bv.setCount(f.getDefaultCount());
				}
			}
		}
		return mainTableSize;
	}
}
