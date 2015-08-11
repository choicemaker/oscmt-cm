/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base.xmlconf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConfigurator;
import com.choicemaker.cm.io.xml.base.XmlRecordSource;

/**
 * Handling of XML Marked Record Pair sources.
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.2 $ $Date: 2010/03/28 09:16:58 $
 */
public class XmlRecordSourceXmlConf implements RecordSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.xml.base.xmlRsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return XmlRecordSource.class;
	}

	/**
	 * Add a XML marked record pair source to the configuration.
	 */
	public void add(RecordSource s) throws XmlConfException {
		try {
			XmlRecordSource src = (XmlRecordSource) s;
			String fileName = src.getFileName();
			Element e = new Element("RecordSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			e.addContent(new Element("fileName").setText(src.getRawXmlFileName()));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			XMLOutputter o = new XMLOutputter("    ", true);
			o.setTextNormalize(true);
			o.output(new Document(e), fs);
			fs.close();
		} catch (FileNotFoundException ex) {
			throw new XmlConfException(ex.getMessage(), ex);
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	public RecordSource getRecordSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException {
		String xmlFileName = e.getChildText("fileName");
		return new XmlRecordSource(fileName, xmlFileName, model);
	}
}
