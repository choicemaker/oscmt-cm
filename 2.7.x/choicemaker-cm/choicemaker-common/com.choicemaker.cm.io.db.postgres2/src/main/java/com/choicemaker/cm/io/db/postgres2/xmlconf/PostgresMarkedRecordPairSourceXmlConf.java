/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.cm.io.db.postgres2.xmlconf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConfigurator;
import com.choicemaker.cm.io.db.base.xmlconf.ConnectionPoolDataSourceXmlConf;
import com.choicemaker.cm.io.db.postgres2.PostgresMarkedRecordPairSource;
import com.choicemaker.cm.io.db.postgres2.PostgresXmlUtils;

/**
 * Handling of Db Marked Record Pair sources.
 *
 * @author    Martin Buechi
 */
public class PostgresMarkedRecordPairSourceXmlConf implements MarkedRecordPairSourceXmlConfigurator {
	
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.postgres2.sqlServerMrpsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return PostgresMarkedRecordPairSource.class;
	}

	/**
	 * Add a Db record source to the configuration.
	 */
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			PostgresMarkedRecordPairSource src = (PostgresMarkedRecordPairSource) s;
			String fileName = src.getFileName();
			Element e = new Element(PostgresXmlUtils.EN_MARKEDRECORDPAIRSOURCE);
			e.setAttribute(PostgresXmlUtils.AN_MRPS_CLASS, EXTENSION_POINT_ID);
			e.setAttribute(PostgresXmlUtils.AN_MRPS_DATASOURCENAME, src.getDataSourceName());
			e.setAttribute(PostgresXmlUtils.AN_MRPS_DBCONFIGURATION, src.getDbConfiguration());
			e.addContent(new Element(PostgresXmlUtils.AN_MRPS_IDSQUERY).setText(src.getMrpsQuery()));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			Format format = Format.getPrettyFormat();
			XMLOutputter o = new XMLOutputter(format);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException {
		String dataSourceName = e.getChildText(PostgresXmlUtils.AN_MRPS_DATASOURCENAME);
		String dbConfiguration = e.getAttributeValue(PostgresXmlUtils.AN_MRPS_DBCONFIGURATION);
		String mrpsQuery = e.getChildText(PostgresXmlUtils.AN_MRPS_IDSQUERY);
		return new PostgresMarkedRecordPairSource(fileName, model, dataSourceName, dbConfiguration, mrpsQuery);
	}
	
	static {
		ConnectionPoolDataSourceXmlConf.maybeInit();
	}
	
}
