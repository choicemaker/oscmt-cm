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

import org.jdom.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.matching.gen.Relation;
import com.choicemaker.cm.matching.gen.Relations;

/**
 * XML initializer for relations.
 *
 * @author    Martin Buechi
 * @see       com.choicemaker.cm.matching.gen.Relations
 */
public class XmlRelationsInitializer implements XmlModuleInitializer {
	public final static XmlRelationsInitializer instance = new XmlRelationsInitializer();

	private XmlRelationsInitializer() { }

	public void init(Element e) throws XmlConfException {
		List relations = e.getChildren("fileRelation");
		Iterator iRelations = relations.iterator();
		while (iRelations.hasNext()) {
			Element c = (Element) iRelations.next();
			String name = c.getAttributeValue("name");
			String fileName = c.getAttributeValue("file");
			String keyType = c.getAttributeValue("keyType").intern();
			String valueType = c.getAttributeValue("valueType").intern();
			boolean reflexive = Boolean.valueOf(c.getAttributeValue("reflexive")).booleanValue();
			try {
				Relation r = Relations.readFileRelation(fileName, keyType, valueType, reflexive);
				Relations.add(name, r);
			} catch (IOException ex) {
				throw new XmlConfException("Internal error.", ex);
			}
		}
	}
	
}
