/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.util.List;

import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.exceptions.ConfigException;

/**
 * Maps URM 2.5 model configuration names to CMS 2.7 named configurations.
 * 
 * @author rphall
 */
public interface UrmConfigurationAdapter {

	String getCmsConfigurationName(String urmConfigurationName)
			throws DatabaseException, ConfigException;

	UrmConfiguration findUrmConfigurationByName(String urmConfigurationName);

	UrmConfiguration findUrmConfiguration(long id);

	List<UrmConfiguration> findAllUrmConfigurations();

}
