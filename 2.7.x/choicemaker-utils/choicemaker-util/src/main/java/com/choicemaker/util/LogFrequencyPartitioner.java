/*
 * Copyright (c) 2001, 2018 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Converts frequency counts into discrete logarithmic bins.
 *
 * @author Martin Buechi (original version)
 * @author Rick Hall (minor usability tweaks)
 */
public class LogFrequencyPartitioner {

	private static final Logger logger = Logger
			.getLogger(LogFrequencyPartitioner.class.getName());

	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	public static final int MIN_RANK = 0;
	public static final int MIN_COUNT = 1;

	private LogFrequencyPartitioner() {
	}

	/**
	 * Returns a list of pairs in which each value is assigned a partition index
	 * based upon its input count. The partition are spaced evenly by the
	 * logarithm of the maximum count they contain. For example, consider the
	 * following list of input pairs:
	 * 
	 * <pre>
	 * { "value 1", 10}, {"value 2", 15}, { "value 3", 40}, { "value 4", 100}
	 * </pre>
	 * 
	 * If the number of partitions is 3, the partitions will be:
	 * 
	 * <pre>
	 * {10 to 21}, {22 to 46}, and {46 to 100}
	 * </pre>
	 * 
	 * The output pairs will be
	 * 
	 * <pre>
	 * { "value 1", 0}, {"value 2", 0}, { "value 3", 1}, { "value 4", 2}
	 * </pre>
	 * 
	 * @param pairs
	 *            Pairs of values and frequency counts
	 * @param numPartitions
	 *            a positive number of partitions
	 * @return Pairs of values and logarithmic partition indices. The indices
	 *         will range from zero to <code>numPartitions</code>, inclusive.
	 */
	public static List<ValueRank> partition(List<ValueCount> pairs,
			int numPartitions) {
		if (pairs == null) {
			throw new IllegalArgumentException("null list of value-rank pairs");
		}
		if (numPartitions < 1) {
			throw new IllegalArgumentException(
					"non-positive number of paritions: " + numPartitions);
		}
		List<ValueRank> retVal = new ArrayList<>();
		if (!pairs.isEmpty()) {
			int minCount = Integer.MAX_VALUE;
			int maxCount = MIN_COUNT;
			for (ValueRank pair : pairs) {
				if (pair.rank > maxCount) {
					maxCount = pair.rank;
				}
				if (pair.rank < minCount) {
					minCount = pair.rank;
				}
			}

			assert maxCount >= minCount;
			assert minCount >= MIN_COUNT;

			int[] boundary = computeBoundary(maxCount, minCount, numPartitions);

			for (ValueCount pair : pairs) {
				int index = getPartitionIndex(boundary, pair.rank);
				ValueRank vp = new ValueRank(pair.value, index);
				retVal.add(vp);
			}
		}
		return retVal;
	}

	static int[] computeBoundary(int maxCount, int minCount, int numPartitions) {
		if (minCount < 1 || maxCount < minCount || numPartitions < 1) {
			throw new IllegalArgumentException(
					"Invalid argument to LogFrequencyPartitioner.computeBoundary(int, int, int)");
		}
		logger.fine("computeBoundary: minCount: " + minCount);
		logger.fine("computeBoundary: maxCount: " + maxCount);
		logger.fine("computeBoundary: numPartitions: " + numPartitions);
		int[] retVal = new int[numPartitions];
		double f =
			Math.pow(((double) maxCount) / minCount, 1.00d / numPartitions);
		logger.fine("computeBoundary: f: " + f);
		double b = maxCount;
		for (int i = numPartitions - 1; i >= 0; --i) {
			retVal[i] = (int) (b + 0.5);
			b = b / f;
		}
		logger.fine("computeBoundary: boundary: " + Arrays.toString(retVal));
		return retVal;
	}

