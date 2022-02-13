/*
 * Copyright (c) 2001, 2020 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.args;

/**
 * Settings for the Offline Automated Batch Algorithm (OABA) are matching
 * parameters that change relatively infrequently. Prior to version 2.7 of
 * ChoiceMaker, these settings were stored with each instance of a model
 * configuration. However, since these statistics rarely change, and are often
 * reused between jobs, it makes more sense to store them separately from any
 * particular model. In a J2EE environment, OABA statistics are cached in memory
 * by an EJB singleton service.
 * 
 * @author rphall
 *
 */
public interface OabaSettings extends AbaSettings {

	int DEFAULT_MAX_SINGLE = 0;
	int DEFAULT_MAX_BLOCKSIZE = 100;
	int DEFAULT_MAX_CHUNKSIZE = 100000;
	int DEFAULT_MAX_OVERSIZED = 1000;
	int DEFAULT_MAX_MATCHES = 500000;
	int DEFAULT_MIN_FIELDS = 3;
	int DEFAULT_INTERVAL = 100;

	/**
	 * @return The maximum size of a 'regular' OABA blocking set. Similar to the ABA
	 * {@link #getLimitPerBlockingSet() blocking set limit}.
	 */
	int getMaxBlockSize();

	/**
	 * The maximum number of records to pack into a chunk file. Essentially an
	 * indirect bound on the file size of a chunk.
	 * @return The maximum number of records to pack into a chunk file
	 */
	int getMaxChunkSize();

	/**
	 * The maximum size of an 'oversized' blocking set. If a blocking set can
	 * not be refined to meet the limit set by the {@link #getMaxBlockSize()
	 * maximum size} of a 'regular' blocking set, but if it has the minimum set
	 * of fields specified by {@link #getMinFields()}, the blocking set will be
	 * kept if it contains fewer than this limit. If these conditions are not
	 * met, the blocking set is discarded.
	 * @return The maximum size of an 'oversized' blocking set
	 */
	int getMaxOversized();

	/**
	 * The maximum number of matches to package into an indexed pair-wise result
	 * file. After the maximum is reached, a new indexed file is started with an
	 * index incremented by 1 (one).
	 * @return The maximum number of matches
	 */
	int getMaxMatches();

	/**
	 * The minimum number of fields from which an oversized blocking set must be
	 * formed in order for it to be kept for further processing.
	 * @return The minimum number of fields
	 */
	int getMinFields();

	/**
	 * Many OABA processes are long running. In the cases where a batch of
	 * records is being processed in a loop, this parameter sets the frequency
	 * at which the loop checks to see if processing should continue or whether
	 * processing should be discontinued.
	 * @return the polling interval
	 */
	int getInterval();

	/**
   * The threshold number of records beyond which query records are matched
   * in a batch. Below this threshold, query records are matched to the
   * reference records one at a time. Usually set to zero (0) so that
	 * batch processing is always performed, but should be set to a value
	 * larger than the number of query records if there are only a few
	 * thousand query record and the number of reference records is a
	 * few million or more.
	 * @return the batch processing threshold
	 */
	int getMaxSingle();

}
