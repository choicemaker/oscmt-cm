/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.choicemaker.cm.batch.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.IControl;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkDataSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIdSink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIdSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet;
import com.choicemaker.cm.io.blocking.automated.offline.core.IIDSet;
import com.choicemaker.cm.io.blocking.automated.offline.core.IIDSetSource;
import com.choicemaker.cm.io.blocking.automated.offline.core.ITransformer;
import com.choicemaker.cm.io.blocking.automated.offline.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessingEvent;
import com.choicemaker.cm.io.blocking.automated.offline.utils.ControlChecker;
import com.choicemaker.util.LongArrayList;
import com.choicemaker.util.Precondition;

/**
 * This version takes in blocks that contains internal id instead of the record
 * id.
 * 
 * This service creates does the following: 1. Read in blocks and/or oversized
 * blocks to create chunk id files in internal ids. 2. Read in the record
 * source, translator and chunk id files to create chunk data files. 3. Create
 * comparing block groups.
 * 
 * This version is more abstracted. It takes in IISSetSource instead of
 * IBlockSource. It also uses transformers to write internal id arrays/trees to
 * record id arrays/trees.
 * 
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
	"rawtypes", "unchecked" })
zpublic class ChunkService3_MCI {

	/**
	 * The name of a system property that can be set to "true" to keep files
	 * used in intermediate computations. By default, intermediate files are
	 * removed once the chunk service has run.
	 */
	public static final String PN_KEEP_FILES = "oaba.ChunkService3.keepFiles";

	/**
	 * Checks the system property {@link #PN_KEEP_FILES} and caches the result
	 */
	private boolean isKeepFilesRequested() {
		String value = System.getProperty(PN_KEEP_FILES, "false");
		Boolean _keepFiles = Boolean.valueOf(value);
		boolean retVal = _keepFiles.booleanValue();
		return retVal;
	}

	private boolean keepFiles = isKeepFilesRequested();

	private static final String DELIM = "|";

	private static final Logger log = Logger.getLogger(ChunkService3.class
			.getName());

	protected static final String SOURCE = "ChunkService3";

	// FIXME make these manifest constants JMX properties
	/** Number of records between when checks of whether this job should stop */
	public static final int COUNT_RECORDS_BETWEEN_STOP_CHECKS = ControlChecker.CONTROL_INTERVAL;
	
	/** Number of records between when debug print of the current record id */
	public static final int COUNT_RECORDS_BETWEEN_DEBUG_PRINTS = 5000;
	
	/** Number of records between when info print of the current record id */
	public static final int COUNT_RECORDS_BETWEEN_INFO_PRINTS = 50000;
	// END FIXME
	
	private IIDSetSource bSource;
	private IIDSetSource osSource;
	private RecordSource stage;
	private RecordSource master;
	private ImmutableProbabilityModel model;

	// these two variables are used to stop the program in the middle
	private IControl control;
	private boolean stop;
	
	// Translates record ids into internal ids and vice-versa
	IRecordIDTranslator3 translator;

	// transformer for the regular blocks.
	private ITransformer transformer;

	// transformer for the oversized blocks.
	private ITransformer transformerO;
	
	private IChunkRecordIDSinkSourceFactory2 recIDFactory;
	private IChunkDataSinkSourceFactory stageSinkFactory;
	private IChunkDataSinkSourceFactory masterSinkFactory;

	private ProcessingEventLog status;
	private int maxChunkSize;

	private int splitIndex;

	private ArrayList recIDSinks = new ArrayList(); // list of chunk id sinks

	// private int totalBlocks = 0;
	private int numChunks = 0;
	private int maxFiles = 0;

	/**
	 * There are two types of chunks, regular and oversized.
	 * 
	 * <pre>
	 * numOS = numChunks - numRegularChunks;
	 * </pre>
	 * 
	 */
	private int numRegularChunks = 0;

	private long time; // this keeps track of time

	/**
	 * This version of the constructor takes in a block source and oversized
	 * block source.
	 * 
	 * @param bSource
	 *            - block source
	 * @param osSource
	 *            - oversized block source
	 * @param stage
	 *            - stage record source
	 * @param master
	 *            - master record source
	 * @param accessProvider
	 *            - probability accessProvider
	 * @param recIDFactory
	 *            - this factory creates chunk id files
	 * @param stageSinkFactory
	 *            - this factory creates chunk data files for the staging data
	 * @param masterSinkFactory
	 *            - this factory creates chunk data files for the master data
	 * @param splitIndex
	 *            - This indicates when the internal goes from stage to master/
	 * @param transformer
	 *            - ID transformer for the regular blocks
	 * @param transformerO
	 *            - ID transformer for the oversized blocks
	 * @param maxChunkSize
	 *            - maximum size of a chunk
	 * @param maxFiles
	 *            - maximum number of files to open.
	 * @param status
	 *            - status of the system
	 */
	public ChunkService3_MCI(IIDSetSource bSource, IIDSetSource osSource,
			RecordSource stage, RecordSource master,
			ImmutableProbabilityModel model,
			IChunkRecordIdSinkSourceFactory recIDFactory,
			IChunkDataSinkSourceFactory stageSinkFactory,
			IChunkDataSinkSourceFactory masterSinkFactory,
			ImmutableRecordIdTranslator translator, ITransformer transformer,
			ITransformer transformerO, int maxChunkSize, int maxFiles,
			ProcessingEventLog status, IControl control) {
			
		Precondition.assertNonNullArgument(bSource);
		Precondition.assertNonNullArgument(stage);
		Precondition.assertNonNullArgument(model);
		Precondition.assertNonNullArgument(recIDFactory);
		Precondition.assertNonNullArgument(stageSinkFactory);
		Precondition.assertNonNullArgument(translator);
		Precondition.assertNonNullArgument(transformer);
		Precondition.assertNonNullArgument(status);
		Precondition.assertNonNullArgument(control);

		this.bSource = bSource;
		this.osSource = osSource;
		this.stage = stage;
		this.master = master;
		this.model = model;
		this.transformer = transformer;
		this.transformerO = transformerO;
		this.recIDFactory = recIDFactory;
		this.stageSinkFactory = stageSinkFactory;
		this.masterSinkFactory = masterSinkFactory;
		this.maxChunkSize = maxChunkSize;
		this.maxFiles = maxFiles;
		this.status = status;
		this.translator = translator;
		this.splitIndex = translator.getSplitIndex();

		this.control = control;
		this.stop = false;
	}

	public int getNumChunks() {
		return numChunks;
	}

	public int getNumRegularChunks() {
		return numRegularChunks;
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
	 * This method runs the service.
	 * 
	 * @throws IOException
	 */
	public void runService() throws BlockingException {

		final String METHOD = "runService()";
		log.entering(SOURCE, METHOD);
		time = System.currentTimeMillis();

		if (status.getStatus() == IStatus.DONE_CREATE_CHUNK_DATA ) {
			//just need to recover numChunks for the matching step
			StringTokenizer temp = new StringTokenizer (status.getAdditionalInfo(),DELIM);
			numChunks = Integer.parseInt( temp.nextToken() );
			numRegularChunks = Integer.parseInt( temp.nextToken() );
			log.info("Recovery, numChunks " + numChunks + " numRegularChunks " + numRegularChunks);
			
		} else if (status.getStatus() == IStatus.DONE_DEDUP_OVERSIZED ) {
			//create ids
			log.info("Creating ids for block source " + bSource.getInfo());
			createIDs (bSource, false, 0, transformer);
			numRegularChunks = numChunks;
			
			if (osSource != null && osSource.exists()) {
				log.info("Creating ids for oversized block source " + osSource.getInfo());
				int count = createIDs (osSource, true, 0, transformerO);
				if (count == 0) {
					transformerO.cleanUp();
				}
			}

			if (!stop) {
				createDataFiles();
			}

		} else if (status.getCurrentProcessingEventId() == OabaProcessing.EVT_DONE_CREATE_CHUNK_IDS
				|| status.getCurrentProcessingEventId() == OabaProcessing.EVT_CREATE_CHUNK_OVERSIZED_IDS) {

		} else if (status.getStatus() == IStatus.DONE_CREATE_CHUNK_IDS ||
			status.getStatus() == IStatus.CREATE_CHUNK_OVERSIZED_IDS) {
				
			//create the chunk data files
			StringTokenizer temp = new StringTokenizer (status.getAdditionalInfo(),DELIM);
			numChunks = Integer.parseInt( temp.nextToken() );
			numRegularChunks = Integer.parseInt( temp.nextToken() );
			log.info("Recovery, numChunks " + numChunks + " numRegularChunks " + numRegularChunks);

			recoverCreateIDs (numChunks);

			createDataFiles ();
			
		} else if (status.getStatus() == IStatus.CREATE_CHUNK_IDS ) {
			//time to create Oversized ID files
			if (osSource != null && osSource.exists()) {
				log.info("Creating ids for oversized block source " + osSource.getInfo());
				int count = createIDs (osSource, true, 0, transformerO);
				if (count == 0) {
					transformerO.cleanUp();
				}
			}

			if (!stop) {
				createDataFiles();
			}

		}

		time = System.currentTimeMillis() - time;
	}

	/**
	 * This method makes sure that the program doesn't overwrite the existing
	 * files. It flushes the factories by calling getNext ().
	 *
	 */
	private void recoverCreateIDs(int numFiles) throws BlockingException {
		for (int i = 0; i < numFiles; i++) {
			recIDSinks.add(recIDFactory.getNextSink());
		}
	}

	/**
	 * This method creates the chunk data files for stage and master record
	 * sources.
	 * 
	 * @throws IOException
	 * @throws XmlConfException
	 */
	private void createDataFiles() throws BlockingException {
		try {
			// Index sets for each chunk data file
			IChunkRecordIndexSet [] crSets = new IChunkRecordIndexSet [numChunks];
				
			// A stage record sink for each chunk data file
			RecordSink [] stageRecordSinks = new RecordSink [numChunks];
			
			// A master record sink for each chunk data file
			RecordSink [] masterRecordSinks = new RecordSink [numChunks];
			
			//set up
			boolean isDebugIndexSets = log.isDebugEnabled();
			for (int i=0; i < numChunks; i++) {
				IChunkRecordIdSink recSink = (IChunkRecordIdSink) recIDSinks.get(i);
				crSets[i] = recIDFactory.getChunkRecordIndexSet(recSink,isDebugIndexSets);
				stageRecordSinks[i] = stageSinkFactory.getNextSink();
				masterRecordSinks[i] = masterSinkFactory.getNextSink();
			} //end for
			
			int start = 0;
			int end = maxFiles;

			if (numChunks <= maxFiles) {
				end = numChunks;
				boolean isStaging = true;
				createDataFiles (start, end, crSets, stageRecordSinks, isStaging, /* splitIndex, */ stage, stageModel);
				
				if (master != null) {
					isStaging = false;
					createDataFiles (start, end, crSets, masterRecordSinks, isStaging, /* splitIndex, */
					master, masterModel);
				} else {
					openMaster (masterRecordSinks);
				}
				
			} else {
				while (start < numChunks) {
					boolean isStaging = true;
					createDataFiles (start, end, crSets, stageRecordSinks, isStaging, /* splitIndex, */
					stage, stageModel);
					
					if (master != null) {
						isStaging = false;
						createDataFiles (start, end, crSets, masterRecordSinks, isStaging, /* splitIndex, */
						master, masterModel);
					} else {
						openMaster (masterRecordSinks);
					}

					start = end;
					end = end + maxFiles;
					if (end > numChunks)
						end = numChunks;
				}
			}
			
			if (!stop) {
				String temp =
					Integer.toString(numChunks) + DELIM
							+ Integer.toString(numRegularChunks);
				status.setCurrentProcessingEvent(OabaProcessingEvent.DONE_CREATE_CHUNK_DATA,
						temp);

				if (!keepFiles) {
					// remove all the chunk record id files
					for (int i = 0; i < numChunks; i++) {
						IChunkRecordIdSink recIDSink =
							(IChunkRecordIdSink) recIDSinks.get(i);
						recIDSink.remove();
					}
				}

				recIDSinks = null;
			}

		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}

	}

	/**
	 * This method just opens the sink so that a empty file will be created for
	 * the master sink. This is necessary because matching requires empty files.
	 * 
	 * @param masterRecordSinks
	 * @throws IOException
	 */
	private void openMaster(RecordSink[] masterRecordSinks) throws IOException {
		int s = masterRecordSinks.length;
		for (int i = 0; i < s; i++) {
			masterRecordSinks[i].open();
			masterRecordSinks[i].close();
		}
	}

	/**
	 * This method write out chunk data for elements in the arrays from start to
	 * end.
	 * 
	 * @param start
	 *            - The location in the array to start writing. Inclusive.
	 * @param end
	 *            - The location in the array to stop writing. Exclusive.
	 * @param crSources
	 *            - The array containing chunk record ids.
	 * @param recordSinks
	 *            - The record sink to which to write the data.
	 * @param ind
	 *            - The array the contains the current chunk record id.
	 * @param rs
	 *            - record source
	 * @param accessProvider
	 *            - ImmutableProbabilityModel of the record source.
	 * @throws BlockingException
	 * @throws XmlConfException
	 * @throws IOException
	 */
	private void createDataFiles(
		int start,
		int end,
		IChunkRecordIndexSet[] crSets,
		RecordSink[] recordSinks,
		boolean isStaging,
		/*int splitIndex, */
		RecordSource rs,
		ImmutableProbabilityModel model)
		throws BlockingException, XmlConfException, IOException {
			
		log.debug ("starting " + start + " ending " + end);

		//set up	
		for (int i=start; i < end; i++) {
			crSets[i].open();
			recordSinks[i].open();
		} //end for

		createDataFile (rs, model, start, end, isStaging, /* splitIndex, */ crSets, recordSinks);

		//close sinks and sources
		for (int i=start; i < end; i++) {
			recordSinks[i].close(); //close the chunk data sinks
			if (crSets[i].isDebugEnabled()) {
				logUncheckedIndices(crSets[i]);
			}
			crSets[i].close(); //close the record id sources
		}

	}
	
	private static int MAX_DEBUG_INDEX_NUMBER = 5;
	
	private static void logUncheckedIndices(IChunkRecordIndexSet indexSet)
		throws BlockingException {
		IChunkRecordIDSource src = indexSet.getUncheckedIndices();
		LongArrayList uncheckedList = new LongArrayList(MAX_DEBUG_INDEX_NUMBER);
		int count = 0;
		while (src.hasNext() ) {
			++ count;
			long uncheckedIndex = src.getNext();
			if(uncheckedList.size() < MAX_DEBUG_INDEX_NUMBER) {
				uncheckedList.add(uncheckedIndex);
			} else {
				if (count > uncheckedList.size()) {
					break;
				}
			}
		}
		int exampleSize = uncheckedList.size();
		// assert count >= exampleSize ;
		if (count>0) {
			StringBuffer sb =
				new StringBuffer("Some (");
			if (count > exampleSize) {
				sb.append("at least ");
			}
			sb.append(count).append(
					") record indices did not correspond to records from the database: ");
			for (int i=0; i<exampleSize; i++) {
				sb.append(uncheckedList.get(i));
				if (i<exampleSize - 1) {
					sb.append(", ");
				}
			}
			if (count > exampleSize) {
				sb.append(", ...");
			}
			String msg = sb.toString();
			log.debug(msg);
		}
	}

	/**
	 * This method creates the chunk data files from the chunk id files in the
	 * range.
	 * 
	 * @param rs
	 *            - the record source
	 * @param accessProvider
	 *            - the probability accessProvider
	 * @param start
	 *            - the chunk id file to start from
	 * @param end
	 *            - the chunk id file to end with, excluding itself
	 * @param offset
	 *            - This is offset of the internal id. Master file's offset is
	 *            the number stageng records. Stage file has offset of 0.
	 * @param ind
	 *            - array of current id in the chunk id file
	 * @param crSources
	 *            - array of chunk id files
	 * @param recordSinks
	 *            - chunk data files to which to write the data
	 * @throws BlockingException
	 * @throws XmlConfException
	 */
	private void createDataFile(
		RecordSource rs,
		ImmutableProbabilityModel model,
		int start,
		int end,
		boolean isStaging,
		/* int splitIndex, */
		IChunkRecordIndexSet[] crSets,
		RecordSink[] recordSinks)
		throws BlockingException, XmlConfException {
			
		final String METHOD = "createDataFile(..)";
		log.entering(SOURCE, METHOD, new Object[] {
				start, end, offset });
		assert rs != null;
		assert model != null;
		assert ind != null;
		assert crSources != null;
		assert recordSinks != null;

		String context = null;
		try {
			rs.setModel(model);
			rs.open();

			// Count intervals between log statements and stop checks				
			int count = /* splitIndex; */0;

			// read the source record and check each of the chunk index files
			while (rs.hasNext() && !stop) {
				Record r = rs.getNext();
				Comparable id = r.getId();
				int index;
				if (isStaging) {
					index = translator.lookupStagingIndex(id);
				} else {
					index = translator.lookupMasterIndex(id); 
				}
				if ((count % COUNT_RECORDS_BETWEEN_DEBUG_PRINTS == 0
					&& log.isDebugEnabled())
					|| (count % COUNT_RECORDS_BETWEEN_INFO_PRINTS == 0
						&& log.isInfoEnabled())) {
					String msg =
						"Record '" + id + "' / index '" + index + "'";
					if (log.isInfoEnabled()) {
						log.info(msg);
					} else {
						log.debug(msg);
					}
				}
				if (index == IRecordIDTranslator3.INVALID_INDEX) {
					log.warn("no internal id for record id '" + id + "'");
				} else {
					// For each chunk data file, check if this record belongs to it
					for (int i = start; i < end; i++) {
						// If the record index belongs to the chunk index set,
						// add the record itself to the chunk data file
						if (crSets[i].containsRecordIndex(index)) {
							recordSinks[i].put(r);
						}
					}
				}
				count ++;
				stop = ControlChecker.checkStop (control, count,COUNT_RECORDS_BETWEEN_STOP_CHECKS);					
			} //end while rs next

			//close source
			rs.close() ;

		} catch (IOException ex) {
			throw new BlockingException (ex.toString());
		}
	}

	/**
	 * This method creates the smaller block sink files and rec id files. These
	 * files correspond to a single chunk.
	 * 
	 * @param source
	 *            - block source
	 * @param isOS
	 *            - true if we are processing the oversized file
	 * @param skip
	 *            - number of blocks to skip
	 * @throws IOException
	 */
	private int createIDs(IIDSetSource source, boolean isOS, int skip,
			ITransformer transformer) throws BlockingException {
		// initialize the translator
		transformer.init();

		source.open();
		
		//this stores the unique recID's in a chunk
		SortedSet rows = new TreeSet ();

		IChunkRecordIdSink recIDSink = recIDFactory.getNextSink();
		recIDSinks.add(recIDSink);

		int count = 0;
		int countAll = 0;

		// skipping
		while ((count < skip) && (source.hasNext())) {
			source.next();
			count++;
		}

		count = 0;
		while (source.hasNext() && !stop) {
			count++;
			countAll++;

			stop = ControlChecker.checkStop(control, countAll);

			IIDSet bs = source.next();
			LongArrayList block = bs.getRecordIDs();

			// add to the set of distinct record ids
			for (int i = 0; i < block.size() && !stop; i++) {
				//put the internal id in the set
				Long index = new Long(block.get(i));
//				boolean isValidIndex =
//					this.translator.isValidStagingIndex(I.longValue())
//						|| this.translator.isValidMasterIndex(I.longValue());
				boolean isValidIndex = index.longValue() >= IRecordIDTranslator3.MINIMUM_VALID_INDEX;
				if (isValidIndex && !rows.contains(index)) {
					rows.add(index);
				} else if (i>0 && !isValidIndex)  {
					log.warn(
						"Element " + i + " of blocking set " + countAll
							+ " is an invalid index (value: " + index + ")");
				} else if (i==0) {
					// Don't bother logging if i==0; the first index in a block will be invalid
					continue;
				} else if (isValidIndex) {
					// Don't bother logging: the row already contains this index
					continue;
				} else {
					// Unexpected -- should be unreachable, although the compiler can't know this
					throw new Error("Unexpected: code should be unreachable");
				}
			}
			
			//transform and write out array or tree
			transformer.transform(bs);
			

			//when the hashset gets too big, clear it and start a new file
			if (rows.size() > maxChunkSize) {
				//write the ids to sink
				writeChunkRows (recIDSink, rows);

				log.info ( recIDSink.getInfo() + " has " + count + " blocks " + rows.size() + " rows");
				
				//write status
				numChunks ++;
				String temp = Integer.toString(numChunks) + IStatus.DELIMIT + Integer.toString(skip + countAll);
				if (isOS) status.setStatus( IStatus.CREATE_CHUNK_OVERSIZED_IDS, temp );
				else status.setStatus( IStatus.CREATE_CHUNK_IDS, temp );
						
				//use the next sink
				transformer.useNextSink();
				
				//create a new recIDSink
				recIDSink = recIDFactory.getNextSink();		
				recIDSinks.add(recIDSink);
				
				//reset variables
				rows = new TreeSet ();
				count = 0;
			}
			
		} //end while
		
		source.close ();

		//One last write to sink
		if (rows.size() > 0 && !stop) {
			writeChunkRows (recIDSink, rows);
			log.info ( recIDSink.getInfo() +" has " + count + " blocks " + rows.size() + " rows");

			numChunks ++;
			
			if (isOS) {
				String temp = Integer.toString(numChunks) + DELIM 
					+ Integer.toString(numRegularChunks);
				status.setStatus( IStatus.CREATE_CHUNK_OVERSIZED_IDS, temp );
			} else {
				String temp = Integer.toString(numChunks);
				status.setStatus( IStatus.CREATE_CHUNK_IDS, temp );
			} 
		}

		// cleanup
		if (!keepFiles)
			source.delete();

		transformer.close();

		return countAll;
	}

	/**
	 * This method writes the ids in the tree set to the sink. The ids are
	 * written in ascending order.
	 * 
	 * @param recSink
	 *            - chunk record id sink
	 * @param rows
	 *            - hash set containing the distinct ids
	 * @throws IOException
	 */
	private static void writeChunkRows(IChunkRecordIdSink recSink, SortedSet rows)
			throws BlockingException {
		recSink.open();

		Iterator it = rows.iterator();
		long id;
		while (it.hasNext()) {
			id = ((Long) it.next()).longValue();
			recSink.writeRecordID(id);
		}

		recSink.close();
	}

	@Override
	public String toString() {
		return "ChunkService3 [model=" + model + ", status=" + status
				+ ", maxChunkSize=" + maxChunkSize + ", splitIndex="
				+ splitIndex + ", recIDSinks=" + recIDSinks + ", numChunks="
				+ numChunks + ", maxFiles=" + maxFiles + ", numRegularChunks="
				+ numRegularChunks + ", keepFiles=" + keepFiles + "]";
	}

}