/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.batch.api;

/**
 * Batch jobs tend to require significant amounts of time to complete if all of
 * their results are fully computed. In some cases, the results of a batch job
 * can be estimated in much shorter amounts of time, without going through a
 * full and rigorous computation. For some applications, these estimates may be
 * good enough.
 *
 * @author rphall
 *
 */
public enum BatchJobRigor {

	/** A batch job whose results are estimated */
	ESTIMATED('E'),

	/** A batch job whose results are fully computed */
	COMPUTED('C');

	public final char symbol;

	BatchJobRigor(char c) {
		symbol = c;
	}

	public static BatchJobRigor valueOf(char c) {
		BatchJobRigor retVal = null;
		switch (c) {
		case 'E':
		case 'e':
			retVal = ESTIMATED;
			break;
		case 'C':
		case 'c':
			retVal = COMPUTED;
			break;
		default:
			throw new IllegalArgumentException(
					"BatchJobRigor: invalid symbol: " + c);
		}
		assert retVal != null;
		return retVal;
	}

}
