package com.choicemaker.cm.oaba.api;

public interface DefaultSettingsPK {

	String getModel();

	String getType();

	String getDatabaseConfiguration();

	String getBlockingConfiguration();

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);

}