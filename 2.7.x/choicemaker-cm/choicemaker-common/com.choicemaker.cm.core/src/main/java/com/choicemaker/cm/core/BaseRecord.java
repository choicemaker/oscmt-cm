/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.Serializable;

/**
 * Base interface for all generated record holder class.
 * This interface is implemented by <em>both</em> the root and
 * all nested record holder classes.
 *
 * @author    Martin Buechi
 */
public interface BaseRecord extends Serializable {
	/**
	 * Computes the validity of fields and the values of derived fields.
	 *
	 * @param   src  The <code>DerivedSource</code> from which this record was read in. Used to
	 *            determine which fields must be computed.
	 * @see     DerivedSource
	 */
	void computeValidityAndDerived(DerivedSource src);

	/**
	 * Set the validity of all fields to false and the value of all derived fields
	 * to null/0.
	 * @param   src  The <code>DerivedSource</code> from which this record was read in. Used to
	 *            determine which fields must be computed.
	 * @see     DerivedSource
	 */
	void resetValidityAndDerived(DerivedSource src);
}
