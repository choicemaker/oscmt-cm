/*******************************************************************************
 * Copyright (c) 2007, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.wcohen.ss.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;

/**
 * XML initializer for StringDistances
 *
 * @author Rick Hall
 * @see com.wcohen.ss.eclipse.StringDistances
 */
public class XmlStringDistanceInitializer implements XmlModuleInitializer {

	public final static XmlStringDistanceInitializer instance = new XmlStringDistanceInitializer();

	private XmlStringDistanceInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor)
			throws XmlConfException {

		List<?> distances = e.getChildren("instance");
		Iterator<?> iDistances = distances.iterator();
		while (iDistances.hasNext()) {
			Element c = (Element) iDistances.next();

			String name = c.getAttributeValue("name");
			String fileName = c.getAttributeValue("file");
			String fileFormatName = c.getAttributeValue("fileFormat");
			String fileFormatVersion = c.getAttributeValue("fileFormatVersion");

			try {
				File file = new File(fileName);
				file = file.getAbsoluteFile();
				URL fUrl = file.toURL();
				FileFormat fileFormat = FileFormat.getInstance(fileFormatName);
				StringDistances.addStringDistance(name, fUrl, fileFormat,
						fileFormatVersion);
			} catch (IOException ex) {
				throw new XmlConfException(ex.getMessage(), ex);
			}

		} // while

		return;
	} // init(Element)

} // XmlStringDistanceInitializer

