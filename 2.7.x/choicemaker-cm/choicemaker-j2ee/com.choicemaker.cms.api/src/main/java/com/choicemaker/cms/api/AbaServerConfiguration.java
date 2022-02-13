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
package com.choicemaker.cms.api;

import java.io.Serializable;

public interface AbaServerConfiguration extends Serializable {

	/** Default id value for non-persistent settings */
	long NONPERSISTENT_SERVER_CONFIG_ID = 0;

	int DEFAULT_THREAD_COUNT = 1;

	/**
	 * The persistence identifier for an instance. If the value is
	 * {@link #NONPERSISTENT_SERVER_CONFIG_ID}, then the configuration is not
	 * persistent.
	 */
	long getId();

	boolean isPersistent();

	int getAbaMinThreadCount();

	int getAbaMaxThreadCount();

}
