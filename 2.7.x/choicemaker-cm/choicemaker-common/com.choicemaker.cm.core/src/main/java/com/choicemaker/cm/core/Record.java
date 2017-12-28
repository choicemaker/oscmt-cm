/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;


/**
 * Base interface for information about an entity.
 *
 * @author    Martin Buechi
 */
public interface Record<T extends Comparable<T>> extends Identifiable<T>, BaseRecord {

	// Moved to Identifiable
//	/**
//	 * Returns a key that uniquely identifies an entity. If two records
//	 * have different identifiers, then they represent different entities
//	 * (in the absence of duplicates).
//	 */
//	T getId();

	/**
	 * Computes non-persistent or cached fields and rows, and checks
	 * whether each field (derived and intrinsic) is valid. If a
	 * field is valid, it marked internally as valid and this
	 * information is available to implementations of this interface.
	 */
	void computeValidityAndDerived();

	/**
	 * Returns a flag indicating the origin of a record. This flag can be
	 * used in calculating derived values, which may depend on the source
	 * of a record.
	 */
	DerivedSource getDerivedSource();
}
