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
 * An operational property is some property computed during a batch job that
 * needs to be retained temporarily.<br/>
 * <br/>
 * After a job is successfully completed, operational properties can be
 * discarded without loss of information needed to repeat the job. However, if a
 * job fails or is aborted, operational properties may be needed to resume the
 * job at the point where it stopped.
 *
 * @author rphall
 */
public interface OperationalProperty extends PersistentObject, Serializable {

	/** The identifier of the job that owns this operational property */
	long getJobId();

	/** The property name */
	String getName();

	/** The property value */
	String getValue();

	void updateValue(String v);

}