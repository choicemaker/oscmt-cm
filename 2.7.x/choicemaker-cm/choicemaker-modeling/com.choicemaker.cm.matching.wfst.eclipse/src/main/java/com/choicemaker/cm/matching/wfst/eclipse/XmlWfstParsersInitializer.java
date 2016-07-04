/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.wfst.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.jdom.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;

/**
 * XML initializer for WfstParsers
 *
 * @author Rick Hall
 * @see com.choicemaker.cm.matching.wfst.eclipse.WfstParsers
 */
public class XmlWfstParsersInitializer implements XmlModuleInitializer {

	public final static XmlWfstParsersInitializer instance = new XmlWfstParsersInitializer();

	private XmlWfstParsersInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor) throws XmlConfException {

		List<?> parsers = e.getChildren("instance");
		Iterator<?> iParsers = parsers.iterator();
		while (iParsers.hasNext()) {
			Element c = (Element) iParsers.next();

			String name = c.getAttributeValue("name");
			String filterFileName = c.getAttributeValue("filterFile");
			String grammarFileName = c.getAttributeValue("grammarFile");
			boolean lazy = true;
			if (c.getAttribute("lazy") != null) {
				lazy = Boolean.getBoolean(c.getAttributeValue("lazy"));
			}

			try {
				File filterFile = new File(filterFileName);
				filterFile = filterFile.getAbsoluteFile();
				URL fUrl = filterFile.toURL();
				File grammarFile = new File(grammarFileName);
				grammarFile = grammarFile.getAbsoluteFile();
				URL gUrl = grammarFile.toURL();
				WfstParsers.addParser(name, fUrl, gUrl, lazy);
			} catch (MalformedURLException ex) {
				throw new XmlConfException(ex.getMessage(), ex);
			} catch (IOException ex) {
				throw new XmlConfException(ex.getMessage(), ex);
			}

		} // while

		return;
	} // init(Element)

} // XmlWfstParsersInitializer

