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

import java.io.Serializable;

import com.choicemaker.cm.args.PersistentObject;

/**
 * An indexed property is a property that is an element in an associative array
 * that is keyed by integers. Indexed properties are often used to store results
 * of a batch job.
 *
 * @author rphall
 */
public interface IndexedProperty extends PersistentObject, Serializable {

	/** The identifier of the job that owns this operational property */
	long getJobId();

	/** The property name */
	String getName();

	/** The property index */
	int getIndex();

	/** The property value */
	String getValue();

	void updateValue(String v);

}