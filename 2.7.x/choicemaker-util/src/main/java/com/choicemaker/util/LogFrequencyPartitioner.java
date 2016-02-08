/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
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
import java.util.List;

/**
 * Converts frequency counts into discrete logarithmic bins.
 *
 * @author Martin Buechi (original version)
 * @author Rick Hall (minor usability tweaks)
 */
public class LogFrequencyPartitioner {

	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	public static final int MIN_MIN_FREQUENCY = 1;
	public static final int MAX_MAX_FREQUENCY = Integer.MAX_VALUE;

	public static final int MIN_PARTITION_IDX = 0;

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
	 * If the number of partition is 3, the partitions will be:
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
	public static List<ValuePartitionPair> partition(
			List<ValueCountPair> pairs, int numPartitions) {
		if (pairs == null) {
			throw new IllegalArgumentException(
					"null list of value-count valueCountPairs");
		}
		if (numPartitions < 1) {
			throw new IllegalArgumentException(
					"non-positive number of paritions: " + numPartitions);
		}
		List<ValuePartitionPair> retVal = new ArrayList<>();
		if (!pairs.isEmpty()) {
			int minFrequency = MAX_MAX_FREQUENCY;
			int maxFrequency = MIN_MIN_FREQUENCY;
			for (ValueCountPair pair : pairs) {
				if (pair.count > maxFrequency) {
					maxFrequency = pair.count;
				}
				if (pair.count < minFrequency) {
					minFrequency = pair.count;
				}
			}

			assert maxFrequency >= minFrequency;
			assert minFrequency >= MIN_MIN_FREQUENCY;

			int[] boundary = new int[numPartitions];
			double f =
				Math.pow(((double) maxFrequency) / minFrequency,
						1.00d / numPartitions);
			double b = maxFrequency;
			for (int i = numPartitions - 1; i >= 0; --i) {
				boundary[i] = (int) (b + 0.5);
				b = b / f;
			}

			for (ValueCountPair pair : pairs) {
				int index = getPartitionIndex(boundary, pair.count);
				ValuePartitionPair vp =
					new ValuePartitionPair(pair.value, index);
				retVal.add(vp);
			}
		}
		return retVal;
	}

	/**
	 * Returns an index between 0 and (partition.length - 1), inclusive
	 * 
	 * @param partition
	 *            an array of positive counts arranged in increasing order
	 * @param c
	 *            a positive count
	 * @return the largest index for which c > partition[index]
	 */
	public static int getPartitionIndex(int[] partition, int c) {
		if (c <= 0) {
			throw new IllegalArgumentException("negative frequency count: " + c);
		}
		if (partition == null || partition.length == 0) {
			throw new IllegalArgumentException("null or empty partition");
		}

		// Check elements of the partition if asserts are enabled
		boolean assertOn = false;
		assert assertOn = true;
		if (assertOn) {
			for (int i = 0; i < partition.length; i++) {
				if (partition[i] < 1) {
					String msg =
						"Non-positive count (" + partition[i]
								+ ") at partition index " + i;
					throw new IllegalArgumentException(msg);
				}
			}
		}

		int i = 0;
		while (i < (partition.length - 1) && c > partition[i]) {
			++i;
		}
		return i;
	}

	/**
	 * Reads a file in which values and counts are on alternate lines: values on
	 * odd lines (1, 3, 5, ...) and counts on even lines (2, 4, 6, ...).
	 * Equivalent to invoking
	 * <pre>
	 * readFile(filename, null, null)
	 * </pre>
	 * 
	 * @param fileName
	 *            name of an existing value-count file
	 * @throws IOException
	 *             if the file can not be found or opened
	 * @throws NumberFormatException
	 *             if any count is not a valid integer
	 * @throws IllegalArgumentException
	 *             if the fileName is null or any line in the file contains an
	 *             invalid value or non-positive count, or if the last value is
	 *             not paired with a subsequent count.
	 */
	public static List<ValueCountPair> readFile(String fileName)
			throws IOException {
		return readFile(fileName, null, null);
	}

