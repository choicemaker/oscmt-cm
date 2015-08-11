/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.services;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.choicemaker.cm.batch.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.IControl;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.io.blocking.automated.BlockingAccessor;
import com.choicemaker.cm.io.blocking.automated.IBlockingConfiguration;
import com.choicemaker.cm.io.blocking.automated.IBlockingField;
import com.choicemaker.cm.io.blocking.automated.IBlockingValue;
import com.choicemaker.cm.io.blocking.automated.IDbField;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecValSink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecValSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIdFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.io.blocking.automated.offline.core.MutableRecordIdTranslator;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessingEvent;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE;
import com.choicemaker.cm.io.blocking.automated.offline.utils.ControlChecker;
import com.choicemaker.cm.io.blocking.automated.offline.utils.MemoryEstimator;
import com.choicemaker.util.IntArrayList;

/**
 * This object performs the creation of rec_id, val_id pairs.
 *
 * It uses input record id to internal id translation. As a result, it can use a
 * array instead of hashmap to stored the val_id's. This also uses dbField
 * instead of blocking fields to prep for swap.
 *
 * This takes in a stage record source and master record source. It translates
 * the stage record source first, then the master record source.
 *
 * Version 2 allows the record ID to be Integer, Long, or String.
 *
 * Version 3 allows a break in the loop. This version also starts recovery from
 * the beginning of the record source.
 *
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class RecValService3 {

	private static final Logger log = Logger.getLogger(RecValService3.class
			.getName());

	private static final int OUTPUT_INTERVAL = 100000;

	private static final int DEBUG_INTERVAL = 1000;

	private final RecordSource master;
	private final RecordSource stage;
	private final ImmutableProbabilityModel model;
	private final String blockingConfiguration;
	private final String queryConfiguration;
	private final String referenceConfiguration;
	private final int numBlockFields;

	private final IRecValSinkSourceFactory rvFactory;

	private final IRecordIdFactory recidFactory;

	private final MutableRecordIdTranslator mutableTranslator;

	private final ProcessingEventLog status;

	private final IControl control;

	private IRecValSink[] sinks;

	/** A flag indicating whether any staging record has been read */
	private boolean firstStage = true;

	private RECORD_ID_TYPE recordIdType = null;

	/** A flag indicating whether any master record has been read */
	private boolean firstMaster = true;

	/**
	 * Tracks the time spent per invocation of {@link #runService()}.
	 * (Assumes that {@link #runService()} is invoked only once per service
	 * instance.)
	 */
	private long time;

	/**
	 * Constructs a service that computes and stores field-record-value triplets
	 * to files, one file per field. Records are represented by translated,
	 * int-valued identifiers. Values are represented by their hashed values.
	 * Fields may be stacked (i.e. multi-valued).
	 * <pre>
	 * File 1: Blocking field 1
	 *   Translated id 356: value hash, value hash
	 *   Translated id 123: value hash
	 *   Translated id 947: value hash, value hash, value hash, ...
	 *   ...
	 * </pre>
	 *
	 * @param queryRS
	 *            query record source
	 * @param refRS
	 *            reference record source
	 * @param model
	 *            matching model
	 * @param dbParams
	 *            database and blocking configuration
	 * @param rvFactory
	 *            a factory for record-value sinks
	 * @param recidFactory
	 *            a factory for record identifiers
	 * @param translator
	 *            a translator of record identifiers to internal identifiers
	 * @param status
	 *            a log for recording status
	 * @param control
	 *            a controller for this service
	 */
	public RecValService3(RecordSource queryRS, RecordSource refRS,
			ImmutableProbabilityModel model, String blockName,
			String queryConf, String refConf,
			IRecValSinkSourceFactory rvFactory, IRecordIdFactory recidFactory,
			MutableRecordIdTranslator translator, ProcessingEventLog status,
			IControl control) {

		this.stage = queryRS;
		this.master = refRS;
		this.model = model;
		this.blockingConfiguration = blockName;
		this.queryConfiguration = queryConf;
		this.referenceConfiguration = refConf;
		this.mutableTranslator = translator;
		this.rvFactory = rvFactory;
		this.recidFactory = recidFactory;
		this.status = status;
		this.control = control;

		BlockingAccessor ba0 = (BlockingAccessor) model.getAccessor();

		IBlockingConfiguration bc0 =
			ba0.getBlockingConfiguration(blockingConfiguration,
					queryConfiguration);
		IBlockingField[] bfs0 = bc0.getBlockingFields();

		// log blocking info
		for (int i = 0; i < bfs0.length; i++) {
			IDbField field = bfs0[i].getDbField();
			log.info("i " + i + " field " + field.getName() + " number "
					+ field.getNumber());
		}

		this.numBlockFields = countFields(bfs0);

		// Check for consistency if a master record source is specified
		if (master != null) {
			BlockingAccessor ba1 = (BlockingAccessor) model.getAccessor();
			IBlockingConfiguration bc1 =
				ba1.getBlockingConfiguration(blockingConfiguration,
						referenceConfiguration);
			IBlockingField[] bfs1 = bc1.getBlockingFields();
			if (bfs1.length != bfs0.length) {
				String msg =
					"Different number of blocking fields for query ("
							+ bfs0.length + ") and reference (" + bfs1.length
							+ ") database configurations";
				throw new IllegalArgumentException(msg);
			}
			for (int i = 0; i < bfs0.length; i++) {
				IDbField field0 = bfs0[i].getDbField();
				IDbField field1 = bfs1[i].getDbField();
				if (field0.getNumber() != field1.getNumber()) {
					String msg =
						"Different field numbers at index " + i + ": field0: "
								+ field0.getNumber() + ", field1: "
								+ field1.getNumber();
					throw new IllegalArgumentException(msg);
				}
				if (!field0.getName().equals(field1.getName())) {
					String msg =
						"Different field names at index " + i + ": field0: '"
								+ field0.getName() + "', field1: '"
								+ field1.getName() + "'";
					throw new IllegalArgumentException(msg);
				}
			}
			int numRefBlockFields = countFields(bfs1);
			assert numRefBlockFields == this.numBlockFields;
		}
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

	public int getNumBlockingFields() {
		return numBlockFields;
	}

	protected RECORD_ID_TYPE getStageType() {
		return recordIdType;
	}

	protected RECORD_ID_TYPE getMasterType() {
		return recordIdType;
	}

	/**
	 * This method runs the service.
	 *
	 *
	 */
	public void runService() throws BlockingException {
		time = System.currentTimeMillis();

		if (status.getCurrentProcessingEventId() >= OabaProcessing.EVT_DONE_REC_VAL) {
			// need to initialize
			log.info("recover rec,val files and mutableTranslator");
			recover();

		} else if (status.getCurrentProcessingEventId() < OabaProcessing.EVT_DONE_REC_VAL) {
			// create the rec_id, val_id files
			log.info("Creating new rec,val files");
			status.setCurrentProcessingEvent(OabaProcessingEvent.CREATE_REC_VAL);
			createFiles();

			boolean stop = this.control.shouldStop();
			if (!stop) {
				status.setCurrentProcessingEvent(OabaProcessingEvent.DONE_REC_VAL);
			}

		} else {
			log.info("Skipping RecValService3.runService()");

		}
		time = System.currentTimeMillis() - time;
	}

	/**
	 * This method closes the translator, converts to an immutable translator,
	 * and determines the record id type handled by the translator. It also sets
	 * up the sinks array for future use.
	 */
	protected void recover() throws BlockingException {

		sinks = new IRecValSink[numBlockFields];
		for (int i = 0; i < numBlockFields; i++) {
			sinks[i] = rvFactory.getNextSink();
		}

		ImmutableRecordIdTranslator immutableTranslator =
			recidFactory.toImmutableTranslator(mutableTranslator);
		this.recordIdType = immutableTranslator.getRecordIdType();
		assert this.recordIdType != null;

	}

	/**
	 * This method creates the files from scratch.
	 *
	 */
	private void createFiles() throws BlockingException {
		sinks = new IRecValSink[numBlockFields];

		int count = 0;

		for (int i = 0; i < numBlockFields; i++) {
			sinks[i] = rvFactory.getNextSink();
			sinks[i].open();
		}

		mutableTranslator.open();

		try {
			Record r;

			boolean stop = this.control.shouldStop();

			// write the stage record source
			if (stage != null) {
				stage.setModel(model);
				stage.open(); // FIXME! try { stage.open(); ... } finally{
								// stage.close(); }

				BlockingAccessor ba = (BlockingAccessor) model.getAccessor();
				IBlockingConfiguration bc =
					ba.getBlockingConfiguration(blockingConfiguration,
							queryConfiguration);

				while (stage.hasNext() && !stop) {
					count++;
					r = stage.getNext();

					if (count % OUTPUT_INTERVAL == 0)
						MemoryEstimator.writeMem();

					stop = ControlChecker.checkStop(control, count);

					writeRecord(bc, r, model);

					// This checks the id type
					if (firstStage) {
						Object O = r.getId();
						recordIdType =
							RECORD_ID_TYPE.fromInstance((Comparable) O);
						firstStage = false;
					}

				} // end while
				stage.close();
			}

			log.info(count + " stage records read");

			// write the master record source
			if (master != null) {
				mutableTranslator.split();

				master.open(); // FIXME! try { master.open(); ... } finally{
								// master.close(); }

				BlockingAccessor ba = (BlockingAccessor) model.getAccessor();
				IBlockingConfiguration bc =
					ba.getBlockingConfiguration(blockingConfiguration,
							referenceConfiguration);

				// 2014-04-24 rphall: Commented out unused local variable.
				// long lastID = Long.MIN_VALUE;
				while (master.hasNext() && !stop) {
					count++;
					r = master.getNext();

					if (count % OUTPUT_INTERVAL == 0)
						MemoryEstimator.writeMem();

					stop = ControlChecker.checkStop(control, count);

					writeRecord(bc, r, model);

					// This checks the id type
					if (firstMaster) {
						firstMaster = false;
					}

				} // end while
				master.close();
			}

			log.info(count + " total records read");
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}

		// close rec val sinks
		for (int i = 0; i < sinks.length; i++) {
			sinks[i].close();
		}
		ImmutableRecordIdTranslator usedLater =
			recidFactory.toImmutableTranslator(mutableTranslator);
		log.info("Converted record-id translator to immutable: " + usedLater);
	}

	/**
	 * This method writes 1 record's rec_id and val_id.
	 *
	 */
	private void writeRecord(IBlockingConfiguration bc, Record r,
			ImmutableProbabilityModel model) throws BlockingException {

		Object O = r.getId();
		int internal = mutableTranslator.translate((Comparable) O);

		HashSet seen = new HashSet(); // stores field value it has seen
		Hashtable values = new Hashtable(); // stores values per field

		IBlockingValue[] bvs = bc.createBlockingValues(r);

		// loop over the blocking value for this record
		for (int j = 0; j < bvs.length; j++) {
			IBlockingValue bv = bvs[j];
			IBlockingField bf = bv.getBlockingField();

			Integer C = new Integer(bf.getDbField().getNumber());

			// System.out.println (bf.number + " " + bv.value + " " +
			// bf.dbField.number + " " + bf.dbField.name);

			String val = new String(bv.getValue());
			String key = bf.getDbField().getNumber() + val;

			if (!seen.contains(key)) {
				seen.add(key);

				IntArrayList list = (IntArrayList) values.get(C);
				if (list == null) {
					list = new IntArrayList(1);
				}

				list.add(val.hashCode()); // use hashcode of the string
				values.put(C, list);
			}

		} // end for

		Enumeration e = values.keys();
		while (e.hasMoreElements()) {
			Integer C = (Integer) e.nextElement();
			sinks[C.intValue()].writeRecordValue((long) internal,
					(IntArrayList) values.get(C));

			if (internal % DEBUG_INTERVAL == 0) {
				log.fine("id " + internal + " C " + C + " " + values.get(C));
			}
		}
	}

	/**
	 * This counts the number of distinct db fields used in the blocking.
	 *
	 * @param bfs
	 *            - array of BlockingFields
	 * @return
	 */
	private int countFields(IBlockingField[] bfs) {
		HashSet set = new HashSet();

		for (int i = 0; i < bfs.length; i++) {
			IDbField field = bfs[i].getDbField();
			Integer I = new Integer(field.getNumber());

			if (!set.contains(I)) {
				set.add(I);
			}
		}

		return set.size();
	}

}
