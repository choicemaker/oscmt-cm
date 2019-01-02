package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.RecordIdControllerBean.*;
import static org.junit.Assert.*;

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
