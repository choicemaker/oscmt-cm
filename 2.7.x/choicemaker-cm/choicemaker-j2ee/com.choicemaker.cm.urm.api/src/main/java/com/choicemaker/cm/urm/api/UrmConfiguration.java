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