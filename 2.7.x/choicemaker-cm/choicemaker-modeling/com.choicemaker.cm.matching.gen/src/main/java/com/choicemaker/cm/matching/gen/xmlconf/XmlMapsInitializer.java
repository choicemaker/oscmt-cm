/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.gen.xmlconf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jasypt.encryption.StringEncryptor;
import org.jdom.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.matching.gen.Maps;

/**
 * XML initializer for mapections (sets).
 *
 * @author Martin Buechi
 * @see com.choicemaker.cm.matching.gen.Maps
 */
public class XmlMapsInitializer implements XmlModuleInitializer {
	public final static XmlMapsInitializer instance = new XmlMapsInitializer();

	private XmlMapsInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor)
			throws XmlConfException {
		@SuppressWarnings("unchecked")
		List<Element> maps = e.getChildren("fileMap");
		for (Object o : maps) {
			Element c = (Element) o;

			String name = c.getAttributeValue("name");
			String fileName = c.getAttributeValue("file");
			String keyType = c.getAttributeValue("keyType").intern();
			String valueType = c.getAttributeValue("valueType").intern();

			boolean singleLine = "true".equals(c
					.getAttributeValue("singleLine"));

			try {
				@SuppressWarnings("rawtypes")
				Map m = null;
				if (singleLine) {
					m = Maps.readSingleLineMap(fileName, keyType, valueType);
				} else {
					m = Maps.readFileMap(fileName, keyType, valueType);
				}
				Maps.addMap(name, m);
			} catch (IOException ex) {
				throw new XmlConfException("Error reading file: " + fileName,
						ex);
			}
		}
	}

}
