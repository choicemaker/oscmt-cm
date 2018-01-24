/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.cachecount;

import java.util.Arrays;

import com.choicemaker.cm.aba.AbaStatistics;
import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.aba.IFieldValueCounts;
import com.choicemaker.cm.aba.base.FieldValueCounts;

/**
 * In-memory implementation of ABA statistics
 *
 * @author mbuechi (implemented as 'CacheCountSource')
 * @author rphall (renamed 'AbaStatisticsImpl')
 */
public class AbaStatisticsImpl implements AbaStatistics {
	private int mainTableSize;
	private FieldValueCounts[] counts;

	public AbaStatisticsImpl(int mainTableSize, final IFieldValueCounts[] arIFVC) {
		this.mainTableSize = mainTableSize;
		if (arIFVC == null) {
			this.counts= null;
		} else {
			this.counts = new FieldValueCounts[arIFVC.length];
			for (int i = 0 ; i<arIFVC.length; i++) {
				IFieldValueCounts ifvc = arIFVC[i];
				if (ifvc == null) {
					counts[i] = null;
				} else if (ifvc instanceof FieldValueCounts) {
					counts[i] = (FieldValueCounts) ifvc;
				} else {
					counts[i] = new FieldValueCounts(ifvc);
				}
			}
		}
	}

	@Override
	@Deprecated
	public long computeBlockingValueCounts(
			IBlockingConfiguration unused_and_unnecessary,
			IBlockingValue[] blockingValues) {
		return computeBlockingValueCounts(blockingValues);
	}

	@Override
	public long computeBlockingValueCounts(IBlockingValue[] blockingValues) {
		for (int i = 0; i < blockingValues.length; ++i) {
			IBlockingValue bv = blockingValues[i];
			IBlockingField bf = bv.getBlockingField();
			int fieldNum = bf.getDbField().getNumber();
			if (fieldNum >= counts.length) {
				// conservative assumptions
				bv.setTableSize(mainTableSize);
				bv.setCount(mainTableSize);
			} else {
				IFieldValueCounts f = counts[fieldNum];
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(counts);
		result = prime * result + mainTableSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbaStatisticsImpl other = (AbaStatisticsImpl) obj;
		if (!Arrays.equals(counts, other.counts))
			return false;
		if (mainTableSize != other.mainTableSize)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbaStatisticsImpl [mainTableSize=" + mainTableSize + ", counts="
				+ Arrays.toString(counts) + "]";
	}

}