	/**
	 * Returns an index between 0 and maxIndex, inclusive, where maxIndex is
	 * (partition.length -1).
	 * 
	 * @param partition
	 *            an array of positive counts arranged in increasing order
	 * @param c
	 *            a positive count
	 * @return the largest index for which c &gt; partition[index], or maxIndex
	 *         if c is greater than every value in the partition
	 */
	public static int getPartitionIndex(int[] partition, final int c) {
		if (c < MIN_COUNT) {
			throw new IllegalArgumentException("negative count: " + c);
		}
		if (partition == null || partition.length == 0) {
			throw new IllegalArgumentException("null or empty partition");
		}

		// Check elements of the partition if asserts are enabled
		boolean assertOn = false;
		assert assertOn = true;
		int previousPartition = Integer.MIN_VALUE;
		if (assertOn) {
			for (int i = 0; i < partition.length; i++) {
				if (partition[i] < MIN_COUNT) {
					String msg =
						"Non-positive count (" + partition[i]
								+ ") at partition index " + i;
					throw new IllegalArgumentException(msg);
				}
				if (partition[i] < previousPartition) {
					String msg =
						"Unordered partition (" + Arrays.toString(partition)
								+ ") at partition index " + i;
					throw new IllegalArgumentException(msg);
				}
				previousPartition = partition[i];
			}
		}

		final int LIMIT = (partition.length - 1);
		int i = MIN_RANK;
		while (i < LIMIT && c >= partition[i]) {
			assert i < partition.length - 1;
			if (c < partition[i + 1]) {
				break;
			}
			++i;
			assert i < partition.length;
		}
		return i;
	}

	/**
	 * Reads a file in which values and counts are on alternate lines: values on
	 * odd lines (1, 3, 5, ...) and counts on even lines (2, 4, 6, ...).
	 * Equivalent to invoking
	 * 
	 * <pre>
	 * readFile(filename, null, null)
	 * </pre>
	 * 
	 * @param fileName
	 *            name of an existing value-count file
	 * @return a non-null list of non-null ValueCount instances
	 * @throws IOException
	 *             if the file can not be found or opened
	 * @throws NumberFormatException
	 *             if any count is not a valid integer
	 * @throws IllegalArgumentException
	 *             if the fileName is null or any line in the file contains an
	 *             invalid value or non-positive count, or if the last value is
	 *             not paired with a subsequent count.
	 */
	public static List<ValueCount> readFile(String fileName) throws IOException {
		return readFile(fileName, null, null);
	}

