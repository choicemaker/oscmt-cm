package com.choicemaker.cms.beans;

import com.choicemaker.cms.api.AbaServerConfiguration;

public class AbaServerConfigurationBean implements AbaServerConfiguration {

	private static final long serialVersionUID = 1L;

	private long id = NONPERSISTENT_SERVER_CONFIG_ID;
	private int abaMinThreadCount = DEFAULT_THREAD_COUNT;
	private int abaMaxThreadCount = DEFAULT_THREAD_COUNT;

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
