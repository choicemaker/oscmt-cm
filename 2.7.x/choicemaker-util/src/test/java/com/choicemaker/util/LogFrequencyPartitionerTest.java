package com.choicemaker.util;

import static com.choicemaker.util.LogFrequencyPartitioner.getPartitionIndex;
import static com.choicemaker.util.LogFrequencyPartitioner.partition;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.choicemaker.util.LogFrequencyPartitioner.ValueCount;
import com.choicemaker.util.LogFrequencyPartitioner.ValueRank;

public class LogFrequencyPartitionerTest {

	public static final int[] ILLEGAL_PARTITION_NULL = null;
	public static final int[] ILLEGAL_PARTITION_EMPTY = new int[] {};
	public static final int[] ILLEGAL_PARTITION_INVALID = new int[] { 0 };

	public static final int ILLEGAL_PARTITION_NUMBER_A = 0;

	public static final int LEGAL_PARTITION_NUMBER_A = 1;
	public static final int LEGAL_PARTITION_NUMBER_B = 2;

	public static final int ILLEGAL_FREQUENCY_COUNT_A = 0;

	public static final int LEGAL_FREQUENCY_COUNT_A = 1;

	public static final int AN_ILLEGAL_PARTITION_IDX = -1;

	@Test
	public void testPartition() {
		List<ValueCount> input1 = new ArrayList<>();
		input1.add(new ValueCount("value 1", 1));

		int numPartitions = 1;
		List<ValueRank> expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));

		List<ValueRank> computed = partition(input1, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 2;
		computed = partition(input1, numPartitions);
		assertTrue(expected.equals(computed));

		List<ValueCount> input2 = new ArrayList<>();
		input2.add(new ValueCount("value 1", 10));
		input2.add(new ValueCount("value 2", 15));
		input2.add(new ValueCount("value 3a", 39));
		input2.add(new ValueCount("value 3b", 40));
		input2.add(new ValueCount("value 3c", 41));
		input2.add(new ValueCount("value 4", 100));

		numPartitions = 1;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 2", 0));
		expected.add(new ValueRank("value 3a", 0));
		expected.add(new ValueRank("value 3b", 0));
		expected.add(new ValueRank("value 3c", 0));
		expected.add(new ValueRank("value 4", 0));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 2;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 2", 0));
		expected.add(new ValueRank("value 3a", 1));
		expected.add(new ValueRank("value 3b", 1));
		expected.add(new ValueRank("value 3c", 1));
		expected.add(new ValueRank("value 4", 1));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 3;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 2", 0));
		expected.add(new ValueRank("value 3a", 1));
		expected.add(new ValueRank("value 3b", 1));
		expected.add(new ValueRank("value 3c", 1));
		expected.add(new ValueRank("value 4", 2));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 5;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 2", 0));
		expected.add(new ValueRank("value 3a", 2));
		expected.add(new ValueRank("value 3b", 2));
		expected.add(new ValueRank("value 3c", 3));
		expected.add(new ValueRank("value 4", 4));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

	}

	@Test
	public void testGetPartitionIndex() {

		int[] partition = new int[] { 1 };
		int count = 1;
		int index = getPartitionIndex(partition, count);
		assertTrue(index == 0);

		count = 2;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 0);

		count = Integer.MAX_VALUE;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 0);

		partition = new int[] {
				1, 10 };
		count = 1;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 0);

		count = 2;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 1);

		count = 9;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 1);

		count = 10;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 1);

		count = Integer.MAX_VALUE;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 1);

		index = AN_ILLEGAL_PARTITION_IDX;
		try {
			count = 0;
			index = getPartitionIndex(partition, count);
			fail("Failed to catch illegal rank: " + count);
		} catch (IllegalArgumentException x) {
			// Computed value should not change from initial value
			assertTrue(index == AN_ILLEGAL_PARTITION_IDX);
		}

		index = AN_ILLEGAL_PARTITION_IDX;
		try {
			count = 1;
			index = getPartitionIndex(ILLEGAL_PARTITION_NULL, count);
			fail("Failed to catch illegal parition (null)");
		} catch (IllegalArgumentException x) {
			// Computed value should not change from initial value
			assertTrue(index == AN_ILLEGAL_PARTITION_IDX);
		}

		index = AN_ILLEGAL_PARTITION_IDX;
		try {
			count = 1;
			index = getPartitionIndex(ILLEGAL_PARTITION_EMPTY, count);
			fail("Failed to catch illegal parition (empty)");
		} catch (IllegalArgumentException x) {
			// Computed value should not change from initial value
			assertTrue(index == AN_ILLEGAL_PARTITION_IDX);
		}

		// Partition values are checked only if assertions are enabled
		boolean assertOn = false;
		assert assertOn = true;
		if (assertOn) {
			index = AN_ILLEGAL_PARTITION_IDX;
			try {
				count = 1;
				index = getPartitionIndex(ILLEGAL_PARTITION_INVALID, count);
				fail("Failed to catch illegal parition (invalid)");
			} catch (IllegalArgumentException x) {
				// Computed value should not change from initial value
				assertTrue(index == AN_ILLEGAL_PARTITION_IDX);
			}
		}

	}

}
