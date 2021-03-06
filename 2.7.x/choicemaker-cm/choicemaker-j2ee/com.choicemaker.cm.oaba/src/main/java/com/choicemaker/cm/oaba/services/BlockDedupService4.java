/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.services;

import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.CONTROL_INTERVAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.IControl;
import com.choicemaker.cm.oaba.core.BlockSet;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IBlockSource;
import com.choicemaker.cm.oaba.core.ISuffixTreeSink;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.core.OabaProcessingConstants;
import com.choicemaker.cm.oaba.core.SuffixTreeNode;
import com.choicemaker.cm.oaba.data.BlockGroupWalker;
import com.choicemaker.cm.oaba.impl.BlockGroup;
import com.choicemaker.cm.oaba.utils.ControlChecker;
import com.choicemaker.cm.oaba.utils.MemoryEstimator;
import com.choicemaker.cm.oaba.utils.SuffixTreeUtils;
import com.choicemaker.util.IntArrayList;
import com.choicemaker.util.LongArrayList;

/**
 *
 * This service object dedups a BlockGroup.
 * 
 * 3/30/05 - This version dedupes the big blocks and then use that to dedupe the
 * smaller blocks. It splits the BlockGroup into interval and dedups the big
 * interval first. If there is a reset within an interval, it saves the smaller
 * blocks to a temp file and dedups the bigs blocks. It then reads in the
 * smaller blocks and use the deduped big blocks to remove duplicate small
 * blocks.
 *
 * 4/12/2005 - This version saves the output as a tree instead of a array.
 * 
 * @author pcheung
 * 
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class BlockDedupService4 {

	private static final Logger log =
		Logger.getLogger(BlockDedupService4.class.getName());

	public final static float FULL = .6f;

	// this indicates how many block files to process at once.
	public int INTERVAL;

	// This variable tells the program whether or not to remove temporary files.
	private boolean removeFiles = true;

	// this object tells the program when to stop
	private final IControl control;

	// BlockGroup that contains duplicate blocks
	private final BlockGroup bGroup;

	// Factory to produce temporary sinks
	private final IBlockSinkSourceFactory bFactory;

	// Factory to produce temporary block sinks to store resets within an
	// interval
	private final IBlockSinkSourceFactory biFactory;

	// Output suffix tree sink that stores dedup-blocks
	private final ISuffixTreeSink sSink;

	// maximum block size
	// private final int maxBlockSize;

	private final ProcessingEventLog status;

	private final UserTransaction userTx;

	private int numBlocksIn = 0;
	private int numBlocksOut = 0;
	private int numTreesOut = 0;
	private int numReset = 0;

	private long time; // this keeps track of time

	private boolean stop = false;

	/**
	 * This constructor takes these parameters
	 * 
	 * @param bGroup
	 *            - input BlockGroup that contains dups
	 * @param bFactory
	 *            - factory to create temporary block sinks.
	 * @param biFactory
	 *            - factory to create temporary block sinks when there is an
	 *            reset within an interval.
	 * @param maxBlockSize
	 *            - maximum size of the blocks
	 */
	public BlockDedupService4(BlockGroup bGroup,
			IBlockSinkSourceFactory bFactory, IBlockSinkSourceFactory biFactory,
			ISuffixTreeSink sSink, int maxBlockSize, ProcessingEventLog status,
			IControl control, int interval, UserTransaction tx) {

		this.bGroup = bGroup;
		this.bFactory = bFactory;
		this.biFactory = biFactory;
		this.sSink = sSink;
		this.status = status;
		this.control = control;
		this.INTERVAL = interval;
		this.userTx = tx;

		this.stop = false;
	}

	public int getNumBlocksIn() {
		return numBlocksIn;
	}

	public int getNumBlocksOut() {
		return numBlocksOut;
	}

	public int getNumTreesOut() {
		return numTreesOut;
	}

	/**
	 * This method checks the current status and decides what to do next.
	 * 
	 * @throws IOException
	 */
	public void runService() throws BlockingException {

		time = System.currentTimeMillis();

		if (status
				.getCurrentProcessingEventId() >= OabaProcessingConstants.EVT_DONE_DEDUP_BLOCKS) {
			// do nothing here

		} else if (status
				.getCurrentProcessingEventId() == OabaProcessingConstants.EVT_DONE_OVERSIZED_TRIMMING) {
			// start deduping the blocks
			log.info("starting to dedup blocks");

			// open sink
			sSink.open();
			startDedup2(0);
			sSink.close();

		} else if (status
				.getCurrentProcessingEventId() == OabaProcessingConstants.EVT_DEDUP_BLOCKS) {
			// the +1 is needed because we need to start with the next file
			int startPoint =
				Integer.parseInt(status.getCurrentProcessingEventInfo());

			// start recovery
			log.info("starting dedup recovery, starting point: " + startPoint);

			// open sink
			sSink.append();
			startDedup2(startPoint);
			sSink.close();
		}

		time = System.currentTimeMillis() - time;

	}

	/**
	 * This method returns the time it takes to run the runService method.
	 * 
	 * @return long - returns the time (in milliseconds) it took to run this
	 *         service.
	 */
	public long getTimeElapsed() {
		return time;
	}

	/**
	 * In this version, for every interval, if there is a reset in the interval,
	 * dedup the big blocks first and then use that to dedup the smaller blocks.
	 * 
	 * @param startPoint
	 * @throws BlockingException
	 */
	private void startDedup2(int startPoint) throws BlockingException {
		IBlockSource[] parts = bGroup.getSources();

		// this is a list of big blocks previously deduped
		ArrayList bigBlocks = new ArrayList();

		// recovery in case of continuing in the middle
		BlockGroupWalker bgw = new BlockGroupWalker(INTERVAL, parts.length);
		for (int i = 0; i < startPoint; i++) {
			bgw.getNextPair();
			bigBlocks.add(bFactory.getNextSink());
		}

		// this counts the number of times through the intervals
		int count = 0;

		int[] pair = bgw.getNextPair();
		while (pair[0] > 0 && !stop) {

			stop = control.shouldStop();

			log.fine("pair " + pair[0] + " " + pair[1]);

			// Initialize
			SuffixTreeNode root = SuffixTreeNode.createRootNode();
			IntArrayList subsumedBlockSets = new IntArrayList();

			// This is a stack to store partial interval
			ArrayList stack = new ArrayList();

			// read the files in size ascending order.
			int blockSetId = 0;

			// this is needed for writing out distint blocks
			ArrayList sources = new ArrayList();

			int ret = runDedup(parts, pair[0], pair[1], sources, blockSetId,
					subsumedBlockSets, root);

			while (ret < pair[1] - 1 && !stop) {
				// this happens when there is a reset within an interval

				stop = control.shouldStop();

				// save the tree to a temp file
				IBlockSink tempSink = biFactory.getNextSink();
				log.fine("Saving temporarily to " + tempSink.getInfo());
				tempSink.open();
				writeUnsubsumedTemp(subsumedBlockSets, tempSink, sources);
				tempSink.close();
				stack.add(tempSink);

				// reset
				blockSetId = 0;
				root = SuffixTreeNode.createRootNode();
				subsumedBlockSets = new IntArrayList();
				sources = new ArrayList();

				ret = runDedup(parts, ret + 2, pair[1], sources, blockSetId,
						subsumedBlockSets, root);
			} // end while ret

			// compare to bigger deduped blocks
			compareToBig(root, subsumedBlockSets, bigBlocks);

			// write out to temp sink and sSink
			IBlockSink tempSink = bFactory.getNextSink();
			tempSink.open();
			numBlocksOut +=
				writeUnsubsumedTemp(subsumedBlockSets, tempSink, sources);
			tempSink.close();
			bigBlocks.add(tempSink);

			writeTree(root, sSink);

			// free memory
			root = null;
			System.gc();

			// dedup the temp files created from resets within the interval
			subsumedBlockSets = new IntArrayList();
			for (int i = stack.size() - 1; i >= 0 && !stop; i--) {
				stop = control.shouldStop();

				IBlockSource source =
					biFactory.getSource((IBlockSink) stack.get(i));

				// read from the stack block source and build a suffix tree
				root = readFromFile(source);

				// compare to big blocks
				compareToBig(root, subsumedBlockSets, bigBlocks);

				// write to bSink and add this to big block
				sources = new ArrayList();
				sources.add(source);
				tempSink = bFactory.getNextSink();
				tempSink.open();
				numBlocksOut +=
					writeUnsubsumedTemp(subsumedBlockSets, tempSink, sources);
				tempSink.close();
				bigBlocks.add(tempSink);

				writeTree(root, sSink);

				// reset and free memory
				subsumedBlockSets = new IntArrayList();
				log.fine(("removing " + source.getInfo()));
				source.delete();
				root = null;
				System.gc();
			}

			/*
			 * //debug if (log.isLoggable(Level.FINE)) { ArrayList children =
			 * root.getAllChildren(); for (int i=0; i<children.size(); i++) {
			 * SuffixTreeNode kid = (SuffixTreeNode) children.get(i);
			 * log.fine(kid.writeSuffixTree2(i + ":")); } }
			 */
			// reset
			subsumedBlockSets = null;
			sources = null;

			blockSetId = 0;
			root = SuffixTreeNode.createRootNode();
			subsumedBlockSets = new IntArrayList();
			sources = new ArrayList();

			pair = bgw.getNextPair();

			setStatusEvent(OabaEventBean.DEDUP_BLOCKS,
					Integer.toString(count));
			count++;

			// clean up
			root = null;
			subsumedBlockSets = null;
			sources = null;

		} // end while pair

		if (!stop) {
			setStatusEvent(OabaEventBean.DONE_DEDUP_BLOCKS);
			log.info("Number of reset " + numReset);

			bGroup.remove();

			if (this.removeFiles) {
				for (int i = 0; i < bigBlocks.size(); i++) {
					IBlockSink sink = (IBlockSink) bigBlocks.get(i);
					sink.remove();
				} // end for
			}
		}

	}

	/**
	 * This method dedups block files from parts[from] to parts[to]. It tries to
	 * read and dedup all the files from "from" and "to", but it will stop when
	 * the memory usuage gets to 60 percent.
	 * 
	 * @param parts
	 * @param from
	 * @param to
	 * @param bigBlocks
	 * @return int - returns value of "to" - 1 if success. Or the ind after
	 *         which the memory usage hits 60%.
	 */
	private int runDedup(IBlockSource[] parts, int from, int to,
			ArrayList sources, int blockSetId, IntArrayList subsumedBlockSets,
			SuffixTreeNode root) throws BlockingException {

		int ret = from - 1;

		int c = 0;

		while (ret < to && !stop) {
			IBlockSource source = parts[ret];
			log.fine("deduping file: " + source.getInfo() + " i " + ret);

			if (source.exists()) {
				sources.add(source);
				source.open();

				while (source.hasNext() && !stop) {
					stop = ControlChecker.checkStop(control, ++c,
							CONTROL_INTERVAL);

					numBlocksIn++;

					BlockSet blockSet = source.next();
					LongArrayList recordIds = blockSet.getRecordIDs();

					checkForSubsets(root, recordIds, 0, subsumedBlockSets);
					SuffixTreeUtils.addBlockSet(root, recordIds, blockSetId);

					blockSetId++;
				}

				source.close();

				if (MemoryEstimator.isFull(FULL) || ret == to - 1) {

					MemoryEstimator.writeMem();
					log.info("Resetting " + subsumedBlockSets.size() + " file "
							+ source.getInfo());
					numReset++;
					return ret;
				} // end if

			} // end if

			ret++;
		} // end while

		return ret;
	}

	/**
	 * This method reads in a block file that has already been dedup into a
	 * suffix tree.
	 */
	private SuffixTreeNode readFromFile(IBlockSource bSource)
			throws BlockingException {
		log.fine("Reading deduped file: " + bSource.getInfo());

		SuffixTreeNode root = SuffixTreeNode.createRootNode();
		bSource.open();

		int c = 0;

		int count = 0;
		while (bSource.hasNext() && !stop) {
			stop = ControlChecker.checkStop(control, ++c, CONTROL_INTERVAL);

			BlockSet blockSet = bSource.next();
			LongArrayList recordIds = blockSet.getRecordIDs();
			SuffixTreeUtils.addBlockSet(root, recordIds, count);
			count++;
		}
		bSource.close();
		log.fine("count " + count);
		return root;
	}

	/**
	 * This method uses the previously deduped bigger blocks to remove nodes
	 * from the tree.
	 * 
	 * @param root
	 * @param subsumedBlockSets
	 * @param bigBlocks
	 * @throws BlockingException
	 */
	private void compareToBig(SuffixTreeNode root,
			IntArrayList subsumedBlockSets, ArrayList bigBlocks)
			throws BlockingException {

		int c = 0;

		for (int i = 0; i < bigBlocks.size() && !stop; i++) {
			IBlockSink sink = (IBlockSink) bigBlocks.get(i);
			IBlockSource bigSource = bFactory.getSource(sink);

			log.fine("CompareToBig " + bigSource.getInfo());

			bigSource.open();

			while (bigSource.hasNext() && !stop) {
				stop = ControlChecker.checkStop(control, ++c, CONTROL_INTERVAL);

				BlockSet bs = bigSource.next();

				LongArrayList recordIds = bs.getRecordIDs();

				// remove blocks from tree that are contained with larger
				// blocks.
				checkForSubsets(root, recordIds, 0, subsumedBlockSets);
			}
			bigSource.close();
		}
	}

	/**
	 * This method write the distinct blocks to sink. A block is distinct if
	 * it's not in the subsumed set.
	 * 
	 * @param subsumedBlockSets
	 *            - the block ids of duplicates
	 * @param sink
	 * @param sources
	 * @throws BlockingException
	 */
	private int writeUnsubsumedTemp(IntArrayList subsumedBlockSets,
			IBlockSink sink, ArrayList sources) throws BlockingException {

		// This has to be sorted
		subsumedBlockSets.sort();

		int counter = 0; // counter for the blocks read
		int ind = 0; // current index on subsumedBlockSets
		int count = 0; // number of blocks written

		int c = 0;

		for (int i = 0; i < sources.size() && !stop; i++) {
			IBlockSource srcI = (IBlockSource) sources.get(i);

			if (srcI.exists()) {
				srcI.open();

				while (srcI.hasNext() && !stop) {
					stop = ControlChecker.checkStop(control, ++c,
							CONTROL_INTERVAL);

					BlockSet bs = srcI.next();

					// don't write anything in subsumedBlocks
					if (ind < subsumedBlockSets.size()
							&& counter == subsumedBlockSets.get(ind)) {
						ind++;
					} else {
						sink.writeBlock(bs);
						count++;
					}

					counter++;
				}

				srcI.close();

			}

		} // end for

		// at the end, ind should be the size of the subsumedsubset
		if (!stop && ind != subsumedBlockSets.size())
			throw new IllegalStateException("Done writeUnsubsumed4 ind " + ind
					+ " size " + subsumedBlockSets.size());

		return count;
	}

	/**
	 * This writes the suffix tree to the sink. Root should have id of -1.
	 * 
	 * @param subsumedBlockSets
	 * @param sink
	 * @param sink2
	 * @param sources
	 * @throws BlockingException
	 */
	private void writeTree(SuffixTreeNode root, ISuffixTreeSink sink2)
			throws BlockingException {
		if (root.getRecordId() != -1) {
			throw new BlockingException(
					"Invalid root id " + root.getRecordId());
		}

		List branches = root.getAllChildren();

		if (branches != null) {
			for (int i = 0; i < branches.size(); i++) {
				numTreesOut++;
				SuffixTreeNode branch = (SuffixTreeNode) branches.get(i);
				sink2.writeSuffixTree(branch);

			} // end for
		}
	}

	/**
	 * This method checks for subset recursively.
	 * 
	 * @param node
	 *            - starting node of the suffix tree
	 * @param recordIds
	 *            - records IDs to check
	 * @param fromIndex
	 *            - which subsets to check
	 * @param subsumedSets
	 *            - array of subsumed block set IDs
	 */
	private static void checkForSubsets(SuffixTreeNode node,
			LongArrayList recordIds, int fromIndex, IntArrayList subsumedSets) {

		for (int i = fromIndex, n = recordIds.size(); i < n; i++) {
			long recordId = recordIds.get(i);
			SuffixTreeNode kid = node.getChild(recordId);
			if (kid != null) {
				if (kid.hasBlockingSetId()) { // the kid represents an (as yet)
												// unsubsumed blocking set.
					subsumedSets.add(kid.getBlockingSetId());
					kid.removeFromParentRecursive();
				} else {
					checkForSubsets(kid, recordIds, i + 1, subsumedSets);
				}
			}
		}
	}

	private void setStatusEvent(ProcessingEvent evt) throws BlockingException {
		setStatusEvent(evt, null);
	}

	private void setStatusEvent(ProcessingEvent evt, String info)
			throws BlockingException {
		assert evt != null;
		try {
			userTx.begin();
			status.setCurrentProcessingEvent(evt, info);
			userTx.commit();
		} catch (NotSupportedException | SystemException | SecurityException
				| IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException e) {
			String msg0 =
				"Failed to log processing status [evt[%s], info[%s]]. "
						+ "Cause: %s";
			String msg = String.format(msg0, evt, info, e.toString());
			log.severe(msg);
			throw new BlockingException(msg);
		}
	}

}
