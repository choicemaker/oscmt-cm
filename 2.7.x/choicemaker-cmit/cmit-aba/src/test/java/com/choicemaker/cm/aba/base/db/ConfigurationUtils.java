/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.aba.base.db;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlParserFactory;

public class ConfigurationUtils {

	private static final Logger logger = Logger.getLogger(ConfigurationUtils.class
			.getName());

	static final String SQLSERVER_CONFIG_FILE = "sqlserver-configuration.xml";
	
	static final String TARGET_NAME = "localhost";

	static Document readConfigurationFromResource(String path)
			throws XmlConfException, JDOMException, IOException {
		SAXBuilder builder = XmlParserFactory.createSAXBuilder(false);
		URL url = ConfigurationUtils.class.getClassLoader().getResource(path);
		if (url == null) {
			String msg = "Unable to load '" + path + "'";
			logger.severe(msg);
			throw new XmlConfException(msg);
		}
		Document document = builder.build(url);
		return document;
	}

	private ConfigurationUtils() {
	}

}
