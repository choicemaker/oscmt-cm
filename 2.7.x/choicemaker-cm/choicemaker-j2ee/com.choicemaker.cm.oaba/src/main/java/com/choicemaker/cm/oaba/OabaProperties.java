/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba;

/**
 * System properties used by offline automated blocking classes:
 * <ul>
 * <li/>Property names begin with <code>PN_</code>
 * <li/>Default property values begin with <code>DPV_</code>
 * </ul>
 * For efficiency, type-safe accessors are provided for non-String property
 * values.
 * 
 * @author rphall
 */
public class OabaProperties {

	private OabaProperties() {
	}

	// Property names

	/**
	 * The name of a system property that can be set to "true" to force the
	 * {@link com.choicemaker.cm.oaba.result.MRPSCreator
	 * MRPSCreator} class to dump the record pairs held by its
	 * {@link com.choicemaker.cm.analyzer.sampler.PairSampler Sampler} delegate
	 * between batch computations.
	 * <p>
	 * <strong>Note that record pairs are dumped to an insecure, temporary
	 * file.</strong>
	 * </p>
	 */
	public static final String PN_RESULT_MRPSCREATOR_DUMP_SAMPLER =
		"com.choicemaker.cm.oaba.result.MRPSCreator.dumpSampler";

	// Public, type-safe accessors for non-String properties

	/**
	 * Returns the value of the System propery
	 * {@link #PN_RESULT_MRPSCREATOR_DUMP_SAMPLER} as a primitive boolean.
	 */
	public static boolean isSamplerDumped() {
		// NOTE getBoolean is consistent with the current default value of this
		// property
		boolean retVal = Boolean.getBoolean(PN_RESULT_MRPSCREATOR_DUMP_SAMPLER);
		return retVal;
	}

}
