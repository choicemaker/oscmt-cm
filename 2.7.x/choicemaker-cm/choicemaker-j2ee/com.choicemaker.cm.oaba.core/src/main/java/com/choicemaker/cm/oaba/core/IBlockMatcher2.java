/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;

/**
 * This interface defines how to match a block source.
 * 
 * This version takes in a record source for stage and another for master. It
 * uses IComparisonGroupSource instead of IBlockSource.
 * 
 * Also, this version doesn't use a validator since we have already split up
 * staging and master records.
 * 
 * @author pcheung
 *
 */
public interface IBlockMatcher2<T extends Comparable<T>> {

	/**
	 * This method matches all the blocks in the ComparisonGroup source.
	 * 
	 * @param cgSource
	 *            - ComparisonGroup srouce
	 * @param stageModel
	 *            - stage data probability model. Need this to read records
	 * @param masterModel
	 *            - master data probability model. Need this to read records
	 * @param stage
	 *            - staging record source
	 * @param master
	 *            - master record source
	 * @param mSink
	 *            - MatchRecord2 sink
	 * @param append
	 *            - true if you want to append to mSink.
	 * @param differ
	 *            - differ threshold
	 * @param match
	 *            - match threshold
	 * @param maxBlockSize
	 *            - maximum block size
	 */
	public void matchBlocks(IComparisonArraySource<T> cgSource,
			ImmutableProbabilityModel stageModel,
			ImmutableProbabilityModel masterModel, RecordSource stage,
			RecordSource master, IMatchRecord2Sink<T> mSink, boolean append,
			float differ, float match, int maxBlockSize)
			throws BlockingException;

	/**
	 * This returns the number of comparisons made. This is reset in the
	 * matchBlocks method.
	 * 
	 * @return int - number of comparisons made.
	 */
	public int getNumComparesMade();

	/**
	 * This returns the number of matches and holds found. This is reset in the
	 * matchBlocks method.
	 * 
	 * @return int - number of matches found
	 */
	public int getNumMatches();

	/**
	 * This returns the number of blocks processed. This is reset in the
	 * matchBlocks method.
	 * 
	 * @return int - number of blocks compared
	 */
	public int getNumBlocks();

	/**
	 * This returns the amount of time in ms in reading records into hash map.
	 * This is reset in the matchBlocks method.
	 * 
	 * @return long
	 */
	public long getTimeInReadMaps();

	/**
	 * This returns the amount of time in ms in performing matches in the block.
	 * This also includes the time to write out to match file. This is reset in
	 * the matchBlocks method.
	 * 
	 * @return long
	 */
	public long getTimeInHandleBlock();

	/**
	 * This returns the amount of time in ms in writing to the match file. This
	 * is reset in the matchBlocks method.
	 * 
	 * @return long
	 */
	public long getTimeInWriteMatches();

}
