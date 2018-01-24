/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.FILE_SEPARATOR;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.MAX_FILE_SIZE;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.TEXT_SUFFIX;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.formatJobId;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.getWorkingDir;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.impl.BlockSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.MatchRecord2CompositeSink;
import com.choicemaker.cm.oaba.impl.MatchRecord2CompositeSource;

/**
 * This object configures factory objects for Batch jobs.
 * 
 * @author pcheung
 *
 */
public class TransitivityFileUtils {

	public static final String BASENAME_TRANSMATCH_STORE_INDEXED =
		"transMatch_";

	public static final String NAMESTEM_TRANSANALYSIS = "trans_analysis";

	static final long serialVersionUID = 271;

	protected static String getCompositeTransMatchFileName(BatchJob job) {
		String wd = getWorkingDir(job);
		assert wd.endsWith(FILE_SEPARATOR);
		String id = formatJobId(job.getId());
		String retVal = wd + BASENAME_TRANSMATCH_STORE_INDEXED + id;
		return retVal;
	}

	@SuppressWarnings("rawtypes")
	public static IMatchRecord2Sink getCompositeTransMatchSink(BatchJob job) {
		String fileName = getCompositeTransMatchFileName(job);
		return new MatchRecord2CompositeSink(fileName, TEXT_SUFFIX,
				MAX_FILE_SIZE);
	}

	@SuppressWarnings("rawtypes")
	public static IMatchRecord2Source getCompositeTransMatchSource(BatchJob job) {
		String fileName = getCompositeTransMatchFileName(job);
		return new MatchRecord2CompositeSource(fileName, TEXT_SUFFIX);
	}

	/**
	 * Block factory for transitivity
	 */
	public static BlockSinkSourceFactory getTransitivityBlockFactory(
			BatchJob job) {
		String wd = getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, "transBlocks", "dat");
	}

	public static String getGroupResultFileName(BatchJob job) {
		String wd = getWorkingDir(job);
		assert wd.endsWith(FILE_SEPARATOR);
		String id = formatJobId(job.getId());
		String retVal =
			wd + BASENAME_TRANSMATCH_STORE_INDEXED + id
					+ NAMESTEM_TRANSANALYSIS;
		return retVal;
	}

	protected TransitivityFileUtils() {
	}

}
