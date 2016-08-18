/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.gen.xmlconf;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.matching.gen.Sets;

/**
 * XML initializer for collections (sets).
 * <p>
 * see com.choicemaker.cm.matching.gen.Colls
 * </p>
 *
 * @author Martin Buechi
 */
public class XmlSetsInitializer implements XmlModuleInitializer {
	public final static XmlSetsInitializer instance = new XmlSetsInitializer();

	private XmlSetsInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor) throws XmlConfException {
		List<?> colls = e.getChildren();
		Iterator<?> iColls = colls.iterator();
		while (iColls.hasNext()) {
			Element c = (Element) iColls.next();
			if (!c.getName().equals("fileSet")) {
				throw new XmlConfException(
						"Only file sets are currently supported.");
			}
			String name = c.getAttributeValue("name");
			String fileName = c.getAttributeValue("file");
			try {
				Set<String> s = Sets.readFileSet(fileName);
				Sets.addCollection(name, s);
			} catch (IOException ex) {
				throw new XmlConfException("Error reading file: " + fileName,
						ex);
			}
		}
	}

}
