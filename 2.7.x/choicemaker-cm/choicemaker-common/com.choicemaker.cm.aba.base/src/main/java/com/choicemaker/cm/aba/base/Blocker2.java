/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.aba.AbaStatistics;
import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.BlockingAccessor;
import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.aba.IBlockingSet;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;

/**
 * Creates blockingSets (using a BlockingSetFactory) and provides methods for
 * retrieving blocked records from a database (the {@link AutomatedBlocker}
 * interface extends the {@link com.choicemaker.cm.core.RecordSource}
 * interface).
 * <p>
 *
 * This class is a refactored version of the {@link Blocker} class. The ABA
 * algorithm has moved to a {@link BlockingSetFactory} class for improved
 * testability.
 * 
 * @author Martin Buechi
 * @author rphall (refactoring)
 */
public class Blocker2 implements AutomatedBlocker {

	private static Logger logger = Logger.getLogger(Blocker2.class.getName());

	/**
	 * The name of a system property that can be set to "true" to force the
	 * comparison of BlockingSets produced by this class to BlockingSets
	 * produced by the original {@link Blocker} class.
	 */
	public static final String PN_SANITY_CHECK =
		"com.choicemaker.cm.aba.base.BlockerSanityCheck";

	// Don't use this variable directly; use isSanityCheckRequested() instead
	private static Boolean _isSanityCheckRequested = null;

	/**
	 * Checks the system property {@link #PN_SANITY_CHECK} and caches the result
	 */
	private static boolean isSanityCheckRequested() {
		if (_isSanityCheckRequested == null) {
			String value = System.getProperty(PN_SANITY_CHECK, "false");
			_isSanityCheckRequested = Boolean.valueOf(value);
		}
		boolean retVal = _isSanityCheckRequested.booleanValue();
		return retVal;
	}

	private final ImmutableProbabilityModel model;
	private final IBlockingConfiguration blockingConfiguration;
	private final String databaseConfiguration;
	private final DatabaseAccessor databaseAccessor;
	private final AbaStatistics abaStatistics;
	private final Record q;
	private final int limitPerBlockingSet;
	private final int singleTableBlockingSetGraceLimit;
	private final int limitSingleBlockingSet;
	private List<IBlockingSet> blockingSets;
	private int numberOfRecordsRetrieved;
	private String name;

	public Blocker2(DatabaseAccessor databaseAccessor,
			ImmutableProbabilityModel model, Record q, int limitPerBlockingSet,
			int singleTableBlockingSetGraceLimit, int limitSingleBlockingSet,
			AbaStatistics abaStatistics, String dbConfigurationName,
			String blockingConfigurationName) {

		final String METHOD = "Blocker2.<init>: ";
		if (databaseAccessor == null) {
			String msg = METHOD + "null database accessor";
			throw new IllegalArgumentException(msg);
		}
		if (model == null) {
			String msg = METHOD + "null model";
			throw new IllegalArgumentException(msg);
		}
		if (q == null) {
			String msg = METHOD + "null record";
			throw new IllegalArgumentException(msg);
		}
		if (abaStatistics == null) {
			String msg = METHOD + "null ABA statistics";
			throw new IllegalArgumentException(msg);
		}
		if (databaseAccessor == null || model == null || q == null
				|| abaStatistics == null) {
			String msg = METHOD + "null constructor argument";
			throw new IllegalArgumentException(msg);
		}
		if (limitPerBlockingSet <= 0 || singleTableBlockingSetGraceLimit <= 0
				|| limitSingleBlockingSet <= 0) {
			String msg = "non-positive blocking limit: limitPerBlockingSet="
					+ limitPerBlockingSet
					+ ", singleTableBlockingSetGraceLimit="
					+ singleTableBlockingSetGraceLimit
					+ ", limitSingleBlockingSet=" + limitSingleBlockingSet;
			throw new IllegalArgumentException(msg);
		}
		if (dbConfigurationName == null || dbConfigurationName.isEmpty()
				|| blockingConfigurationName == null
				|| blockingConfigurationName.isEmpty()) {
			String msg = "null or blank database configuration name";
			throw new IllegalArgumentException(msg);
		}

		this.abaStatistics = abaStatistics;
		this.databaseAccessor = databaseAccessor;
		this.model = model;
		this.databaseConfiguration = dbConfigurationName;
		this.blockingConfiguration =
			((BlockingAccessor) model.getAccessor()).getBlockingConfiguration(
					blockingConfigurationName, dbConfigurationName);
		this.q = q;
		this.limitPerBlockingSet = limitPerBlockingSet;
		this.singleTableBlockingSetGraceLimit =
			singleTableBlockingSetGraceLimit;
		this.limitSingleBlockingSet = limitSingleBlockingSet;
	}

