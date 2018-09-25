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
