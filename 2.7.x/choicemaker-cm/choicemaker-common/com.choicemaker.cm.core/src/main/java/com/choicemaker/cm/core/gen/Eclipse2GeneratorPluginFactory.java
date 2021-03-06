/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.core.gen;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;

public class Eclipse2GeneratorPluginFactory implements
		IGeneratorPluginFactory {

	private static final Logger logger = Logger
			.getLogger(Eclipse2GeneratorPluginFactory.class.getName());

	public static final String EXTENSION_POINT = ChoiceMakerExtensionPoint.CM_CORE_GENERATORPLUGIN;

	public static final String EXTENSION_EXECUTABLE_PROPERTY = "class";

	// An unmodifiable list of generator plugins
	private final List generatorPlugins;

	/**
	 * A default constructor method that looks up a System property to determine
	 * the list of plugins to install
	 */
	public Eclipse2GeneratorPluginFactory() throws IllegalArgumentException {
		this(load());
	}

	/**
	 * A constructor method that loads the specified list of generator plugins
	 */
	public Eclipse2GeneratorPluginFactory(List generatorPlugins) {
		this.generatorPlugins = ListBackedGeneratorPluginFactory.validateAndCopy(generatorPlugins);
	}

	/**
	 * Loads generator plugins from the Eclipse 2.1 registry
	 *
	 * @return a non-null list (possibly empty)
	 * @throws IllegalStateException if exactly one configuration does not
	 * exist for each GeneratorPlugin defined in the registry,
	 */
	public static List load() throws IllegalStateException {
		final String msgPrefix =
			"Loading generatorPlugins from an Eclipse 2 registry: ";
		List retVal = new LinkedList();
		CMExtension[] extensions =
			CMPlatformUtils.getExtensions(EXTENSION_POINT);
		for (int i = 0; i < extensions.length; i++) {
			CMExtension extension = extensions[i];
			CMConfigurationElement[] elems =
				extension.getConfigurationElements();
			if (elems.length == 0) {
				String msg =
					msgPrefix + "no configurations for '"
							+ extension.toString() + "'";
				throw new IllegalStateException(msg);
			} else if (elems.length > 1) {
				String msg =
					msgPrefix + "multiple configurations for '"
							+ extension.toString() + "'";
				throw new IllegalStateException(msg);
			}
			try {
				GeneratorPlugin gp =
					(GeneratorPlugin) elems[0]
							.createExecutableExtension(EXTENSION_EXECUTABLE_PROPERTY);
				logger.fine("Generator: '" + gp.toString() + "'");
				retVal.add(gp);
			} catch (Exception e) {
				String msg = msgPrefix + e.toString() + ": " + e.getCause();
				throw new IllegalStateException(msg);
			}
		}
		return retVal;
	}

	@Override
	public List lookupGeneratorPlugins() {
		return this.generatorPlugins;
	}

}
