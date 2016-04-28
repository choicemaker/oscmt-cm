/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.IOException;

import com.choicemaker.cm.core.report.Reporter;

/**
 * Manages a collection of IProbabilityModel instances.
 * @author rphall (Based on earlier PMManager and ProbabilityModel classes)
 */
public interface IProbabilityModelManager {

	/**
	 * Load models from the plugin registry into this manager
	 * @return the number of plugins loaded
	 */
	int loadModelPlugins() throws ModelConfigurationException, IOException;

	/**
	 * Adds a probability model to the collection of configured models.
	 *
	 * @param   model  The probability model.
	 */
	void addModel(IProbabilityModel model);

	Accessor createAccessor(String className, ClassLoader cl)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	Reporter[] getGlobalReporters();

	/**
	 * Returns the specified probability model.
	 *
	 * @return  The specified probability model.
	 */
	ImmutableProbabilityModel getImmutableModelInstance(String name);

	/**
	 * Returns the specified probability model.
	 *
	 * @return  The specified probability model.
	 */
	IProbabilityModel getModelInstance(String name);

	IProbabilityModel[] getModels();

//	/** Returns an unmodifiable map of names to models */
//	Map models();

	void setGlobalReporters(Reporter[] rs);

}
