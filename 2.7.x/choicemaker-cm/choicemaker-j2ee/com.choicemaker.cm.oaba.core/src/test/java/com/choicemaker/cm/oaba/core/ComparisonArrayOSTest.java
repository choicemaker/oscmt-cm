/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import static com.choicemaker.cm.oaba.core.ComparisonSetGenerationUtils.generate;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ComparisonArrayOSTest {

	public void test(int maxBlockSize) {
		Set<SyntheticRecordIds> testSet = generate(maxBlockSize);
		for (SyntheticRecordIds srids: testSet) {
			List<String> queryIds = srids.getQueryIds();
			List<String> referenceIds = srids.getReferenceIds();
			ComparisonArrayOS<String> caos = new ComparisonArrayOS<>(
					queryIds, referenceIds, SyntheticRecordIds.ID_TYPE, SyntheticRecordIds.ID_TYPE, maxBlockSize
					);
//			int count = 0;
			while (caos.hasNextPair()) {
//				++count;
				ComparisonPair<String> cp = caos.getNextPair();
				String id1 = cp.getId1();
				assertTrue(id1 != null);
				assertTrue(queryIds.contains(id1));
				String id2 = cp.getId2();
				assertTrue(id2 != null);
				assertTrue(queryIds.contains(id2) || referenceIds.contains(id2));
			}
		}
	}

	@Test
	public void testMaxBlockSize1() {
		test(1);
	}

	@Test
	public void testMaxBlockSize3() {
		test(3);
	}

	@Test
	public void testMaxBlockSize5() {
		test(5);
	}

	@Test
	public void testMaxBlockSize7() {
		test(7);
	}

	@Test
	public void testMaxBlockSize11() {
		test(11);
	}

	@Test
	public void testMaxBlockSize13() {
		test(13);
	}

	@Test
	public void testMaxBlockSize23() {
		test(23);
	}

	@Test
	public void testMaxBlockSize47() {
		test(47);
	}

}
