/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.services;

import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.CONTROL_INTERVAL;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.DEBUG_INTERVAL;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FM0;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FM1;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FM2;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FS0;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FS1;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FS2;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FT0;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FT2;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logConnectionAcquisition;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logRecordIdCount;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logTransferRate;
import static com.choicemaker.util.SystemPropertyUtils.PV_LINE_SEPARATOR;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.aba.BlockingAccessor;
import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.aba.IDbField;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.IControl;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.oaba.core.IRecValSink;
import com.choicemaker.cm.oaba.core.IRecValSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IRecordIdFactory;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.core.OabaProcessingConstants;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.utils.MemoryEstimator;
import com.choicemaker.util.IntArrayList;
import com.choicemaker.util.Precondition;

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

	private static final Logger log =
		Logger.getLogger(RecValService3.class.getName());

	private static final String SOURCE = RecValService3.class.getSimpleName();
	private static final String TX_FAILURE_MSG =
		"%s.%s: transaction failed: %s";

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

	private final RecordMatchingMode mode;

	private IRecValSink[] sinks;

	private final UserTransaction userTx;

	private RECORD_ID_TYPE recordIdType = null;

	/**
	 * Tracks the time spent per invocation of {@link #runService()}. (Assumes
	 * that {@link #runService()} is invoked only once per service instance.)
	 */
	private long time;

	/**
	 * Constructs a service that computes and stores field-record-value triplets
	 * to files, one file per field. Records are represented by translated,
	 * int-valued identifiers. Values are represented by their hashed values.
	 * Fields may be stacked (i.e. multi-valued).
	 * 
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
			ImmutableProbabilityModel model, String blockName, String queryConf,
			String refConf, IRecValSinkSourceFactory rvFactory,
			IRecordIdFactory recidFactory, MutableRecordIdTranslator translator,
			ProcessingEventLog status, IControl control,
			RecordMatchingMode mode, UserTransaction tx) {

		Precondition.assertNonNullArgument("null user transaction", tx);

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
		this.mode = mode;
		this.userTx = tx;

		BlockingAccessor ba0 = (BlockingAccessor) model.getAccessor();

		IBlockingConfiguration bc0 = ba0.getBlockingConfiguration(
				blockingConfiguration, queryConfiguration);
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
			IBlockingConfiguration bc1 = ba1.getBlockingConfiguration(
					blockingConfiguration, referenceConfiguration);
			IBlockingField[] bfs1 = bc1.getBlockingFields();
			if (bfs1.length != bfs0.length) {
				String msg = "Different number of blocking fields for query ("
						+ bfs0.length + ") and reference (" + bfs1.length
						+ ") database configurations";
				throw new IllegalArgumentException(msg);
			}
			for (int i = 0; i < bfs0.length; i++) {
				IDbField field0 = bfs0[i].getDbField();
				IDbField field1 = bfs1[i].getDbField();
				if (field0.getNumber() != field1.getNumber()) {
					String msg = "Different field numbers at index " + i
							+ ": field0: " + field0.getNumber() + ", field1: "
							+ field1.getNumber();
					throw new IllegalArgumentException(msg);
				}
				if (!field0.getName().equals(field1.getName())) {
					String msg = "Different field names at index " + i
							+ ": field0: '" + field0.getName() + "', field1: '"
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
		// final String METHOD = "runService()";
		// final String TAG = SOURCE + "." + METHOD + ": ";
		time = System.currentTimeMillis();

		if (status
				.getCurrentProcessingEventId() >= OabaProcessingConstants.EVT_DONE_REC_VAL) {
			// need to initialize
			log.info("recover rec,val files and mutableTranslator");
			recover();

		} else if (status
				.getCurrentProcessingEventId() < OabaProcessingConstants.EVT_DONE_REC_VAL) {
			// create the rec_id, val_id files
			log.info("Creating new rec,val files");
			setStatusEvent(OabaEventBean.CREATE_REC_VAL);
			createFiles();
			setStatusEvent(OabaEventBean.DONE_REC_VAL);

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
		final String METHOD = "recover()";

		sinks = new IRecValSink[numBlockFields];
		for (int i = 0; i < numBlockFields; i++) {
			sinks[i] = rvFactory.getNextSink();
		}

		try {
			userTx.begin();
			ImmutableRecordIdTranslator immutableTranslator =
				recidFactory.toImmutableTranslator(mutableTranslator);
			this.recordIdType = immutableTranslator.getRecordIdType();
			userTx.commit();
		} catch (SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException
				| NotSupportedException | SystemException e) {
			String msg =
				String.format(TX_FAILURE_MSG, SOURCE, METHOD, e.toString());
			log.severe(msg);
			throw new BlockingException(msg);
		}
		assert this.recordIdType != null;

	}

	private void acquireStaging() throws Exception {
		assert this.stage != null;
		final String METHOD = "acquireStaging()";
		final String TAG = SOURCE + "." + METHOD + ": ";

		log.finest(TAG + "opening stage");
		final long startAcquire = System.currentTimeMillis();
		// userTx.begin();
		stage.open();
		// userTx.commit();
		final long acquireMsecs = System.currentTimeMillis() - startAcquire;
		log.finest(TAG + "stage opened");
		logConnectionAcquisition(log, FS0, TAG, acquireMsecs);
	}

	private void acquireMaster() throws Exception {
		assert this.master != null;
		final String METHOD = "acquireMaster()";
		final String TAG = SOURCE + "." + METHOD + ": ";

		log.finest(TAG + "opening master");
		final long startAcquire = System.currentTimeMillis();
		// userTx.begin();
		master.open();
		// userTx.commit();
		final long acquireMsecs = System.currentTimeMillis() - startAcquire;
		log.finest(TAG + "master opened");
		logConnectionAcquisition(log, FM0, TAG, acquireMsecs);
	}

	private RECORD_ID_TYPE computeRecordIdType(Record r) {
		assert r != null;
		Object O = r.getId();
		RECORD_ID_TYPE retVal = RECORD_ID_TYPE.fromInstance((Comparable) O);
		return retVal;
	}

	private int createStagingFiles() throws BlockingException, IOException {
		assert this.stage != null;
		final String METHOD = "createStagingFiles()";
		final String TAG = SOURCE + "." + METHOD + ": ";

		boolean stop = shouldStop(TAG);
		int count = 0;
		try {

			BlockingAccessor ba = (BlockingAccessor) model.getAccessor();
			IBlockingConfiguration bc = ba.getBlockingConfiguration(
					blockingConfiguration, queryConfiguration);

			int incrementalCount = 0;
			final long startStaging = System.currentTimeMillis();
			long incrementalStart = startStaging;
			boolean firstStage = true;
			// userTx.begin();
			while (stage.hasNext() && !stop) {

				count++;
				++incrementalCount;
				final Record r = stage.getNext();
				writeRecord(bc, r, model);
				if (firstStage) {
					recordIdType = computeRecordIdType(r);
					firstStage = false;
				}

				if (count % CONTROL_INTERVAL == 0) {
					// userTx.commit();

					log.finest(TAG + "count: " + count);
					stop = shouldStop(TAG);
					MemoryEstimator.writeMem();

					logRecordIdCount(log, FS1, TAG, r.getId(), count);
					final long incrementalMsecs =
						System.currentTimeMillis() - incrementalStart;
					logTransferRate(log, FS2, TAG, incrementalCount,
							incrementalMsecs);
					incrementalCount = 0;
					incrementalStart = System.currentTimeMillis();

					// userTx.begin();
				}

			}
			// if (userTx.getStatus() == Status.STATUS_ACTIVE) {
			// userTx.commit();
			// }
			final long downloadMsecs =
				System.currentTimeMillis() - startStaging;
			logTransferRate(log, FS2, TAG, count, downloadMsecs);

		} finally {
			log.finest(TAG + "closing stage");
			stage.close();
			log.finest(TAG + "stage closed");
		}

		return count;
	}

	private int createMasterFiles() throws BlockingException, IOException {
		assert this.master != null;
		final String METHOD = "createMasterFiles()";
		final String TAG = SOURCE + "." + METHOD + ": ";

		boolean stop = shouldStop(TAG);
		int count = 0;
		try {

			BlockingAccessor ba = (BlockingAccessor) model.getAccessor();
			IBlockingConfiguration bc = ba.getBlockingConfiguration(
					blockingConfiguration, referenceConfiguration);

			int incrementalCount = 0;
			final long startMaster = System.currentTimeMillis();
			long incrementalStart = startMaster;
			boolean firstMaster = true;
			// userTx.begin();
			while (master.hasNext() && !stop) {

				++count;
				++incrementalCount;
				final Record r = master.getNext();
				writeRecord(bc, r, model);
				if (firstMaster) {
					assert this.recordIdType == computeRecordIdType(r);
					firstMaster = false;
				}

				if (count % CONTROL_INTERVAL == 0) {
					// userTx.commit();

					log.finest(TAG + "count: " + count);
					stop = shouldStop(TAG);
					MemoryEstimator.writeMem();

					logRecordIdCount(log, FM1, TAG, r.getId(), count);
					final long incrementalMsecs =
						System.currentTimeMillis() - incrementalStart;
					logTransferRate(log, FM2, TAG, incrementalCount,
							incrementalMsecs);
					incrementalCount = 0;
					incrementalStart = System.currentTimeMillis();

					// userTx.begin();
				}

			}
			// if (userTx.getStatus() == Status.STATUS_ACTIVE) {
			// userTx.commit();
			// }
			final long downloadMsecs = System.currentTimeMillis() - startMaster;
			logTransferRate(log, FM2, TAG, count, downloadMsecs);

		} finally {
			log.finest(TAG + "closing master");
			master.close();
			log.finest(TAG + "master closed");
		}

		return count;
	}

	/**
	 * This method creates per-field files ('btemp') that hold record-value
	 * lists
	 */
	private void createFiles() throws BlockingException {
		final String METHOD = "createFiles()";
		final String TAG = SOURCE + "." + METHOD + ": ";

		try {
			sinks = new IRecValSink[numBlockFields];
			for (int i = 0; i < numBlockFields; i++) {
				sinks[i] = rvFactory.getNextSink();
				log.finest(TAG + "opening sink #" + i);
				sinks[i].open();
			}
			log.finest(TAG + "sinks opened: " + numBlockFields);

			log.finest(TAG + "opening translator");
			userTx.begin();
			mutableTranslator.open();
			userTx.commit();
			log.finest(TAG + "translator opened");

			long totalAcquireMsecs = 0;
			long totalDownloadMsecs = 0;

			int count = 0;
			if (stage != null) {
				stage.setModel(model);

				final long startAcquire = System.currentTimeMillis();
				acquireStaging();
				totalAcquireMsecs += System.currentTimeMillis() - startAcquire;

				final long startTransfer = System.currentTimeMillis();
				count += createStagingFiles();
				totalDownloadMsecs +=
					System.currentTimeMillis() - startTransfer;
			}

			log.info(count + " stage records read");
			int masterCount = 0;

			if (master == null) {
				log.fine("Skipping master records (no master record source)");
			} else if (mode != RecordMatchingMode.BRM) {
				assert master != null;
				String name = mode == null ? "null" : mode.name();
				String msg = "Skipping master records (mode: '" + name + "')";
				log.fine(msg);
			} else {
				assert master != null;
				assert mode == RecordMatchingMode.BRM;

				log.finest(TAG + "spliting translator");
				userTx.begin();
				mutableTranslator.split();
				userTx.commit();
				log.finest(TAG + "translator split");

				master.setModel(model);

				final long startAcquire = System.currentTimeMillis();
				acquireMaster();
				totalAcquireMsecs += System.currentTimeMillis() - startAcquire;

				final long startTransfer = System.currentTimeMillis();
				masterCount += createMasterFiles();
				count += masterCount;
				totalDownloadMsecs +=
					System.currentTimeMillis() - startTransfer;
			}

			log.info(masterCount + " master records read");
			log.info(count + " total records read");
			logConnectionAcquisition(log, FT0, TAG, totalAcquireMsecs);
			logTransferRate(log, FT2, TAG, count, totalDownloadMsecs);

			// close rec val sinks
			for (int i = 0; i < sinks.length; i++) {
				log.finest(TAG + "closing sink #" + i);
				sinks[i].close();
			}
			log.finest(TAG + "sinks closed: " + sinks.length);

			// FIXME: is this service responsible for converting the
			// translator to immutable? The service client (StartOaba)
			// also does this (idempotent) conversion. Two conversions
			// are harmless but time consuming.
			log.finest(TAG + "converting translator to immutable");
			ImmutableRecordIdTranslator usedLater = null;
			userTx.begin();
			usedLater = recidFactory.toImmutableTranslator(mutableTranslator);
			userTx.commit();
			log.info("Converted record-id translator to immutable: "
					+ usedLater);

		} catch (Exception e) {
			final String msg =
				String.format(TX_FAILURE_MSG, SOURCE, METHOD, e.toString());
			log.severe(msg);
			try {
				userTx.setRollbackOnly();
			} catch (Exception e1) {
				final String msg1 =
					TAG + "unable to rollback transaction: " + e1.toString();
				log.severe(msg1);
			}
			throw new BlockingException(msg);
		}
	}

	private boolean shouldStop(String tag) throws BlockingException {
		boolean retVal = false;
		try {
			userTx.begin();
			retVal = this.control.shouldStop();
			userTx.commit();
			log.finest(tag + "shouldStop: " + this.control.shouldStop());
		} catch (NotSupportedException | SystemException | SecurityException
				| IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException e) {
			String msg0 =
				"Failed to check whether processing should stop. Cause: %s";
			String msg = String.format(msg0, e.toString());
			log.severe(msg);
			throw new BlockingException(msg);
		}
		return retVal;
	}

	private void setStatusEvent(ProcessingEvent evt) throws BlockingException {
		setStatusEvent(evt, null);
	}

	private void setStatusEvent(ProcessingEvent evt, String info)
			throws BlockingException {
		assert evt != null;
		try {
			userTx.begin();
			this.status.setCurrentProcessingEvent(evt, info);
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

	/**
	 * This method writes the record id and value ids of a record to the
	 * per-field record-value ('btemp') files.
	 *
	 */
	private void writeRecord(IBlockingConfiguration bc, Record r,
			ImmutableProbabilityModel model) throws BlockingException {

		final String METHOD = "writeRecord(..)";
		final String CLASS = this.getClass().getSimpleName();
		final String TAG = CLASS + "." + METHOD + ": ";

		assert r != null : "null database record";
		final Object recordId = r.getId();
		assert recordId != null : "null database record id";
		final int internal = mutableTranslator.translate((Comparable) recordId);

		HashSet seen = new HashSet(); // stores field value it has seen
		Hashtable values = new Hashtable(); // stores values per field

		int blockingValueIdx = -1;
		try {
			IBlockingValue[] bvs = bc.createBlockingValues(r);

			// loop over the blocking value for this record
			for (int j = 0; j < bvs.length; j++) {
				blockingValueIdx = j;
				IBlockingValue bv = bvs[j];
				IBlockingField bf = bv.getBlockingField();

				final Integer C = new Integer(bf.getDbField().getNumber());

				String val = new String(bv.getValue());
				String key = bf.getDbField().getNumber() + val;

				if (!seen.contains(key)) {
					seen.add(key);

					IntArrayList list = (IntArrayList) values.get(C);
					if (list == null) {
						list = new IntArrayList(1);
					}

					list.add(val.hashCode());
					values.put(C, list);
				}

			} // end for

			Enumeration e = values.keys();
			while (e.hasMoreElements()) {
				Integer C = (Integer) e.nextElement();
				sinks[C.intValue()].writeRecordValue((long) internal,
						(IntArrayList) values.get(C));

				if (internal % DEBUG_INTERVAL == 0) {
					log.finest(
							"id " + internal + " C " + C + " " + values.get(C));
				}
			}
		} catch (RuntimeException | Error | BlockingException x) {
			final String CONTEXT = "[BlockingValue index: " + blockingValueIdx
					+ ", BlockingField index: " + blockingValueIdx + "]: ";
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			x.printStackTrace(pw);

			String s = sw.toString();
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			StringReader sr = new StringReader(s);
			LineNumberReader lnr = new LineNumberReader(sr);
			String STACK = null;
			try {
				while ((s = lnr.readLine()) != null) {
					if (s.contains("choicemaker")) {
						pw.println(s);
					}
				}
				STACK = sw.toString();
			} catch (IOException x2) {
				STACK = "<no stack>: " + x.toString();
			}

			String msg =
				TAG + CONTEXT + x.toString() + PV_LINE_SEPARATOR + STACK;
			log.severe(msg);
			throw x;
		}
	}

	/**
	 * This counts the number of distinct db fields used in the blocking.
	 *
	 * @param bfs
	 *            - array of BlockingFields
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
