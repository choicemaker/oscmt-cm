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

import com.choicemaker.cm.args.AbaSettings;

public class AbaSettingsBean implements AbaSettings {

	private static final long serialVersionUID = 1L;

	private long id = NONPERSISTENT_ABA_SETTINGS_ID;
	private int abaMaxMatches = DEFAULT_ABA_MAX_MATCHES;
	private int limitPerBlockingSet = DEFAULT_LIMIT_PER_BLOCKING_SET;
	private int limitSingleBlockingSet = DEFAULT_LIMIT_SINGLE_BLOCKING_SET;
	private int singleTableBlockingSetGraceLimit =
		DEFAULT_SINGLE_TABLE_GRACE_LIMIT;

	public AbaSettingsBean() {
	}

	/** Treats a non-persistent copy of the specified template */
	public AbaSettingsBean(AbaSettings template) {
		this.limitPerBlockingSet = template.getLimitPerBlockingSet();
		this.limitSingleBlockingSet = template.getLimitSingleBlockingSet();
		this.singleTableBlockingSetGraceLimit =
			template.getSingleTableBlockingSetGraceLimit();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public int getAbaMaxMatches() {
		return abaMaxMatches;
	}

	@Override
	public int getLimitPerBlockingSet() {
		return limitPerBlockingSet;
	}

	@Override
	public int getLimitSingleBlockingSet() {
		return limitSingleBlockingSet;
	}

	@Override
	public int getSingleTableBlockingSetGraceLimit() {
		return singleTableBlockingSetGraceLimit;
	}

	@Override
	public boolean isPersistent() {
		return id != NONPERSISTENT_ABA_SETTINGS_ID;
	}

	public void setAbaMaxMatches(int abaMaxMatches) {
		this.abaMaxMatches = abaMaxMatches;
	}

	public void setLimitPerBlockingSet(int limitPerBlockingSet) {
		this.limitPerBlockingSet = limitPerBlockingSet;
	}

	public void setLimitSingleBlockingSet(int limitSingleBlockingSet) {
		this.limitSingleBlockingSet = limitSingleBlockingSet;
	}

	public void setSingleTableBlockingSetGraceLimit(
			int singleTableBlockingSetGraceLimit) {
		this.singleTableBlockingSetGraceLimit =
			singleTableBlockingSetGraceLimit;
	}

}
