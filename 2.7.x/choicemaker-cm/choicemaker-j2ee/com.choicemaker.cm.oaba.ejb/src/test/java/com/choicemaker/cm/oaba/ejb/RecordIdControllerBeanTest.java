/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.RecordIdControllerBean.computeBatchMultiple;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RecordIdControllerBeanTest {
	
	private static int[][] batchTotalExpected = new int[][] {
		{0, 0, 1},
		{0, 1, 1},
		{1, 0, 1},
		{1, 1, 1},
		{1, 9, 1},
		{1, 10, 1},
		{1, 11, 2},
		{0, 9, 1},
		{0, 10, 1},
		{0, 11, 1},
		{9, 0, 9},
		{10, 0, 10},
		{11, 0, 11},
		{11, 109, 11},
		{11, 110, 11},
		{11, 111, 22}
	};

	@Test
	public void testComputeBatchMultiple() {
		for (int[] bte : batchTotalExpected) {
			int batchSize = bte[0];
			int totalSize = bte[1];
			int expected = bte[2];
			int computed = computeBatchMultiple(batchSize, totalSize);
			assertTrue(expected == computed);
		}
	}

}
