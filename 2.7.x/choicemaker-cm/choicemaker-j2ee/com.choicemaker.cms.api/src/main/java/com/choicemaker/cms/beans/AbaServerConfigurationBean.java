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
package com.choicemaker.cms.beans;

import com.choicemaker.cms.api.AbaServerConfiguration;

public class AbaServerConfigurationBean implements AbaServerConfiguration {

	private static final long serialVersionUID = 1L;

	private long id = NONPERSISTENT_SERVER_CONFIG_ID;
	private int abaMinThreadCount = DEFAULT_THREAD_COUNT;
	private int abaMaxThreadCount = DEFAULT_THREAD_COUNT;

	public AbaServerConfigurationBean() {
	}

	/** Treats a non-persistent copy of the specified template */
	public AbaServerConfigurationBean(AbaServerConfiguration template) {
		this.abaMinThreadCount = template.getAbaMinThreadCount();
		this.abaMaxThreadCount = template.getAbaMaxThreadCount();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public boolean isPersistent() {
		return id != NONPERSISTENT_SERVER_CONFIG_ID;
	}

	@Override
	public int getAbaMinThreadCount() {
		return abaMinThreadCount;
	}

	@Override
	public int getAbaMaxThreadCount() {
		return abaMaxThreadCount;
	}

	public void setAbaMinThreadCount(int abaMinThreadCount) {
		this.abaMinThreadCount = abaMinThreadCount;
	}

	public void setAbaMaxThreadCount(int abaMaxThreadCount) {
		this.abaMaxThreadCount = abaMaxThreadCount;
	}

}
