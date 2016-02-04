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

import java.util.ArrayList;
import java.util.List;

//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

/**
 * Converts frequency counts into discrete logarithmic bins.
 *
 * @author Martin Buechi (original version)
 * @author Rick Hall (minor usability tweaks)
 */
public class LogFrequencyPartitioner {

//	public static final String LINE_SEPARATOR = System
//			.getProperty("line.separator");

//	public static void main(String[] args) throws IOException {
//		LogFrequencyPartitioner lfp = new LogFrequencyPartitioner();
//		lfp.readFile(args[0]);
//		lfp.computeBoundaries(Integer.parseInt(args[2]));
//		lfp.writeFile(args[1]);
//	}
	
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
	 * <pre>
	 * { "value 1", 10}, {"value 2", 15}, { "value 3", 40}, { "value 4", 100}
	 * </pre>
	 * If the number of partition is 3, the partitions will be:
	 * <pre>
	 * {10 to 21}, {22 to 46}, and {46 to 100}
	 * </pre>
	 * The output pairs will be
	 * <pre>
	 * { "value 1", 0}, {"value 2", 0}, { "value 3", 1}, { "value 4", 2}
	 * </pre>
	 * 
	 * @param pairs Pairs of values and frequency counts
	 * @param numPartitions a positive number of partitions
	 * @return Pairs of values and logarithmic partition indices. The indices
	 * will range from zero to <code>numPartitions</code>, inclusive.
	 */
	public static List<ValuePartitionPair> partition(List<ValueCountPair> pairs, 
			int numPartitions) {
		if (pairs == null) {
			throw new IllegalArgumentException("null list of value-count valueCountPairs");
		}
		if (numPartitions < 1) {
			throw new IllegalArgumentException("non-positive number of paritions: " + numPartitions);
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
			double f = Math.pow(((double) maxFrequency) / minFrequency, 1.00d / numPartitions);
			double b = maxFrequency;
			for (int i = numPartitions - 1; i >= 0; --i) {
				boundary[i] = (int)(b + 0.5);
				b = b / f;
			}
			
			for (ValueCountPair pair : pairs) {
				int index = getPartitionIndex(boundary, pair.count);
				ValuePartitionPair vp = new ValuePartitionPair(pair.value, index);
				retVal.add(vp);
			}
		}
		return retVal;
	}

	/**
	 * Returns an index between 0 and (partition.length - 1), inclusive
	 * @param partition an array of positive counts arranged in increasing
	 * order
	 * @param c a positive count
	 * @return the largest index for which c > partition[index]
	 */
	public static int getPartitionIndex(int[] partition, int c) {
		if (c <= 0) {
			throw new IllegalArgumentException("negative frequency count: " + c);
		}
		if (partition == null || partition.length == 0) {
			throw new IllegalArgumentException(
					"null or empty partition");
		}

		// Check element of the partition if asserts are enabled
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
		while (i < (partition.length-1) && c > partition[i]) {
			++i;
		}
		return i;
	}

//	public void addPair(ValueCountPair pair) {
//		if (pair == null) {
//			throw new IllegalArgumentException("Null value-count pair");
//		}
//		if (pair.count > maxFrequency) {
//			maxFrequency = pair.count;
//		}
//		if (pair.count < minFrequency) {
//			minFrequency = pair.count;
//		}
//		valueCountPairs.add(pair);
//	}
//
//	public void addPair(String value, int count) {
//		addPair(new ValueCountPair(value,count));
//	}

//	public void readFile(String fileName) throws IOException {
//		FileReader fr = new FileReader(new File(fileName).getAbsoluteFile());
//		BufferedReader in = new BufferedReader(fr);
//		try {
//			while (in.ready()) {
//				String value = in.readLine().trim();
//				int count = Integer.parseInt(in.readLine().trim());
//				addPair(value, count);
//			}
//		} catch (NumberFormatException ex) {
//			System.out.println (ex.toString());
//		}
//		in.close();
//		fr.close();
//	}

//	public void computeBoundaries(int numPartitions) {
//		if (numPartitions < 1) {
//			throw new IllegalArgumentException("non-positive number of paritions: " + numPartitions);
//		}
//		assert maxFrequency >= minFrequency;
//		assert minFrequency >= DEFAULT_FREQUENCY;
//		boundary = new int[numPartitions];
//		double f = Math.pow(((double) maxFrequency) / minFrequency, 1.00d / numPartitions);
//		double b = maxFrequency;
//		for (int i = numPartitions - 1; i >= 0; --i) {
//			boundary[i] = (int)(b + 0.5);
//			//System.out.println(boundary[i]);
//			b = b / f;
//		}
//	}

//	public void writeFile(String fileName) throws IOException {
//		writeFile(fileName, LINE_SEPARATOR, LINE_SEPARATOR);
//	}
//
//	public void writeFile(String fileName, String elementSep, String lineSep) throws IOException {
//		FileOutputStream fs = new FileOutputStream(fileName);
//		Writer w = new OutputStreamWriter(new BufferedOutputStream(fs));
//		Iterator i = valueCountPairs.iterator();
//		while (i.hasNext()) {
//			ValueCountPair p = (ValueCountPair) i.next();
//			w.write(p.val + elementSep);
//			w.write(getFrequencyClass(p.count) + lineSep);
//		}
//		w.flush();
//		w.close();
//		fs.close();
//	}

	public static class ValueCountPair {
		public final String value;
		public final int count;
		ValueCountPair(String val, int count) {
			if (val == null) {
				throw new IllegalArgumentException("null value");
			}
			if (count < LogFrequencyPartitioner.MIN_MIN_FREQUENCY) {
				throw new IllegalArgumentException("non-positive count: " + count);
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
		ValuePartitionPair(String val, int partition) {
			if (val == null) {
				throw new IllegalArgumentException("null value");
			}
			if (partition < 0) {
				throw new IllegalArgumentException("negative partition: " + partition);
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
			return "ValuePartitionPair [value=" + value + ", partition=" + partition + "]";
		}
	}

}
