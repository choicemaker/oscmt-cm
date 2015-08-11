/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base.xmlconf;

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
import com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource2;

/**
 * Handling of Db Marked Record Pair sources.
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.2 $ $Date: 2010/03/24 22:39:47 $
 */
public class DbMarkedRecordPairSourceXmlConf implements MarkedRecordPairSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.base.dbMrpsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return OracleMarkedRecordPairSource2.class;
	}

	/**
	 * Add a Db marked record pair source to the configuration.
	 */
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			OracleMarkedRecordPairSource2 src = (OracleMarkedRecordPairSource2) s;
			String fileName = src.getFileName();
			Element e = new Element("MarkedRecordPairSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			e.setAttribute("conf", src.getConf());
			e.addContent(new Element("selection").setText(src.getSelection()));
			e.addContent(new Element("connectionName").setText(src.getDataSourceName()));
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
		String conf = e.getAttributeValue("conf");
		String selection = e.getChildText("selection");
		String connectionName = e.getChildText("connectionName");
		return new OracleMarkedRecordPairSource2(fileName, connectionName, model, conf, selection);
	}
}
