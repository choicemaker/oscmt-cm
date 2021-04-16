/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.BINARY_SUFFIX;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.FILE_SEPARATOR;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.TEXT_SUFFIX;
import static com.choicemaker.cm.batch.ejb.BatchJobFileUtils.formatJobId;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.ejb.BatchJobFileUtils;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.core.IComparisonArraySource;
import com.choicemaker.cm.oaba.core.IComparisonSetSource;
import com.choicemaker.cm.oaba.core.IComparisonTreeSource;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.core.IndexedFileObserver;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.impl.BlockSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ChunkDataSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ChunkRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ComparisonArrayGroupSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ComparisonArraySinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ComparisonSetOSSource;
import com.choicemaker.cm.oaba.impl.ComparisonTreeGroupSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ComparisonTreeSetSource;
import com.choicemaker.cm.oaba.impl.ComparisonTreeSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.IDTreeSetSource;
import com.choicemaker.cm.oaba.impl.MatchRecord2CompositeSink;
import com.choicemaker.cm.oaba.impl.MatchRecord2CompositeSource;
import com.choicemaker.cm.oaba.impl.MatchRecord2SinkSourceFactory;
import com.choicemaker.cm.oaba.impl.RecValSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.SuffixTreeSink;
import com.choicemaker.cm.oaba.impl.SuffixTreeSource;
import com.choicemaker.util.Precondition;

/**
 * This object configures factory objects for Batch jobs.
 * 
 * @author pcheung
 *
 */
public class OabaFileUtils {

	public static final String BASENAME_BIG_BLOCK_STORE = "bigBlocks";

	public static final String BASENAME_BLOCK_STORE = "blocks";

	public static final String BASENAME_BLOCKGROUP_STORE = "blockGroup";

	public static final String BASENAME_CHUNKMASTER_ROW_STORE =
		"chunkmasterrow";

	public static final String BASENAME_CHUNKROW_STORE = "chunkrow";

	public static final String BASENAME_CHUNKSTAGE_ROW_STORE = "chunkstagerow";

	public static final String BASENAME_COMPARE_ARRAY_GROUP =
		"compareArrayGroup_O";

	public static final String BASENAME_COMPARE_ARRAY_STORE = "compareArray_O";

	public static final String BASENAME_COMPARE_TREE_GROUP_STORE =
		"compareTreeGroup";

	public static final String BASENAME_COMPARE_TREE_STORE = "compareTree";

	public static final String BASENAME_COMPAREGROUP_STORE = "compareGroup";

	public static final String BASENAME_MATCH_STORE_INDEXED = "match_";

	public static final String BASENAME_MATCH_TEMP_STORE = "matchtemp";

	public static final String BASENAME_MATCH_TEMP_STORE_INDEXED = "matchtemp_";

	public static final String BASENAME_MATCHCHUNK_STORE = "matchchunk";

	public static final String BASENAME_OVERSIZED_GROUP_STORE =
		"oversizedGroup";

	public static final String BASENAME_OVERSIZED_STORE = "oversized";

	public static final String BASENAME_OVERSIZED_TEMP_STORE = "oversizedTemp";

	public static final String BASENAME_RECVAL_STORE = "btemp";

	public static final String BASENAME_TEMP_BLOCK_STORE = "tempBlocks";

	public static final String BASENAME_TWOMATCH_STORE = "twomatch";

	public static final String FILENAME_TREE_STORE = "trees.txt";

