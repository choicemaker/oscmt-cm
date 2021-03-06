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
package com.choicemaker.cm.core.configure.jdom;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.choicemaker.cm.core.configure.xml.IBuilder;
import com.choicemaker.cm.core.configure.xml.IDocument;
import com.choicemaker.cm.core.configure.xml.XmlSpecificationException;
import com.choicemaker.cm.core.xmlconf.XmlParserFactory;

/**
 * @author rphall
 */
public class JdomBuilder implements IBuilder {

	private static Logger logger = Logger.getLogger(JdomBuilder.class.getName());

	private final SAXBuilder builder;

	public JdomBuilder() {
		this.builder = XmlParserFactory.createSAXBuilder(false);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IBuilder#build(java.io.Reader)
	 */
	@Override
	public IDocument build(Reader characterStream)
		throws XmlSpecificationException, IOException {
		IDocument retVal;
		try {
			Document document = this.getSAXBuilder().build(characterStream);
			retVal = new JdomDocument(document);
		} catch (JDOMException x) {
			String msg = "Unable to build document: " + x.toString();
			logger.severe(msg);
			throw new XmlSpecificationException(msg, x);
		}
		return retVal;
	}

	public SAXBuilder getSAXBuilder() {
		return builder;
	}

}