	/**
	 * Reads a file in which values and counts are separated by <code>elementSep</code>
	 * and pairs are separated by <code>lineSep</code>.
	 * 
	 * @param fileName
	 *            name of an existing value-count file
	 * @param elementSep
	 * 			separates the value from the count within a pair. If null,
	 *          values and counts must appear on alternating lines, as if
	 *          <code>lineSep</code> is also the element separator.
	 * @param lineSep
	 * 			separates pairs
	 * @throws IOException
	 *             if the file can not be found or opened
	 * @throws NumberFormatException
	 *             if any count is not a valid integer
	 * @throws IllegalArgumentException
	 *             if the fileName is null or any line in the file contains an
	 *             invalid value or non-positive count, or if the last value is
	 *             not paired with a subsequent count.
	 */
	public static List<ValueCountPair> readFile(String fileName,
			Character elementSep, String lineSep) throws IOException {
		if (fileName == null) {
			throw new IllegalArgumentException("null file name");
		}
		if (lineSep == null) {
			lineSep = EOL;
		}
		String sElementSep =
			elementSep == null ? null : String.valueOf(elementSep);

		List<ValueCountPair> retVal = new ArrayList<>();
		BufferedReader in = null;
		try {
			FileReader fr =
				new FileReader(new File(fileName).getAbsoluteFile());
			in = new BufferedReader(fr);
			String line = in.readLine();
			while (line != null) {
				String value = null;
				String sCount = null;

				if (sElementSep != null) {
					// This simple algorithm will fail (or 'succeed'
					// erroneously) if there are escaped element-separator
					// tokens in the line
					String[] tokens = line.split(sElementSep);
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
					sCount = sCount.trim();
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
				ValueCountPair vcp = new ValueCountPair(value, count);
				retVal.add(vcp);

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
	 * @param fileName
	 *            name of an existing value-count file
	 * @throws IOException
	 *             if the file can not be found or opened
	 * @throws NumberFormatException
	 *             if any count is not a valid integer
	 * @throws IllegalArgumentException
	 *             if the fileName is null or any line in the file contains an
	 *             invalid value or non-positive count, or if the last value is
	 *             not paired with a subsequent count.
	 */
	public static void writeFile(List<ValuePartitionPair> pairs, String fileName)
			throws IOException {
		writeFile(pairs, fileName, null, null);
	}

	/**
	 * Writes a file in which values and partition indices are separated by the
	 * specified element separator and value-partition pairs are separated by
	 * the specified line separator.
	 * 
	 * @param fileName
	 *            name of an existing value-count file
	 * @param elementSep
	 *            separates a value from a partition index. If null, then values
	 *            and partition indices are written on alternating lines, as if
	 *            the element separator is the same as the line separator.
	 * @param lineSep
	 *            separates value-partition pairs from each other. If null, then
	 *            the system default for the line separator is used.
	 * @throws IOException
	 *             if the file can not be created or written
	 */
	public static void writeFile(List<ValuePartitionPair> pairs,
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
		FileOutputStream fs = null;
		Writer w = null;
		try {
			fs = new FileOutputStream(fileName);
			w = new OutputStreamWriter(new BufferedOutputStream(fs));
			for (ValuePartitionPair p : pairs) {
				w.write(p.value);
				if (elementSep != null) {
					w.write(elementSep);
				} else {
					w.write(lineSep);
				}
				w.write(p.partition + lineSep);
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
	}

	public static class ValueCountPair {
		public final String value;
		public final int count;

		public ValueCountPair(String val, int count) {
			if (val == null) {
				throw new IllegalArgumentException("null value");
			}
			if (count < LogFrequencyPartitioner.MIN_MIN_FREQUENCY) {
				throw new IllegalArgumentException("non-positive count: "
						+ count);
			}
			this.value = val;
			this.count = count;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + count;
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
			ValueCountPair other = (ValueCountPair) obj;
			if (count != other.count) {
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
			return "ValueCountPair [value=" + value + ", count=" + count + "]";
		}
	}

	public static class ValuePartitionPair {
		public final String value;
		public final int partition;

		public ValuePartitionPair(String val, int partition) {
			if (val == null) {
				throw new IllegalArgumentException("null value");
			}
			if (partition < 0) {
				throw new IllegalArgumentException("negative partition: "
						+ partition);
			}
			this.value = val;
			this.partition = partition;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + partition;
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
			ValuePartitionPair other = (ValuePartitionPair) obj;
			if (partition != other.partition) {
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
			return "ValuePartitionPair [value=" + value + ", partition="
					+ partition + "]";
		}
	}

}
