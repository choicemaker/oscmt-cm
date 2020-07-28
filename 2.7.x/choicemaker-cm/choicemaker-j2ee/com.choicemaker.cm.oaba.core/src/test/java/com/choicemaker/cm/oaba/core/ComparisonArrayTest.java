/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.choicemaker.util.Precondition;

public class ComparisonArrayTest {

	private static String error0 =
		"Computed count (%d) != expected count (%d) for %d "
				+ "query records and %d reference records";

	public void test(int countQueryRecords, int countReferenceRecords) {
		Precondition.assertBoolean(countQueryRecords >= 0);
		Precondition.assertBoolean(countReferenceRecords >= 0);

		SyntheticRecordIds testSet =
			new SyntheticRecordIds(countQueryRecords, countReferenceRecords);

		final int expectedCount =
			((countQueryRecords * (countQueryRecords - 1)) / 2)
					+ (countQueryRecords * countReferenceRecords);

		ComparisonArray<String> ca = new ComparisonArray<>(
				testSet.getQueryIds(), testSet.getReferenceIds(),
				SyntheticRecordIds.ID_TYPE, SyntheticRecordIds.ID_TYPE);

		int computedCount = 0;
		while (ca.hasNextPair()) {
			ca.getNextPair();
			++computedCount;
		}

		if (expectedCount != computedCount) {
			String error = String.format(error0, computedCount, expectedCount,
					countQueryRecords, countReferenceRecords);
			fail(error);
		}
	}

	@Test
	public void test_0_0() {
		test(0, 0);
	}

	@Test
	public void test_0_1() {
		test(0, 1);
	}

	@Test
	public void test_1_0() {
		test(1, 0);
	}

	@Test
	public void test_1_1() {
		test(1, 1);
	}

	@Test
	public void test_0_2() {
		test(0, 2);
	}

	@Test
	public void test_2_0() {
		test(2, 0);
	}

	@Test
	public void test_1_2() {
		test(1, 2);
	}

	@Test
	public void test_2_1() {
		test(2, 1);
	}

	@Test
	public void test_2_2() {
		test(2, 2);
	}

	@Test
	public void test_42_37() {
		test(42, 37);
	}

}
