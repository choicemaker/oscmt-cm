/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.xml.base.xmlconf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConfigurator;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSource;

/**
 * Handling of XML Marked Record Pair sources.
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.2 $ $Date: 2010/03/28 09:16:58 $
 */
public class XmlMarkedRecordPairSourceXmlConf implements MarkedRecordPairSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.xml.base.xmlMrpsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return XmlMarkedRecordPairSource.class;
	}

	/**
	 * Add a XML marked record pair source to the configuration.
	 */
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			XmlMarkedRecordPairSource src = (XmlMarkedRecordPairSource) s;
			String fileName = src.getFileName();
			Element e = new Element("MarkedRecordPairSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			//e.addContent(new Element("fileName").setText(src.getXmlFileName()));
			e.addContent(new Element("fileName").setText(src.getRawXmlFileName()));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			XMLOutputter o = new XMLOutputter("    ", true);
			o.setTextNormalize(true);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException {
		String xmlFileName = e.getChildText("fileName");
		return new XmlMarkedRecordPairSource(fileName, xmlFileName, model);
	}
}