	@Override
	public void open() throws IOException {

		this.numberOfRecordsRetrieved = 0;
		this.blockingSets = BlockingSetFactory.createBlockingSets(
				this.blockingConfiguration, this.q, this.limitPerBlockingSet,
				this.singleTableBlockingSetGraceLimit,
				this.limitSingleBlockingSet, this.abaStatistics);
		databaseAccessor.open(this, databaseConfiguration);

		// If processing has gotten this far, (i.e. an
		// IncompleteBlockingSetsException
		// was not thrown), then the results from this class should be the same
		// as
		// the ones from the original Blocker class.
		if (Blocker2.isSanityCheckRequested()) {
			logger.warning("SanityCheck is slowing down blocking");
			logger.info(
					"Comparing BlockingSets to ones from original Blocker class...");
			doSanityCheck();
			logger.info(
					"... Finished comparing BlockingSets to ones from original Blocker class.");
		}

	}

	/**
	 * Throws an IllegalStateException if the Blocker class does not produce the
	 * same blocking sets as this class.
	 */
	private void doSanityCheck() throws IOException {
		DatabaseAccessor clone = null;
		try {
			clone = this.getDatabaseAccessor().cloneWithNewConnection();
		} catch (CloneNotSupportedException x) {
			String msg =
				"Unable to perform sanity check because database accessor can't be cloned";
			logger.warning(msg);
		}

		// Verify that the clone is different from the original
		// (a sanity check within a sanity check -- which is slightly insane)
		if (clone == this.getDatabaseAccessor()) {
			String msg =
				"Unable to perform sanity check because database accessor hasn't been cloned";
			logger.warning(msg);
			clone = null;
		}

		if (clone != null) {
			try (Blocker sanityCheck = new Blocker(clone, getModel(),
					getQueryRecord(), getLimitPerBlockingSet(),
					getSingleTableBlockingSetGraceLimit(),
					getLimitSingleBlockingSet(), getCountSource(),
					databaseConfiguration, getBlockingConfiguration())) {
				sanityCheck.open();
				List<IBlockingSet> newBlockingSets = this.getBlockingSets();
				@SuppressWarnings("unchecked")
				List<IBlockingSet> oldBlockingSets =
					sanityCheck.getBlockingSets();
				if (newBlockingSets.size() != oldBlockingSets.size()) {
					throw new IllegalStateException(
							"Different sizes of blocking set collections");
				}
				for (int i = 0; i < newBlockingSets.size(); i++) {
					IBlockingSet newBlockingSet = newBlockingSets.get(i);
					IBlockingSet oldBlockingSet = oldBlockingSets.get(i);
					if (newBlockingSet == null && oldBlockingSet != null) {
						throw new IllegalStateException(
								"Blocking sets " + i + " are different");
					} else if (newBlockingSet != null
							&& !newBlockingSet.equals(oldBlockingSet)) {
						throw new IllegalStateException(
								"Blocking sets " + i + " are different");
					}
				}
				sanityCheck.close();
			}
		} // if clone
	} // end doSanityCheck()

	public Accessor getAccessor() {
		return model.getAccessor();
	}

	public BlockingAccessor getBlockingAccessor() {
		return (BlockingAccessor) model.getAccessor();
	}

	@Override
	public List<IBlockingSet> getBlockingSets() {
		return blockingSets;
	}

	@Override
	public void close() throws IOException {
		databaseAccessor.close();
	}

	@Override
	public boolean hasNext() {
		return databaseAccessor.hasNext();
	}

	@Override
	public Record getNext() throws IOException {
		++this.numberOfRecordsRetrieved;
		return databaseAccessor.getNext();
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException("no sink");
	}

	@Override
	public int getNumberOfRecordsRetrieved() {
		return this.numberOfRecordsRetrieved;
	}

	@Override
	public String getFileName() {
		throw new UnsupportedOperationException("not file based");
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		throw new UnsupportedOperationException(
				"can't change model after construction");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public IBlockingConfiguration getBlockingConfiguration() {
		return blockingConfiguration;
	}

	@Override
	public DatabaseAccessor getDatabaseAccessor() {
		return databaseAccessor;
	}

	@Override
	public AbaStatistics getCountSource() {
		return abaStatistics;
	}

	@Override
	public Record getQueryRecord() {
		return q;
	}

	@Override
	public int getLimitPerBlockingSet() {
		return limitPerBlockingSet;
	}

	@Override
	public int getSingleTableBlockingSetGraceLimit() {
		return singleTableBlockingSetGraceLimit;
	}

	@Override
	public int getLimitSingleBlockingSet() {
		return limitSingleBlockingSet;
	}

}
