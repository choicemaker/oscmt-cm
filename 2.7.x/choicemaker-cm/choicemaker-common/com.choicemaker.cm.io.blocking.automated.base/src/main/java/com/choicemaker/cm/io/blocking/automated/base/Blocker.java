/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.blocking.automated.IBlockingConfiguration;
import com.choicemaker.cm.io.blocking.automated.IBlockingField;
import com.choicemaker.cm.io.blocking.automated.IBlockingSet;
import com.choicemaker.cm.io.blocking.automated.IBlockingValue;
import com.choicemaker.cm.io.blocking.automated.IDbField;
import com.choicemaker.cm.io.blocking.automated.IField;
import com.choicemaker.cm.io.blocking.automated.IQueryField;
import com.choicemaker.cm.io.blocking.automated.UnderspecifiedQueryException;
import com.choicemaker.cm.io.blocking.automated.util.PrintUtils;

/**
 * @author    Martin Buechi
 * @deprecated Returns sometimes erroneous blockingSets;
 * use {@link Blocker2} instead.
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class Blocker implements AutomatedBlocker {
	public static final String LIMIT_PER_BLOCKING_SET = "limitPerBlockingSet";
	public static final String LIMIT_SINGLE_BLOCKING_SET = "limitSingleBlockingSet";
	private static Logger logger = Logger.getLogger(Blocker.class.getName());

	private String name;
	private AbaStatistics abaStatistics;
	private DatabaseAccessor databaseAccessor;
	private ImmutableProbabilityModel model;
	private final String databaseConfiguration;
	private IBlockingConfiguration blockingConfiguration;
	private Record q;
	private int limitPerBlockingSet;
	private int singleTableBlockingSetGraceLimit;
	private int limitSingleBlockingSet;
	private List possibleSubsets;
	private List blockingSets;
	private int numberOfRecordsRetrieved;

//	Blocker(DatabaseAccessor databaseAccessor, ImmutableProbabilityModel model,
//			Record q, AbaSettings abaSettings) {
//		this(
//			databaseAccessor,
//			model,
//			q,
//			abaSettings.getLimitPerBlockingSet(),
//			abaSettings.getSingleTableBlockingSetGraceLimit(),
//			abaSettings.getLimitSingleBlockingSet());
//	}

//	Blocker(DatabaseAccessor databaseAccessor,
//				   ImmutableProbabilityModel model,
//				   Record q,
//				   AbaSettings abaSettings,
//				   String dbConfigurationName,
//				   String blockingConfigurationName) {
//		this(
//		databaseAccessor,
//		model,
//		q,
//		abaSettings.getLimitPerBlockingSet(),
//		abaSettings.getSingleTableBlockingSetGraceLimit(),
//		abaSettings.getLimitSingleBlockingSet(),
//		(AbaStatistics) model.getCountSource(),
//		dbConfigurationName,
//		blockingConfigurationName
//		);
//	}

//	Blocker(
//		DatabaseAccessor databaseAccessor,
//		ImmutableProbabilityModel model,
//		Record q,
//		int limitPerBlockingSet,
//		int singleTableBlockingSetGraceLimit,
//		int limitSingleBlockingSet) {
//		this(
//			databaseAccessor,
//			model,
//			q,
//			limitPerBlockingSet,
//			singleTableBlockingSetGraceLimit,
//			limitSingleBlockingSet,
//			(AbaStatistics) model.getCountSource(),
//			model.getDatabaseConfigurationName(),
//			model.getBlockingConfigurationName());
//	}

//	public Blocker(
//		DatabaseAccessor databaseAccessor,
//		ImmutableProbabilityModel model,
//		Record q,
//		int limitPerBlockingSet,
//		int singleTableBlockingSetGraceLimit,
//		int limitSingleBlockingSet,
//		AbaStatistics abaStatistics,
//		String dbConfigurationName,
//		String blockingConfigurationName) {
//		this (
//			databaseAccessor,
//			model,
//			q,
//			limitPerBlockingSet,
//			singleTableBlockingSetGraceLimit,
//			limitSingleBlockingSet,
//			abaStatistics,
//			((BlockingAccessor) model.getAccessor()).getBlockingConfiguration(
//				blockingConfigurationName,
//				dbConfigurationName));
//	}

	// For testing; see doSanityCheck()
	Blocker(
		DatabaseAccessor databaseAccessor,
		ImmutableProbabilityModel model,
		Record q,
		int limitPerBlockingSet,
		int singleTableBlockingSetGraceLimit,
		int limitSingleBlockingSet,
		AbaStatistics abaStatistics,
		String dbConfigurationName,
		IBlockingConfiguration blockingConfiguration) {
		this.databaseAccessor = databaseAccessor;
		this.model = model;
		this.databaseConfiguration = dbConfigurationName;
		this.q = q;
		this.limitPerBlockingSet = limitPerBlockingSet;
		this.singleTableBlockingSetGraceLimit =
			singleTableBlockingSetGraceLimit;
		this.limitSingleBlockingSet = limitSingleBlockingSet;
		this.abaStatistics = abaStatistics;
		this.blockingConfiguration = blockingConfiguration;
	}

	public void open() throws IOException {
		numberOfRecordsRetrieved = 0;
		IBlockingValue[] blockingValues =
			blockingConfiguration.createBlockingValues(getQueryRecord());

		long mainTableSize = getCountSource().computeBlockingValueCounts(blockingConfiguration, blockingValues);

		logger.fine("blockingValues numberOfRecordsRetrieved: " + blockingValues.length);

		for (int i=0; i<blockingValues.length; i++) {
			logger.fine(blockingValues[i].getValue() + " " + blockingValues[i].getCount() + " " + blockingValues[i].getBlockingField().getDbField().getName());
		}

		Arrays.sort(blockingValues);
		logger.fine("blockingValues size: " + blockingValues.length);
		for (int i = 0; i < blockingValues.length; i++) {
			PrintUtils.logBlockingValue(logger,"Blocking value " + i + " ", blockingValues[i]);
		}

		possibleSubsets = new ArrayList(256);
		possibleSubsets.add(new BlockingSet(mainTableSize));
		blockingSets = new ArrayList(64);

		logger.fine("Starting to form blocking sets...");
		for (int i = 0; i < blockingValues.length; ++i) {

			IBlockingValue bv = blockingValues[i];
			PrintUtils.logBlockingValue(logger,"Blocking value " + i + " ", bv);

			boolean emptySet = true;
			int size = possibleSubsets.size();
			for (int j = 0; j < size; ++j) { // don't iterate over newly added subsets
				BlockingSet bs = (BlockingSet) possibleSubsets.get(j);

				if (bs != null && valid(bv, bs)) {

					BlockingSet nbs = new BlockingSet(bs, bv);
					PrintUtils.logBlockingSet(logger,"Candidate blocking set ", nbs);

					if(!emptySet && nbs.getNumTables() > bs.getNumTables() && bs.getExpectedCount() <= getSingleTableBlockingSetGraceLimit()) {
						addToBlockingSets(bs);
						int ordinal = getBlockingSets().size() - 1;
						String msg =
							"Formed a grace-limit blocking set (ordinal # "
								+ ordinal
								+ ") ";
						PrintUtils.logBlockingSet(logger,msg, bs);

						possibleSubsets.set(j, null); // don't consider in future
						//TODO: check singleton blocking set numberOfRecordsRetrieved
					} else if (nbs.getExpectedCount() <= getLimitPerBlockingSet()) {
						addToBlockingSets(nbs);
						int ordinal = getBlockingSets().size() - 1;
						if (emptySet) {
							String msg =
								"Formed a single-value blocking set (ordinal # "
									+ ordinal
									+ ") ";
							PrintUtils.logBlockingSet(logger,msg, nbs);
							break;
						}
						String msg =
							"Formed a compound-value blocking set (ordinal # "
								+ ordinal
								+ ") ";
						PrintUtils.logBlockingSet(logger,msg, nbs);
					} else {
						possibleSubsets.add(nbs);
						String msg =
							"Added candidate blocking set to collection of (oversized) possible blocking sets.";
						logger.fine(msg);
					}
				}
				emptySet = false;
			}
		}
		logger.fine(
			"...Finished forming blocking sets. Blocking set size == "
				+ getBlockingSets().size());

		if (getBlockingSets().isEmpty()) {
			logger.fine(
				"No blocking sets were formed yet. Looking for best possible subset of blocking values...");
			Iterator iPossibleSubsets = possibleSubsets.iterator();
			iPossibleSubsets.next(); // skip empty set
			IBlockingSet best = null;
			long bestCount = Long.MIN_VALUE;
			while (iPossibleSubsets.hasNext()) {
				IBlockingSet bs = (IBlockingSet) iPossibleSubsets.next();
				long count = bs.getExpectedCount();
				if (count < getLimitSingleBlockingSet() && count > bestCount) {
					best = bs;
					bestCount = count;
				}
			}
			if (best != null) {
				PrintUtils.logBlockingSet(logger,
					"...Found a suitable subset of blocking values. Using it as the blocking set. ",
					best);
				getBlockingSets().add(best);
			} else {
				logger.fine("...No suitable subset of blocking values.");
				throw new UnderspecifiedQueryException("Query not specific enough; would return too many records.");
			}
		}

		logger.fine("Listing final blocking sets...");
		for (int i = 0; i < getBlockingSets().size(); i++) {
			IBlockingSet b = (IBlockingSet) getBlockingSets().get(i);
			PrintUtils.logBlockingSet(logger,"Blocking set " + i + " ", b);
		}
		logger.fine("...Finished listing final blocking sets");

		getDatabaseAccessor().open(this,databaseConfiguration);
	}

	private boolean valid(IBlockingValue bv, BlockingSet bs) {
		IBlockingField bf = bv.getBlockingField();
		IQueryField qf = bf.getQueryField();
		IDbField dbf = bf.getDbField();

		int size = bs.numFields();
		for (int i = 0; i < size; ++i) {
			IBlockingValue cbv = bs.getBlockingValue(i);
			IBlockingField cbf = cbv.getBlockingField();

			// multiple use of same DbField (implied by multiple use of same BlockingField)
			if (dbf == cbf.getDbField()) {
				logger.fine("invalid BlockingValue for BlockingSet: multiple use of same DbField");
				return false;
			}
			// multiple use of same QueryField
			if (qf == cbf.getQueryField()) {
				logger.fine("invalid BlockingValue for BlockingSet: multiple use of same QueryField");
				return false;
			}
			// illegal combinations
			if (illegalCombination(bs, bf.getIllegalCombinations())) {
				logger.fine("invalid BlockingValue for BlockingSet: Illegal BlockingField combination");
				return false;
			}
			if (illegalCombination(bs, qf.getIllegalCombinations())) {
				logger.fine("invalid BlockingValue for BlockingSet: Illegal QueryField combination");
				return false;
			}
			if (illegalCombination(bs, dbf.getIllegalCombinations())) {
				logger.fine("invalid BlockingValue for BlockingSet: Illegal DbField combination");
				return false;
			}
		}
		return true;
	}

	private boolean illegalCombination(BlockingSet bs, IField[][] illegalCombinations) {
		for (int i = 0; i < illegalCombinations.length; ++i) {
			IField[] ic = illegalCombinations[i];
			int j = 0;
			while (j < ic.length && bs.containsField(ic[j])) {
				++j;
			}
			if (j == ic.length) {
				return true;
			}
		}
		return false;
	}

	private void addToBlockingSets(BlockingSet nbs) {
		Iterator iBlockingSets = getBlockingSets().iterator();
		while (iBlockingSets.hasNext()) {
			BlockingSet cbs = (BlockingSet) iBlockingSets.next();
			if (nbs.returnsSupersetOf(cbs)) {
				iBlockingSets.remove();
			} else if (cbs.returnsSupersetOf(nbs)) {
				return;
			}
		}
		getBlockingSets().add(nbs);
	}

	public void close() throws IOException {
		getDatabaseAccessor().close();
	}

	public boolean hasNext() {
		return getDatabaseAccessor().hasNext();
	}

	public Record getNext() throws IOException {
		++numberOfRecordsRetrieved;
		return getDatabaseAccessor().getNext();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImmutableProbabilityModel getModel() {
		return model;
	}

	public void setModel(ImmutableProbabilityModel m) {
		this.model = m;
	}

	public boolean hasSink() {
		return false;
	}

	public Sink getSink() {
		throw new UnsupportedOperationException("no sink");
	}

	public int getNumberOfRecordsRetrieved() {
		return numberOfRecordsRetrieved;
	}

	public String getFileName() {
		throw new UnsupportedOperationException();
	}

	public DatabaseAccessor getDatabaseAccessor() {
		return databaseAccessor;
	}

	public IBlockingConfiguration getBlockingConfiguration() {
		return blockingConfiguration;
	}

	public Record getQueryRecord() {
		return q;
	}

	public int getLimitPerBlockingSet() {
		return limitPerBlockingSet;
	}

	public int getSingleTableBlockingSetGraceLimit() {
		return singleTableBlockingSetGraceLimit;
	}

	public int getLimitSingleBlockingSet() {
		return limitSingleBlockingSet;
	}

	public List getBlockingSets() {
		return blockingSets;
	}

	public AbaStatistics getCountSource() {
		return abaStatistics;
	}

}

