/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
 * Manages persistent, indexed properties associated with a batch job.
 * 
 * @author rphall
 */
public interface IndexedPropertyController extends IndexedPropertyMonitoring {

	void setIndexedPropertyValue(BatchJob job, String pn, int index, String pv);

	IndexedProperty saveIndexedProperty(IndexedProperty property);

	void removeIndexedProperty(IndexedProperty property);

	/** Returns a count of the number of properties deleted */
	int deleteIndexedPropertiesByJobIdName(long jobId, String name);

}
