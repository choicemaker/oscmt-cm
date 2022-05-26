/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.xmlconf;

import java.net.URL;

import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class XmlParserFactory {

	public static SAXBuilder createSAXBuilder(boolean validate) {
		ClassLoader oldCl = setClassLoader();
		SAXBuilder builder = new SAXBuilder(validate);
		restoreClassLoader(oldCl);

		return builder;
	}

	public static XMLReader createXMLReader() throws SAXException {
		ClassLoader oldCl = setClassLoader();
//		XMLReader reader = XMLReaderFactory.createXMLReader(className);
		XMLReader reader = XMLReaderFactory.createXMLReader();
		restoreClassLoader(oldCl);

		return reader;
	}

	public static XMLReader createXMLReader(String /* ignored */className) throws SAXException {
		return createXMLReader();
	}

	public static ClassLoader setClassLoader() {
		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(XmlParserFactory.class.getClassLoader());
		return oldCl;
	}

	public static void restoreClassLoader(ClassLoader oldClassLoader) {
		if (oldClassLoader == null) {
			throw new IllegalArgumentException();
		}
		Thread.currentThread().setContextClassLoader(oldClassLoader);
	}

	public static final String CHOICEMAKER_XSD_URL =
		"https://www.choicemaker.com/xml_schemas/2.6/ChoiceMakerAll.xsd";

	public static boolean connected() {
		try {
			// URL url = new URL("http://www.choicemaker.com/");
			URL url = new URL(CHOICEMAKER_XSD_URL);
			url.getContent();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
