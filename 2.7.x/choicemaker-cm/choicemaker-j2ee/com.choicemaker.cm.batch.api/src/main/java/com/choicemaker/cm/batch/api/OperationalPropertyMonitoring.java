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

import java.util.List;

/**
 * Monitors persistent, operational properties.
 * 
 * @author rphall
 */
public interface OperationalPropertyMonitoring {

	String getOperationalPropertyValue(BatchJob job, String pn);

	OperationalProperty findOperationalProperty(long propertyId);

	OperationalProperty findOperationalProperty(BatchJob job, String name);

	List<OperationalProperty> findOperationalProperties(BatchJob job);

	@Deprecated
	List<OperationalProperty> findAllOperationalProperties();

}
