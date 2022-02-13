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
package com.choicemaker.cm.urm.api;

import com.choicemaker.cm.args.PersistentObject;

public interface UrmConfiguration {

	public static final long DEFAULT_CONFIGURATIONID =
		PersistentObject.NONPERSISTENT_ID;

	public static final String DEFAULT_URMCONFIGURATIONNAME = "";

	public static final String DEFAULT_CMSCONFIGURATIONNAME = "";

	long getId();

	String getUrmConfigurationName();

	String getCmsConfigurationName();

}