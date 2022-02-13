/*******************************************************************************
 * Copyright (c) 2015, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.choicemaker.util.Precondition;

public class ComparisonArrayOSTest2 {

	private static String error0 =
		"Computed count (%d) != expected count (%d) for %d "
				+ "query records, %d reference records and "
				+ "maxBlockSize = %d";

	/** N choose 2 */
	public static int NC2(int n) {
		int retVal = 0;
		if (n > 1) {
			retVal = (n * (n - 1)) / 2;
		}
		return retVal;
	}

	public static int expectedCount(int countQueryRecords,
			int countReferenceRecords, int maxBlockSize) {
		Precondition.assertBoolean(countQueryRecords >= 0);
		Precondition.assertBoolean(countReferenceRecords >= 0);
		Precondition.assertBoolean(maxBlockSize >= 2);

		final int countRStage = Math.min(countQueryRecords, maxBlockSize);
		final int countTStage = Math.max(0, countQueryRecords - maxBlockSize);
		final int countTMaster = countReferenceRecords;

		final int countStep4 = NC2(countRStage);
		final int countStep5 = countRStage * countTStage;
		final int countStep6 = countRStage * countTMaster;
		final int countStep7 = countTMaster * Math.min(countTStage, 4);
		final int countStep8 = Math.min(NC2(countTStage), 4 * countTStage);

		final int retVal =
			countStep4 + countStep5 + countStep6 + countStep7 + countStep8;

		return retVal;
	}

	public void test(int countQueryRecords, int countReferenceRecords,
			int maxBlockSize) {
		test(countQueryRecords, countReferenceRecords,
			maxBlockSize, false);
	}

	public void test(int countQueryRecords, int countReferenceRecords,
			int maxBlockSize, boolean dump) {
		Precondition.assertBoolean(countQueryRecords >= 0);
		Precondition.assertBoolean(countReferenceRecords >= 0);
		Precondition.assertBoolean(maxBlockSize >= 2);

		SyntheticRecordIds testSet =
			new SyntheticRecordIds(countQueryRecords, countReferenceRecords);

		final int expectedCount = expectedCount(countQueryRecords,
				countReferenceRecords, maxBlockSize);

		ComparisonArrayOS<String> ca =
			new ComparisonArrayOS<>(testSet.getQueryIds(),
					testSet.getReferenceIds(), SyntheticRecordIds.ID_TYPE,
					SyntheticRecordIds.ID_TYPE, maxBlockSize);
		if (dump) {
			System.out.println(ca.dump());
		}

		Set<ComparisonPair<String>> uniquePairs = new HashSet<>();
		int computedCount = 0;
		while (ca.hasNextPair()) {
			ComparisonPair<String> cp = ca.getNextPair();
			boolean isNew = uniquePairs.add(cp);
			if (isNew) {
				++computedCount;
				if (dump) {
					String msg0 = "Pair %d: %s";
					String msg = String.format(msg0, computedCount, cp);
					System.out.println(msg);
				}
			} else {
				if (dump) {
					String msg0 = "Duplicate: %s";
					String msg = String.format(msg0, cp);
					System.err.println(msg);
				}
			}
		}

		if (expectedCount != computedCount) {
			System.err.println(ca.toString());
			System.err.println(ca.dump());
			String error = String.format(error0, computedCount, expectedCount,
					countQueryRecords, countReferenceRecords, maxBlockSize);
			fail(error);
		}
	}

	@Test
	public void test_0_0_M() {
		for (int maxBlockSize = 2; maxBlockSize < 101; maxBlockSize++) {
			assertTrue(expectedCount(0, 0, maxBlockSize) == 0);
			test(0, 0, maxBlockSize);
		}
	}

	@Test
	public void test_M_R_M() {
		for (int maxBlockSize = 2; maxBlockSize < 51; maxBlockSize++) {
			for (int countQuery = 0; countQuery <= maxBlockSize; countQuery++) {
				for (int countReference =
					0; countReference < 10; countReference++) {
					assertTrue(expectedCount(countQuery, countReference,
							maxBlockSize) == NC2(countQuery)
									+ (countQuery * countReference));
					test(countQuery, countReference, maxBlockSize);
				}
			}
		}
	}

	@Test
	public void test_4_3_2() {
		final int maxBlockSize = 2;
		final int countQuery = 4;
		final int countReference = 3;
		final int lowerBound =
			NC2(maxBlockSize) + (maxBlockSize * countReference);
		int upperBound = NC2(countQuery) + (countQuery * countReference);
		int expectedCount =
			expectedCount(countQuery, countReference, maxBlockSize);
		assertTrue(expectedCount >= lowerBound);
		assertTrue(expectedCount <= upperBound);
		test(countQuery, countReference, maxBlockSize /*, true*/);
	}

	@Test
	public void test_Q_0_M() {
		for (int maxBlockSize = 2; maxBlockSize < 51; maxBlockSize++) {
			for (int countQuery = maxBlockSize + 2; countQuery <= maxBlockSize
					+ 2; countQuery++) {
				int countReference = 3;
				final int lowerBound =
					NC2(maxBlockSize) + (maxBlockSize * countReference);
				int upperBound =
					NC2(countQuery) + (countQuery * countReference);
				int expectedCount =
					expectedCount(countQuery, countReference, maxBlockSize);
				assertTrue(expectedCount >= lowerBound);
				assertTrue(expectedCount <= upperBound);
				test(countQuery, countReference, maxBlockSize);
			}
		}
	}

	@Test
	public void test_Q_R_M() {
		for (int maxBlockSize = 2; maxBlockSize < 51; maxBlockSize++) {
			for (int countQuery = maxBlockSize + 2; countQuery <= maxBlockSize
					+ 2; countQuery++) {
				for (int countReference =
					0; countReference < 3; countReference++) {
					int lowerBound =
						NC2(maxBlockSize) + (maxBlockSize * countReference);
					int upperBound =
						NC2(countQuery) + (countQuery * countReference);
					int expectedCount =
						expectedCount(countQuery, countReference, maxBlockSize);
					assertTrue(expectedCount >= lowerBound);
					assertTrue(expectedCount <= upperBound);
					test(countQuery, countReference, maxBlockSize);
				}
			}
		}
	}
}
