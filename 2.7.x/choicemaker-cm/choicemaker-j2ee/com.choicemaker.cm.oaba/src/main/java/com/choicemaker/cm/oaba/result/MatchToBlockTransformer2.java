/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.result;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.oaba.core.BlockSet;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IMatchRecord2SinkSourceFactory;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.utils.MemoryEstimator;
import com.choicemaker.util.IntArrayList;
import com.choicemaker.util.LongArrayList;

/**
 * This object reads in an IMatchRecord2Source, creates equivalence classes, and
 * outputs them as blocks to a IBlockSink. <br/>
 * In this version, we use SetJoiner instead of EquivalenceClassBuilder.
 * SetJoiner is faster and more efficient, but it only works on ids that are
 * sequential. We have to use the translator to map record ids into internal
 * ids.
 *
 * @author pcheung
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class MatchToBlockTransformer2 {

	private static final Logger log = Logger
			.getLogger(MatchToBlockTransformer2.class.getName());

	private IMatchRecord2Source mSource;
	private IMatchRecord2SinkSourceFactory mFactory;
	private IBlockSink blockSink;
	private ImmutableRecordIdTranslator translator;
	private IRecordIdSink idSink;

	// this is the number of records in the translator file.
	private int numRecords = 0;

	// this is the number of records needed in chunking.
	private int numChunkRecords = 0;

	// max size of an EC
	private int max = 0;

	/**
	 * This constructor takes these parameters.
	 *
	 * @param mSource
	 *            - match record pairs source
	 * @param mFactory
	 *            - MatchRecord2 factory that provides temporary work files.
	 * @param translator
	 *            - a mapping between record id and internal id
	 * @param blockSink
	 *            - a block sink to store the equivalence classes
	 * @param idSink
	 *            - an id sink to hold the record ids of the size 2 equivalence
	 *            classes.
	 */
	public MatchToBlockTransformer2(IMatchRecord2Source mSource,
			IMatchRecord2SinkSourceFactory mFactory,
			ImmutableRecordIdTranslator translator, IBlockSink blockSink,
			IRecordIdSink idSink) {

		this.mSource = mSource;
		this.mFactory = mFactory;
		this.blockSink = blockSink;
		this.translator = translator;
		this.idSink = idSink;
	}

	/**
	 * This method does the following:
	 *
	 * 1. Transform the MatchRecord2 file into internal ids using the
	 * translator. 2. Perform set find and union using SetJoiner. 3. Output the
	 * sets as blocks.
	 *
	 * @return int - the number of distinct records in the MatchRecord2 source.
	 * @throws BlockingException
	 */
	public int process() throws BlockingException {
		long t = System.currentTimeMillis();

		numChunkRecords = 0;

		IMatchRecord2Sink sink = translate();
		int[] roots = unionFind(sink);
		writeBlocks(roots);

		t = System.currentTimeMillis() - t;
		log.info("time: " + t);

		return numChunkRecords;
	}

	/**
	 * This method translates the MatchRecord2 source with record ids into a
	 * MatchRecord2 sink with internal ids.
	 */
	private IMatchRecord2Sink translate() throws BlockingException {

		HashMap stageIDs = null;
		HashMap masterIDs = null;

		// get the staging id mapping
		List list = translator.getList1();
		assert list != null;
		int size = list.size();
		stageIDs = new HashMap(size);
		for (int i = 0; i < size; i++) {
			Comparable c = (Comparable) list.get(i);
			if (!stageIDs.containsKey(c))
				stageIDs.put(c, new Integer(i));
		}
		numRecords = size;

		int sizeStage = size;

		// get the master id mapping
		list = translator.getList2();
		if (list != null) {
			size = list.size();
			masterIDs = new HashMap(size);
			for (int i = 0; i < size; i++) {
				Comparable c = (Comparable) list.get(i);
				if (!masterIDs.containsKey(c)) {
					masterIDs.put(c, new Integer(i + sizeStage));
				}
			}
			numRecords += size;
		} else {
			masterIDs = new HashMap(1);
		}

		log.fine("stage size " + stageIDs.size());
		log.fine("master size " + masterIDs.size());

		IMatchRecord2Sink retVal = null;
		try {
			retVal = mFactory.getNextSink();
			mSource.open();
			retVal.open();

			// now write out the translated MatchRecord2
			MatchRecord2<?> mr = null;
			while (mSource.hasNext()) {
				mr = (MatchRecord2) mSource.next();

				Integer I1 = (Integer) stageIDs.get(mr.getRecordID1());
				Integer I2 = null;
				if (mr.getRecord2Role() == RECORD_SOURCE_ROLE.STAGING) {
					I2 = (Integer) stageIDs.get(mr.getRecordID2());
				} else {
					I2 = (Integer) masterIDs.get(mr.getRecordID2());
				}

				if (I1 == null || I2 == null) {
					throw new BlockingException("Could not translate "
							+ mr.getRecordID1().toString() + " and "
							+ mr.getRecordID2().toString());
				}

				final String noteInfo = mr.getNotesAsDelimitedString();
				MatchRecord2<Integer> mr2 =
					new MatchRecord2(I1, I2, mr.getRecord2Role(),
							mr.getProbability(), mr.getMatchType(), noteInfo);

				retVal.writeMatch(mr2);
			}

		} finally {
			if (mSource != null) {
				mSource.close();
			}
			if (retVal != null) {
				retVal.close();
			}
			stageIDs = null;
			masterIDs = null;
		}

		return retVal;
	}

	/**
	 * This method reads the MatchRecord2 source with internal ids and groups
	 * the ids into related sets. It returns a int [] indicating the set to
	 * which an id belongs.
	 *
	 * @param sink
	 *            - sink containing MatchRecord2 with internal ids
	 * @return int [] - this indicated the set to which the id belongs
	 * @throws BlockingException
	 */
	public int[] unionFind(IMatchRecord2Sink sink) throws BlockingException {
		IMatchRecord2Source source = mFactory.getSource(sink);

		SetJoiner setJoiner = new SetJoiner(numRecords);

		source.open();
		while (source.hasNext()) {
			MatchRecord2 mr = (MatchRecord2) source.next();
			Integer I1 = (Integer) mr.getRecordID1();
			Integer I2 = (Integer) mr.getRecordID2();
			setJoiner.union(I1.intValue(), I2.intValue());
		}
		source.close();

		mFactory.removeSource(source);

		int[] roots = setJoiner.flatten();
		return roots;
	}

	/**
	 * This method writes out the sets out to the blocks file.
	 *
	 * @param roots
	 *            - this indicated the set to which the id belongs
	 * @throws BlockingException
	 */
	private void writeBlocks(int[] roots) throws BlockingException {
		InMemorySetIterator sit = new InMemorySetIterator(roots);

		int count = 0;
		int twos = 0;
		int compares = 0;

		boolean init = true;

		blockSink.open();
		idSink.open();

		while (sit.hasNext()) {
			count++;

			IntArrayList set = sit.getNext();
			int size = set.size();

			// only keep those sets with more than 2 records
			if (size > 2) {
				numChunkRecords += size;
				if (size > max)
					max = size;
				compares += size * (size - 1) / 2;

				BlockSet bs = new BlockSet(0);
				LongArrayList list = new LongArrayList(size);

				for (int i = 0; i < size; i++) {
					list.add(set.get(i));
				}
				bs.setRecordIDs(list);

				blockSink.writeBlock(bs);
			} else if (size == 2) {
				twos++;
				Comparable C = translator.reverseLookup(set.get(0));

				if (init) {
					idSink.setRecordIDType(RECORD_ID_TYPE.fromInstance(C));
					init = false;
				}

				idSink.writeRecordID(C);
				C = translator.reverseLookup(set.get(1));
				idSink.writeRecordID(C);
			}
		}

		idSink.close();
		blockSink.close();

		log.info("total number of equivalence classes: " + count);
		log.info("max size of an equivalence class: " + max);
		log.info("number of size two equivalence classes: " + twos);
		log.info("total number of records required: " + numChunkRecords);
		log.info("total number of comparisons required: " + compares);

		MemoryEstimator.writeMem();

	}

	/**
	 * This returns the maximum size of an equivalence class.
	 *
	 * @return int - max size of an EC
	 */
	public int getMaxEC() {
		return max;
	}

}
