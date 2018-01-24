package com.choicemaker.cm.oaba.api;

public interface DefaultSettingsPK {

	String getModel();

	String getType();

	String getDatabaseConfiguration();

	String getBlockingConfiguration();

	int hashCode();

	boolean equals(Object obj);

}