package com.choicemaker.util;

import static com.choicemaker.util.LogFrequencyPartitioner.computeBoundary;
import static com.choicemaker.util.LogFrequencyPartitioner.getPartitionIndex;
import static com.choicemaker.util.LogFrequencyPartitioner.partition;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.choicemaker.util.LogFrequencyPartitioner.ValueCount;
import com.choicemaker.util.LogFrequencyPartitioner.ValueRank;

public class LogFrequencyPartitionerTest {

	private static final Logger logger = Logger
			.getLogger(LogFrequencyPartitionerTest.class.getName());

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
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 1));
		computed = partition(input1, numPartitions);
		assertTrue(expected.equals(computed));

		List<ValueCount> input2 = new ArrayList<>();
		input2.add(new ValueCount("value 10", 10));
		input2.add(new ValueCount("value 15", 15));
		input2.add(new ValueCount("value 39", 39));
		input2.add(new ValueCount("value 40", 40));
		input2.add(new ValueCount("value 41", 41));
		input2.add(new ValueCount("value 100", 100));

		numPartitions = 1;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 10", 0));
		expected.add(new ValueRank("value 15", 0));
		expected.add(new ValueRank("value 39", 0));
		expected.add(new ValueRank("value 40", 0));
		expected.add(new ValueRank("value 41", 0));
		expected.add(new ValueRank("value 100", 0));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 2;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 10", 0));
		expected.add(new ValueRank("value 15", 0));
		expected.add(new ValueRank("value 39", 0));
		expected.add(new ValueRank("value 40", 0));
		expected.add(new ValueRank("value 41", 0));
		expected.add(new ValueRank("value 100", 1));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 3;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 10", 0));
		expected.add(new ValueRank("value 15", 0));
		expected.add(new ValueRank("value 39", 0));
		expected.add(new ValueRank("value 40", 0));
		expected.add(new ValueRank("value 41", 0));
		expected.add(new ValueRank("value 100", 2));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 5;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 10", 0));
		expected.add(new ValueRank("value 15", 0));
		expected.add(new ValueRank("value 39", 1));
		expected.add(new ValueRank("value 40", 2));
		expected.add(new ValueRank("value 41", 2));
		expected.add(new ValueRank("value 100", 4));
		computed = partition(input2, numPartitions);
		assertTrue(expected.equals(computed));

	}

	public void failComputeBoundary(int maxCount, int minCount,
			int numPartitions) {
		try {
			logger.fine("computeBoundary args: " + maxCount + ", " + minCount
					+ ", " + numPartitions);
			computeBoundary(maxCount, minCount, numPartitions);
			fail("computeBoundary failed to detect invalid args: " + maxCount
					+ ", " + minCount + ", " + numPartitions);
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		} catch (Exception x) {
			fail("computeBoundary unexpected exception: " + x.toString());
		}
	}

	@Test
	public void testBoundaryInvalidParameters() {
		// Maximum count too small
		failComputeBoundary(0, 1, 1);
		failComputeBoundary(1, 2, 1);

		// Maximum and minimum count too small
		failComputeBoundary(0, 0, 1);
		failComputeBoundary(0, 1, 1);

		// Minimum count too small
		failComputeBoundary(1, 0, 1);

		// Partition count too small
		failComputeBoundary(1, 1, 0);
	}

	@Test
	public void testBoundary() {
		int maxCount;
		int minCount;
		int numPartitions;
		int[] expected;
		int[] computed;

		maxCount = 1;
		minCount = 1;
		numPartitions = 1;
		expected = new int[] { 1 };
		computed = computeBoundary(maxCount, minCount, numPartitions);
		assertTrue(Arrays.equals(expected, computed));

		maxCount = 1;
		minCount = 1;
		numPartitions = 5;
		expected = new int[] {
				1, 1, 1, 1, 1 };
		computed = computeBoundary(maxCount, minCount, numPartitions);
		assertTrue(Arrays.equals(expected, computed));

		maxCount = 100;
		minCount = 1;
		numPartitions = 2;
		expected = new int[] {
				10, 100 };
		computed = computeBoundary(maxCount, minCount, numPartitions);
		assertTrue(Arrays.equals(expected, computed));

		maxCount = 100;
		minCount = 1;
		numPartitions = 4;
		expected = new int[] {
				3, 10, 32, 100 };
		computed = computeBoundary(maxCount, minCount, numPartitions);
		assertTrue(Arrays.equals(expected, computed));

	}

	public void failGetPartition(int[] partitions, int count) {
		try {
			logger.fine("getPartitionIndex args: "
					+ Arrays.toString(partitions) + ", " + count);
			getPartitionIndex(partitions, count);
			fail("getPartitionIndex failed to detect invalid args: "
					+ Arrays.toString(partitions) + ", " + count);
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		} catch (Exception x) {
			fail("getPartitionIndex unexpected exception: " + x.toString());
		}
	}

	@Test
	public void testGetPartitionInvalidParameters() {
		int[] partitions;
		int count;

		// Null partition
		partitions = null;
		count = 1;
		failGetPartition(partitions, count);

		// Empty partition
		partitions = new int[0];
		count = 1;
		failGetPartition(partitions, count);

		// Detailed check if asserts are enabled
		boolean assertOn = false;
		assert assertOn = true;
		if (assertOn) {
			// Invalid count within partition
			partitions = new int[] { -1 };
			count = 1;
			failGetPartition(partitions, count);

			// Invalid count within partition
			partitions = new int[] { 0 };
			count = 1;
			failGetPartition(partitions, count);

			partitions = new int[] {
					0, 1 };
			count = 1;
			failGetPartition(partitions, count);

			// Unordered partition
			partitions = new int[] {
					2, 1 };
			count = 1;
			failGetPartition(partitions, count);
		}

		// Invalid count
		partitions = new int[] {
				1, 2 };
		count = 0;
		failGetPartition(partitions, count);
	}

	@Test
	public void testGetPartition() {
		int[] partitions;
		int count;
		int expected;
		int computed;

		partitions = new int[] { 1 };
		count = 1;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 10;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		partitions = new int[] {
				1, 1, 1, 1, 1 };
		count = 1;
		expected = 4;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 10;
		expected = 4;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		partitions = new int[] {
				10, 100 };
		count = 1;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 10;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 100;
		expected = 1;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 1000;
		expected = 1;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		partitions = new int[] {
				3, 10, 32, 100 };
		count = 1;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 3;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 4;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 9;
		expected = 0;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 10;
		expected = 1;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 11;
		expected = 1;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 31;
		expected = 1;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 32;
		expected = 2;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 33;
		expected = 2;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 99;
		expected = 2;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 100;
		expected = 3;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 101;
		expected = 3;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);

		count = 1000;
		expected = 3;
		computed = getPartitionIndex(partitions, count);
		assertTrue(expected == computed);
	}

	@Test
	public void testPartition2() {

		int numPartitions;
		List<ValueRank> expected;
		List<ValueRank> computed;
		final double sqrt10 = Math.pow(10., 0.5); // 3.162...

		List<ValueCount> input = new ArrayList<>();
		input.add(new ValueCount("value 1", 1));
		input.add(new ValueCount("value 3", (int) sqrt10));
		input.add(new ValueCount("value 4", 1 + (int) sqrt10));
		input.add(new ValueCount("value 10", 10));
		input.add(new ValueCount("value 31", (int) (10. * sqrt10)));
		input.add(new ValueCount("value 32", 1 + (int) (10. * sqrt10)));
		input.add(new ValueCount("value 100", 100));
		input.add(new ValueCount("value 101", 100));
		input.add(new ValueCount("value 1000", 100));

		numPartitions = 1;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 3", 0));
		expected.add(new ValueRank("value 4", 0));
		expected.add(new ValueRank("value 10", 0));
		expected.add(new ValueRank("value 31", 0));
		expected.add(new ValueRank("value 32", 0));
		expected.add(new ValueRank("value 100", 0));
		expected.add(new ValueRank("value 101", 0));
		expected.add(new ValueRank("value 1000", 0));
		computed = partition(input, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 2;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 3", 0));
		expected.add(new ValueRank("value 4", 0));
		expected.add(new ValueRank("value 10", 0));
		expected.add(new ValueRank("value 31", 0));
		expected.add(new ValueRank("value 32", 0));
		expected.add(new ValueRank("value 100", 1));
		expected.add(new ValueRank("value 101", 1));
		expected.add(new ValueRank("value 1000", 1));
		computed = partition(input, numPartitions);
		assertTrue(expected.equals(computed));

		numPartitions = 4;
		expected = new ArrayList<>();
		expected.add(new ValueRank("value 1", 0));
		expected.add(new ValueRank("value 3", 0));
		expected.add(new ValueRank("value 4", 0));
		expected.add(new ValueRank("value 10", 1));
		expected.add(new ValueRank("value 31", 1));
		expected.add(new ValueRank("value 32", 2));
		expected.add(new ValueRank("value 100", 3));
		expected.add(new ValueRank("value 101", 3));
		expected.add(new ValueRank("value 1000", 3));
		computed = partition(input, numPartitions);
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
		assertTrue(index == 0);

		count = 9;
		index = getPartitionIndex(partition, count);
		assertTrue(index == 0);

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
