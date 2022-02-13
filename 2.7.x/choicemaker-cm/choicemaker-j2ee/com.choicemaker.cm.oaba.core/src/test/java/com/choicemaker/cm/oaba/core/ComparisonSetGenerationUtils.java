/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.oaba.core;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ComparisonSetGenerationUtils {
	
	private ComparisonSetGenerationUtils() {}

	public static Set<Integer> boundaryValuesAt(final int n0) {
		Set<Integer> retVal = new LinkedHashSet<>();
		for (int i=-1; i<=1; i++) {
			int n = n0 + i;
			if (n >= 0) {
				retVal.add(n);
			}
		}
		return retVal;
	}

	public static Set<SyntheticRecordIds> generate(final int bQ, final int bR) {
		Set<SyntheticRecordIds> retVal = new LinkedHashSet<>();
		Set<Integer> queryCounts = boundaryValuesAt(bQ);
		Set<Integer> referenceCounts = boundaryValuesAt(bR);
		for (int q : queryCounts) {
			for (int r : referenceCounts) {
				SyntheticRecordIds srids = new SyntheticRecordIds(q,r);
				retVal.add(srids);
			}
		}
		return retVal;
	}

	public static Set<SyntheticRecordIds> generate(int maxBlockSize) {
		SortedSet<SyntheticRecordIds> retVal = new TreeSet<>();
	
		Set<SyntheticRecordIds> setSynRids = generate(1,1);
		retVal.addAll(setSynRids);
	
		setSynRids = generate(maxBlockSize/2,1);
		retVal.addAll(setSynRids);
		setSynRids = generate(maxBlockSize/2,maxBlockSize/2);
		retVal.addAll(setSynRids);
		setSynRids = generate(maxBlockSize/2,maxBlockSize);
		retVal.addAll(setSynRids);
		setSynRids = generate(maxBlockSize/2,2*maxBlockSize);
		retVal.addAll(setSynRids);
	
		setSynRids = generate(maxBlockSize,1);
		retVal.addAll(setSynRids);
		setSynRids = generate(maxBlockSize,maxBlockSize/2);
		retVal.addAll(setSynRids);
		setSynRids = generate(maxBlockSize,maxBlockSize);
		retVal.addAll(setSynRids);
		setSynRids = generate(maxBlockSize,2*maxBlockSize);
		retVal.addAll(setSynRids);
	
		setSynRids = generate(2*maxBlockSize,1);
		retVal.addAll(setSynRids);
		setSynRids = generate(2*maxBlockSize,maxBlockSize/2);
		retVal.addAll(setSynRids);
		setSynRids = generate(2*maxBlockSize,maxBlockSize);
		retVal.addAll(setSynRids);
		setSynRids = generate(2*maxBlockSize,2*maxBlockSize);
		retVal.addAll(setSynRids);
	
		return retVal;
	}
	
}
