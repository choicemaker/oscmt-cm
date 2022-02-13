/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

public interface DefaultSettings {

	DefaultSettingsPK getPrimaryKey();

	String getModel();

	String getType();

	String getDatabaseConfiguration();

	String getBlockingConfiguration();

	long getSettingsId();

}