	/**
	 * Reads a file in which values and counts are separated by
	 * <code>elementSep</code> and pairs are separated by <code>lineSep</code>.
	 * 
	 * @param fileName
	 *            name of an existing value-count file
	 * @param elementSep
	 *            separates the value from the count within a pair. If null,
	 *            values and counts must appear on alternating lines, as if
	 *            <code>lineSep</code> is also the element separator.
	 * @param lineSep
	 *            separates pairs
	 * @return a non-null list of non-null ValueCount instances
	 * @throws IOException
	 *             if the file can not be found or opened
	 * @throws NumberFormatException
	 *             if any count is not a valid integer
	 * @throws IllegalArgumentException
	 *             if the fileName is null or any line in the file contains an
	 *             invalid value or non-positive count, or if the last value is
	 *             not paired with a subsequent count.
	 */
	public static List<ValueCount> readFile(String fileName,
			Character elementSep, String lineSep) throws IOException {
		if (fileName == null) {
			throw new IllegalArgumentException("null file name");
		}
		if (lineSep == null) {
			lineSep = EOL;
		}

		Pattern p = null;
		if (elementSep != null) {
			String sElementSep = String.valueOf(elementSep);
			String literal = Pattern.quote(sElementSep);
			p = Pattern.compile(literal);
		}

		List<ValueCount> retVal = new ArrayList<>();
		BufferedReader in = null;
		try {
			FileReader fr =
				new FileReader(new File(fileName).getAbsoluteFile());
			in = new BufferedReader(fr);
			String line = in.readLine();
			while (line != null) {
				String value = null;
				String sCount = null;

				if (p != null) {
					// This simple algorithm will fail (or 'succeed'
					// erroneously) if there are escaped element-separator
					// tokens in the line
					String[] tokens = p.split(line, -1);
					if (tokens.length != 2) {
						String msg = "Invalid line: '" + line + "'";
						throw new IllegalArgumentException(msg);
					}
					value = tokens[0].trim();
					sCount = tokens[1].trim();

				} else {
					value = line.trim();
					sCount = in.readLine();
					if (sCount == null) {
						String msg = "Missing count for value '" + value + "'";
						throw new IllegalArgumentException(msg);
					} else {
						sCount = sCount.trim();
					}
				}

				int count = 0;
				try {
					count = Integer.parseInt(sCount);
				} catch (NumberFormatException x) {
					String msg =
						"Invalid count (" + sCount + ") for value '" + value
								+ "'";
					throw new IllegalArgumentException(msg);
				}
				if (count < 1) {
					String msg =
						"Non-positive count (" + count + ") for value '"
								+ value + "'";
					throw new IllegalArgumentException(msg);
				}
				ValueCount vc = new ValueCount(value, count);
				retVal.add(vc);

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		return retVal;
	}

	/**
	 * Writes a file in which values and partition indices are on alternate
	 * lines: values on odd lines (1, 3, 5, ...) and counts on even lines (2, 4,
	 * 6, ...).
	 * 
	 * @param pairs
	 *            a non-null list of non-null ValueRank instances
	 * @param fileName
	 *            name of the file (must not already exist)
	 * @throws IOException
	 *             if the file can not be found or opened
	 * @throws NumberFormatException
	 *             if any count is not a valid integer
	 * @throws IllegalArgumentException
	 *             if the fileName is null or any line in the file contains an
	 *             invalid value or non-positive count, or if the last value is
	 *             not paired with a subsequent count.
	 */
	public static void writeFile(List<ValueRank> pairs, String fileName)
			throws IOException {
		writeFile(pairs, fileName, null, null);
	}

	/**
	 * Writes a file in which values and partition indices are separated by the
	 * specified element separator and value-partition pairs are separated by
	 * the specified line separator.
	 * 
	 * @param pairs
	 *            a non-null list of non-null ValueRank instances
	 * @param fileName
	 *            name of the file (must not already exist)
	 * @param elementSep
	 *            separates a value from a partition index. If null, then values
	 *            and partition indices are written on alternating lines, as if
	 *            the element separator is the same as the line separator.
	 * @param lineSep
	 *            separates value-partition pairs from each other. If null, then
	 *            the system default for the line separator is used.
	 * @param <T>
	 *            a type that extends ValueRank
	 * @return the number of values-partition pairs that were written
	 * @throws IOException
	 *             if the file can not be created or written
	 */
	public static <T extends ValueRank> int writeFile(List<T> pairs,
			String fileName, Character elementSep, String lineSep)
			throws IOException {
		if (pairs == null) {
			throw new IllegalArgumentException("null pairs");
		}
		if (fileName == null) {
			throw new IllegalArgumentException("null file name");
		}
		if (lineSep == null) {
			lineSep = EOL;
		}
		int retVal = 0;
		FileOutputStream fs = null;
		Writer w = null;
		try {
			fs = new FileOutputStream(fileName);
			w = new OutputStreamWriter(new BufferedOutputStream(fs));
			for (ValueRank p : pairs) {
				w.write(p.value);
				if (elementSep != null) {
					w.write(elementSep);
				} else {
					w.write(lineSep);
				}
				w.write(p.rank + lineSep);
				++retVal;
			}
		} finally {
			if (w != null) {
				w.flush();
				w.close();
				fs.close();
				w = null;
				fs = null;
			}
			if (fs != null) {
				fs.close();
				fs = null;
			}
		}
		return retVal;
	}

	public static class ValueRank {
		public final String value;
		public final int rank;

		public ValueRank(String val, int count) {
			if (val == null) {
				throw new IllegalArgumentException("null value");
			}
			if (count < LogFrequencyPartitioner.MIN_RANK) {
				throw new IllegalArgumentException("non-positive rank: "
						+ count);
			}
			this.value = val;
			this.rank = count;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + rank;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ValueRank other = (ValueRank) obj;
			if (rank != other.rank) {
				return false;
			}
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "ValueCountPair [value=" + value + ", rank=" + rank + "]";
		}
	}

	/**
	 * A subclass of ValueRank in which the rank (a.k.a. count) must be positive
	 */
	public static class ValueCount extends ValueRank {

		public ValueCount(String val, int count) {
			super(val, count);
			if (count < LogFrequencyPartitioner.MIN_COUNT) {
				throw new IllegalArgumentException("non-positive rank: "
						+ count);
			}
		}

		@Override
		public String toString() {
			return "ValueCount [value=" + value + ", count=" + rank + "]";
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			return super.equals(obj);
			/*
			 * ValueCount other = (ValueCount) obj; if (rank != other.rank) {
			 * return false; } if (value == null) { if (other.value != null) {
			 * return false; } } else if (!value.equals(other.value)) { return
			 * false; } return true;
			 */
		}

	}

}
