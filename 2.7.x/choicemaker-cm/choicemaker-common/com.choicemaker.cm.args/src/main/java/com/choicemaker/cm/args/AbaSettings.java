/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.args;

import java.io.Serializable;

/**
 * Settings for the online/real-time ABA (Automated Batch Algorithm) are
 * matching parameters that change relatively infrequently. Prior to version 2.7
 * of ChoiceMaker, these settings were recorded as properties of the
 * <code>ImmutableProbabilityModel</code> interface.
 * 
 * @author rphall
 *
 */
public interface AbaSettings extends Serializable {

	/**
	 * The default value for {@link #getAbaMaxMatches()}. The value 0 (zero)
	 * indicates no maximum.
	 */
	int DEFAULT_ABA_MAX_MATCHES = 0;

	/** The default value for {@link #getLimitPerBlockingSet()} */
	int DEFAULT_LIMIT_PER_BLOCKING_SET = 50;

	/** The default values for {@link #getLimitSingleBlockingSet()} */
	int DEFAULT_LIMIT_SINGLE_BLOCKING_SET = 100;

	/** The default value for {@link #getSingleTableBlockingSetGraceLimit()} */
	int DEFAULT_SINGLE_TABLE_GRACE_LIMIT = 200;

	/** Default id value for non-persistent settings */
	long NONPERSISTENT_ABA_SETTINGS_ID = 0;

	/**
	 * @return The persistence identifier for an instance. If the value is
	 * {@link #NONPERSISTENT_ABA_SETTINGS_ID}, then the settings are not
	 * persistent.
	 */
	long getId();

	boolean isPersistent();

	/**
	 * @return The maximum number of matches returned by Online matching. The value 0
	 * (zero) indicates no maximum; that is, no limit to the number of matches
	 * returned.
	 */
	int getAbaMaxMatches();

	/**
	 * @return The maximum of size of a blocking set before it must be refined by
	 * qualifying it with additional blocking values.
	 */
	int getLimitPerBlockingSet();

	/**
	 * @return A special exemption to the {@link #getLimitPerBlockingSet() general limit
	 * on blocking set size}. If only one blocking set is formed, then its size is
	 * limited by this value.
	 */
	int getLimitSingleBlockingSet();

	/**
	 * @return Another special exemption to the {@link #getLimitPerBlockingSet() general
	 * limit on blocking set size}. If a blocking does not require joins between tables,
	 * then its size is limited by this value.
	 */
	int getSingleTableBlockingSetGraceLimit();

}