	public static BlockSinkSourceFactory getBigBlocksSinkSourceFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_BIG_BLOCK_STORE,
				BINARY_SUFFIX);
	}

	public static BlockSinkSourceFactory getBlockFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_BLOCK_STORE,
				BINARY_SUFFIX);
	}

	public static BlockSinkSourceFactory getBlockGroupFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_BLOCKGROUP_STORE,
				BINARY_SUFFIX);
	}

	public static ComparisonArraySinkSourceFactory getCGFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ComparisonArraySinkSourceFactory(wd,
				BASENAME_COMPAREGROUP_STORE, BINARY_SUFFIX);
	}

	public static ChunkRecordIdSinkSourceFactory getChunkIDFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ChunkRecordIdSinkSourceFactory(wd, BASENAME_CHUNKROW_STORE,
				BINARY_SUFFIX);
	}

	public static ComparisonArraySinkSourceFactory getComparisonArrayFactoryOS(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ComparisonArraySinkSourceFactory(wd,
				BASENAME_COMPARE_ARRAY_STORE, BINARY_SUFFIX);
	}

	/**
	 * This is used by the parallelization code. It creates many array files for
	 * each chunk.
	 */
	public static ComparisonArrayGroupSinkSourceFactory getComparisonArrayGroupFactoryOS(
			BatchJob job, int num) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ComparisonArrayGroupSinkSourceFactory(wd,
				BASENAME_COMPARE_ARRAY_GROUP, BINARY_SUFFIX, num);
	}

	@SuppressWarnings({
			"unchecked", "rawtypes" })
	public static IComparisonSetSource getComparisonSetSource(BatchJob job,
			final int currentChunk, final int treeIndex,
			final RECORD_ID_TYPE recordIdType, final int numRegularChunks,
			final int numProcessors, final int maxBlockSize)
			throws BlockingException {
		IComparisonSetSource retVal;
		if (currentChunk < numRegularChunks) {
			// regular-sized comparison sets
			ComparisonTreeGroupSinkSourceFactory factory =
				getComparisonTreeGroupFactory(job, recordIdType, numProcessors);
			IComparisonTreeSource source =
				factory.getSource(currentChunk, treeIndex);
			if (source.exists()) {
				retVal = new ComparisonTreeSetSource(source);
			} else {
				throw new BlockingException(
						"Could not get regular source " + source.getInfo());
			}
		} else {
			// over-sized comparison sets
			int i = currentChunk - numRegularChunks;
			ComparisonArrayGroupSinkSourceFactory factoryOS =
				getComparisonArrayGroupFactoryOS(job, numProcessors);
			IComparisonArraySource sourceOS = factoryOS.getSource(i, treeIndex);
			if (sourceOS.exists()) {
				retVal = new ComparisonSetOSSource(sourceOS, maxBlockSize);
			} else {
				throw new BlockingException(
						"Could not get oversized source " + sourceOS.getInfo());
			}
		}
		return retVal;
	}

	public static ComparisonTreeSinkSourceFactory getComparisonTreeFactory(
			BatchJob job, RECORD_ID_TYPE stageType) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ComparisonTreeSinkSourceFactory(wd,
				BASENAME_COMPARE_TREE_STORE, TEXT_SUFFIX, stageType);
	}

	/**
	 * This is used by the parallelization code. It creates many tree files for
	 * each chunk.
	 */
	public static ComparisonTreeGroupSinkSourceFactory getComparisonTreeGroupFactory(
			BatchJob job, RECORD_ID_TYPE stageType, int num) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ComparisonTreeGroupSinkSourceFactory(wd,
				BASENAME_COMPARE_TREE_GROUP_STORE, TEXT_SUFFIX, num, stageType);
	}

	protected static String getCompositeMatchFileName(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		assert wd.endsWith(FILE_SEPARATOR);
		String id = formatJobId(job.getId());
		String retVal = wd + BASENAME_MATCH_STORE_INDEXED + id;
		return retVal;
	}

	/**
	 * This returns the final sink in which to store the result of the OABA. The
	 * file size is limited to <code>maxFileSize</code>. The file name is [file
	 * dir]/match_[job id]_*.txt.
	 *
	 * @return IMatchRecord2Sink - the sink to store the OABA output.
	 */
	@SuppressWarnings("rawtypes")
	public static IMatchRecord2Sink getCompositeMatchSink(BatchJob job,
			int maxFileSize, IndexedFileObserver ifo) {
		Precondition.assertBoolean(maxFileSize > 0);
		String fileName = getCompositeMatchFileName(job);
		return new MatchRecord2CompositeSink(fileName, TEXT_SUFFIX, maxFileSize,
				ifo);
	}

	/**
	 * This returns the source handle to the OABA result.
	 */
	@SuppressWarnings("rawtypes")
	public static IMatchRecord2Source getCompositeMatchSource(BatchJob job) {
		String fileName = getCompositeMatchFileName(job);
		return new MatchRecord2CompositeSource(fileName, TEXT_SUFFIX);
	}

	public static ChunkDataSinkSourceFactory getMasterDataFactory(BatchJob job,
			ImmutableProbabilityModel model) {
		if (model == null) {
			throw new IllegalArgumentException("null modelId");
		}
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ChunkDataSinkSourceFactory(wd,
				BASENAME_CHUNKMASTER_ROW_STORE, model);
	}

	/**
	 * This gets the match result sink for each chunk of the Matcher Bean.
	 * 
	 */
	public static MatchRecord2SinkSourceFactory getMatchChunkFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new MatchRecord2SinkSourceFactory(wd, BASENAME_MATCHCHUNK_STORE,
				"txt");
	}

	public static MatchRecord2SinkSourceFactory getMatchTempFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new MatchRecord2SinkSourceFactory(wd, BASENAME_MATCH_TEMP_STORE,
				"txt");
	}

	public static MatchRecord2SinkSourceFactory getMatchTempFactory(
			BatchJob job, int i) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		String str = Integer.toString(i);
		String fileNameIndexed = BASENAME_MATCH_TEMP_STORE_INDEXED + str + "_";
		return new MatchRecord2SinkSourceFactory(wd, fileNameIndexed, "txt");
	}

	public static BlockSinkSourceFactory getOversizedFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_OVERSIZED_STORE,
				BINARY_SUFFIX);
	}

	public static BlockSinkSourceFactory getOversizedGroupFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_OVERSIZED_GROUP_STORE,
				BINARY_SUFFIX);
	}

	public static BlockSinkSourceFactory getOversizedTempFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_OVERSIZED_TEMP_STORE,
				BINARY_SUFFIX);
	}

	public static RecValSinkSourceFactory getRecValFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new RecValSinkSourceFactory(wd, BASENAME_RECVAL_STORE,
				BINARY_SUFFIX);
	}

	public static MatchRecord2SinkSourceFactory getSet2MatchFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new MatchRecord2SinkSourceFactory(wd, BASENAME_TWOMATCH_STORE,
				TEXT_SUFFIX);
	}

	public static ChunkDataSinkSourceFactory getStageDataFactory(BatchJob job,
			ImmutableProbabilityModel model) {
		if (model == null) {
			throw new IllegalArgumentException("null modelId");
		}
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new ChunkDataSinkSourceFactory(wd, BASENAME_CHUNKSTAGE_ROW_STORE,
				model);
	}

	public static SuffixTreeSink getSuffixTreeSink(BatchJob job) {
		String path = getTreeFilePath(job);
		return new SuffixTreeSink(path);
	}

	public static BlockSinkSourceFactory getTempBlocksSinkSourceFactory(
			BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new BlockSinkSourceFactory(wd, BASENAME_TEMP_BLOCK_STORE,
				BINARY_SUFFIX);
	}

	protected static String getTreeFilePath(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		assert wd.endsWith(FILE_SEPARATOR);
		String retVal = wd + FILENAME_TREE_STORE;
		return retVal;
	}

	public static IDTreeSetSource getTreeSetSource(BatchJob job) {
		String path = getTreeFilePath(job);
		SuffixTreeSource sSource = new SuffixTreeSource(path);
		IDTreeSetSource source = new IDTreeSetSource(sSource);
		return source;
	}

	protected OabaFileUtils() {
	}

}
