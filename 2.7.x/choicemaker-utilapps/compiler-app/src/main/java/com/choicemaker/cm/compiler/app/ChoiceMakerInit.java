/*
 * Copyright (c) 2014, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package com.choicemaker.cm.compiler.app;

import java.io.File;
import java.util.logging.Logger;

import com.choicemaker.cm.core.PropertyNames;
import com.choicemaker.cm.core.WellKnownPropertyValues;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ConfigurationManager;
import com.choicemaker.util.SystemPropertyUtils;

/**
 * Initializes the ChoiceMaker environment once per JVM
 * @author rphall
 */
public final class ChoiceMakerInit {

	private static File theProjectFile = null;
	private static String theLogConfiguration = null;
	private static boolean isInitialized = false;
	private static Logger logger = Logger.getLogger(ChoiceMakerInit.class.getName());

	public static synchronized boolean isInitialized() {
		return isInitialized;
	}

	public static synchronized void initialize(
		File projectFile,
		String logConfiguration,
		boolean reload,
		boolean initGui)
		throws XmlConfException {

		if (!isInitialized) {
			// Preconditions
			if (projectFile == null) {
				throw new IllegalArgumentException("null project file");
			}
//			if (logConfiguration == null) {
//				throw new IllegalArgumentException("null log configuration name");
//			}

			SystemPropertyUtils.setPropertyIfMissing(
					PropertyNames.INSTALLABLE_GENERATOR_PLUGIN_FACTORY,
					WellKnownPropertyValues.LIST_BACKED_GENERATOR_PLUGIN_FACTORY);
			SystemPropertyUtils.setPropertyIfMissing(
					PropertyNames.INSTALLABLE_CHOICEMAKER_CONFIGURATOR,
					WellKnownPropertyValues.LIST_BACKED_CONFIGURATOR);

			ConfigurationManager.getInstance().init(
				projectFile.getAbsolutePath(),
				logConfiguration,
				reload,
				initGui);
			theProjectFile = projectFile;
			theLogConfiguration = logConfiguration;
			isInitialized = true;

		} else if (
			!theProjectFile.getAbsolutePath().equals(projectFile.getAbsolutePath())) {
			String msg =
				"Attempt to reinitialize ChoiceMaker with a new project file "
					+ "(current == '"
					+ theProjectFile.getAbsolutePath()
					+ "', new == '"
					+ projectFile.getAbsolutePath() + "')";
			throw new IllegalStateException(msg);

		} else if (!theLogConfiguration.equals(logConfiguration)) {
			logger.warning("the deprecated logging configuration is ignored: " + logConfiguration);
//			String msg =
//				"Attempt to reinitialize ChoiceMaker with a new log configuration "
//					+ "(current == '"
//					+ theLogConfiguration
//					+ "', new == '"
//					+ logConfiguration
//					+ "')";
//			throw new IllegalStateException(msg);
		}

		return;
	} // initialize(File,String,boolean,boolean)

	public static synchronized void deleteGeneratedCode() {
		if (isInitialized) {
			ConfigurationManager.getInstance().deleteGeneratedCode();
		}
	}

}
