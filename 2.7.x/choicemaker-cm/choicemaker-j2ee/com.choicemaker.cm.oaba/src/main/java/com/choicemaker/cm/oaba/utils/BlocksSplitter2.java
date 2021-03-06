/*******************************************************************************
 * Copyright (c) 2015, 2021 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.utils;

import java.util.ArrayList;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.BlockSet;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;

/**
 * This object creates a list of IBlockSinks, one for each range of block sizes.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class BlocksSplitter2 {

	private ArrayList sinks;

	// this keeps track of number of bucket files per block size.
	// numBuckets[i] contains the buckets for size i + 1.
	// numBuckets[0] is 0 because there is no single element block.
	private int[] numBuckets;

	// if there is more than 1 bucket file for an id, we need to rotate the
	// bucket files.
	// this variable stores the rotation info
	private int[] rotate;

	// private int maxSize; //maximum size of a block set.
	private int minSize; // minimum size of a block set.

	/*
	 * sinks.get(i) contains blocks with size >= minSize + interval*(i) and <
	 * minSize + interval*(i+1)
	 */
	private int numFiles; // number of files to create

	// size of the interval
	// interval = ceiling((maxSize - minSize)/numFiles)
	private int interval;

	private IBlockSinkSourceFactory bFactory;

	public BlocksSplitter2(IBlockSinkSourceFactory bFactory, int minSize,
			int maxSize, int numFiles) {
		// this.maxSize = maxSize;
		this.minSize = minSize;
		this.numFiles = numFiles;
		this.bFactory = bFactory;

		this.interval =
			(int) Math.round(Math.ceil((maxSize - minSize) * 1.0 / numFiles));
		if (this.interval == 0)
			this.interval = 1;

		// initialize numBuckets
		numBuckets = new int[numFiles];
		numBuckets[0] = 0;
		for (int i = 1; i < numFiles; i++) {
			numBuckets[i] = 1;
		}

		// initialize rotate
		rotate = new int[numFiles + 1];
		for (int i = 0; i < numFiles; i++) {
			rotate[i] = 0;
		}

		// System.out.println (minSize + " " + maxSize + " " + numFiles + " " +
		// interval);
	}

	/**
	 * This method sets the number of bucket files to create for the given
	 * interval. By default there is 1 bucket file per interval, but you can
	 * increase that if there are a lot of blocks for a given size.
	 * 
	 * @param num
	 */
	public void setSize(int interval, int num) {
		if (num >= 0 && num < numFiles)
			numBuckets[interval - 1] = num;
	}

	/**
	 * This method initializes the sinks array list.
	 * 
	 * Call this method after you are done calling setSize (int, int).
	 *
	 */
	public void Initialize() throws BlockingException {
		sinks = new ArrayList(numFiles);

		for (int i = 0; i < numFiles; i++) {
			IBlockSink sink = bFactory.getNextSink();
			sinks.add(sink);

			// clean up old files
			if (sink.exists())
				bFactory.removeSink(sink);
		}
	}

	/**
	 * This method opens all the sinks.
	 * 
	 */
	/*
	 * public void openAll () throws IOException { for (int i=0; i<
	 * sinks.size(); i++) { IBlockSink sink = (IBlockSink) sinks.get(i);
	 * sink.open(); } }
	 */

	/**
	 * This method closes all the sinks.
	 * 
	 */
	/*
	 * public void closeAll () throws IOException { for (int i=0; i<
	 * sinks.size(); i++) { IBlockSink sink = (IBlockSink) sinks.get(i);
	 * sink.close(); } }
	 */

	/**
	 * This returns the array list of the bucket sinks. They are in the order of
	 * block size.
	 * 
	 * @return ArrayList of IBlockSink
	 */
	public ArrayList getSinks() {
		return sinks;
	}

	/**
	 * This method write the block set to the appropiate file by calculating the
	 * number of file with size less than it and take into consideration if
	 * there is more than 1 file for this size.
	 * 
	 * @param block
	 */
	public void writeToSink(BlockSet block) throws BlockingException {
		int size = block.getRecordIDs().size();
		int ind = getBucket(size);

		int num = numBuckets[ind]; // number of buckets this size has

		// need to rotate if there is more than 1 file
		if (num > 1) {
			ind = ind + (rotate[size - 1] % num);
			rotate[size - 1]++;
		}

		IBlockSink sink = (IBlockSink) sinks.get(ind);
		sink.append();
		sink.writeBlock(block);
		sink.close();
	}

	/**
	 * This method removes all files used by the sinks.
	 *
	 */
	public void removeAll() throws BlockingException {
		for (int i = 0; i < sinks.size(); i++) {
			bFactory.removeSink((IBlockSink) sinks.get(i));
		}
	}

	/**
	 * This gets the bucket to which this size belongs.
	 * 
	 * return = floor (size - minSize)/interval
	 * 
	 * @param size
	 */
	private int getBucket(int size) {
		int i = (size - minSize) / interval;

		// System.out.println (size + " " + i);

		return i;
	}

}
