package com.choicemaker.cms.beans;

import com.choicemaker.cms.api.AbaSettings;

public class AbaSettingsBean implements AbaSettings {

	private static final long serialVersionUID = 1L;

	private long id = NONPERSISTENT_SETTINGS_ID;
	private int limitPerBlockingSet = DEFAULT_LIMIT_PER_BLOCKING_SET;
	private int limitSingleBlockingSet = DEFAULT_LIMIT_SINGLE_BLOCKING_SET;
	private int singleTableBlockingSetGraceLimit =
		DEFAULT_SINGLE_TABLE_GRACE_LIMIT;

	@Override
	public long getId() {
		return id;
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
		return id != NONPERSISTENT_SETTINGS_ID;
	}

	public void setLimitPerBlockingSet(int limitPerBlockingSet) {
		this.limitPerBlockingSet = limitPerBlockingSet;
	}

	public void setSingleTableBlockingSetGraceLimit(
			int singleTableBlockingSetGraceLimit) {
		this.singleTableBlockingSetGraceLimit =
			singleTableBlockingSetGraceLimit;
	}

}
