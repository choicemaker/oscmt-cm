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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConfigurator;
import com.choicemaker.cm.io.db.oracle.OracleRecordSource;

/**
 * Handling of Db Marked Record Pair sources.
 *
 * @author    Martin Buechi
 */
public class DbRecordSourceXmlConf implements RecordSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.base.dbRsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return OracleRecordSource.class;
	}

	/**
	 * Add a Db record source to the configuration.
	 */
	public void add(RecordSource s) throws XmlConfException {
		try {
			OracleRecordSource src = (OracleRecordSource) s;
			String fileName = src.getFileName();
			Element e = new Element("RecordSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			e.setAttribute("conf", src.getConf());
			e.addContent(new Element("selection").setText(src.getSelection()));
			e.addContent(new Element("connectionName").setText(src.getDataSourceName()));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			Format format = Format.getPrettyFormat();
			XMLOutputter o = new XMLOutputter(format);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	public RecordSource getRecordSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException {
		String conf = e.getAttributeValue("conf");
		String selection = e.getChildText("selection");
		String connectionName = e.getChildText("connectionName");
		return new OracleRecordSource(fileName, connectionName, model, conf, selection);
	}
}
