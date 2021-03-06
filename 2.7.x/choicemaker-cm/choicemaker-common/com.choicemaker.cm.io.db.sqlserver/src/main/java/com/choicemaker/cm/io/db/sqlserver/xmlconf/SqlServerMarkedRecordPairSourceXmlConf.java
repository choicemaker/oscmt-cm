/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.cm.io.db.sqlserver.xmlconf;

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
import com.choicemaker.cm.io.db.sqlserver.SqlServerMarkedRecordPairSource;
import com.choicemaker.cm.io.db.sqlserver.SqlServerXmlUtils;

/**
 * Handling of Db Marked Record Pair sources.
 *
 * @author    Martin Buechi
 */
public class SqlServerMarkedRecordPairSourceXmlConf implements MarkedRecordPairSourceXmlConfigurator {
	
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.sqlserver.sqlServerMrpsReader";

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return SqlServerMarkedRecordPairSource.class;
	}

	/**
	 * Add a Db record source to the configuration.
	 */
	@Override
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			SqlServerMarkedRecordPairSource src = (SqlServerMarkedRecordPairSource) s;
			String fileName = src.getFileName();
			Element e = new Element(SqlServerXmlUtils.EN_MARKEDRECORDPAIRSOURCE);
			e.setAttribute(SqlServerXmlUtils.AN_MRPS_CLASS, EXTENSION_POINT_ID);
			e.setAttribute(SqlServerXmlUtils.AN_MRPS_DATASOURCENAME, src.getDataSourceName());
			e.setAttribute(SqlServerXmlUtils.AN_MRPS_DBCONFIGURATION, src.getDbConfiguration());
			e.addContent(new Element(SqlServerXmlUtils.AN_MRPS_IDSQUERY).setText(src.getMrpsQuery()));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			Format format = Format.getPrettyFormat();
			XMLOutputter o = new XMLOutputter(format);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	@Override
	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException {
		String dataSourceName = e.getChildText(SqlServerXmlUtils.AN_MRPS_DATASOURCENAME);
		String dbConfiguration = e.getAttributeValue(SqlServerXmlUtils.AN_MRPS_DBCONFIGURATION);
		String mrpsQuery = e.getChildText(SqlServerXmlUtils.AN_MRPS_IDSQUERY);
		return new SqlServerMarkedRecordPairSource(fileName, model, dataSourceName, dbConfiguration, mrpsQuery);
	}
	
	static {
		ConnectionPoolDataSourceXmlConf.maybeInit();
	}
	
}
