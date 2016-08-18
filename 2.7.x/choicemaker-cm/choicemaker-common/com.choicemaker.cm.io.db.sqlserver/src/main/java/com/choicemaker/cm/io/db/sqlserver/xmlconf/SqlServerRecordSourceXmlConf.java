/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConfigurator;
import com.choicemaker.cm.io.db.base.xmlconf.ConnectionPoolDataSourceXmlConf;
import com.choicemaker.cm.io.db.sqlserver.SqlServerRecordSource;
import com.choicemaker.cm.io.db.sqlserver.SqlServerXmlUtils;

/**
 * Handling of Db Marked Record Pair sources.
 *
 * @author    Martin Buechi
 */
public class SqlServerRecordSourceXmlConf implements RecordSourceXmlConfigurator {
	
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.sqlserver.sqlServerRsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return SqlServerRecordSource.class;
	}

	/**
	 * Add a Db record source to the configuration.
	 */
	public void add(RecordSource s) throws XmlConfException {
		try {
			SqlServerRecordSource src = (SqlServerRecordSource) s;
			String fileName = src.getFileName();
			Element e = new Element(SqlServerXmlUtils.EN_RECORDSOURCE);
			e.setAttribute(SqlServerXmlUtils.AN_RS_CLASS, EXTENSION_POINT_ID);
			e.setAttribute(SqlServerXmlUtils.AN_RS_DATASOURCENAME, src.getDataSourceName());
			e.setAttribute(SqlServerXmlUtils.AN_RS_DBCONFIGURATION, src.getDbConfiguration());
			e.addContent(new Element(SqlServerXmlUtils.AN_RS_IDSQUERY).setText(src.getIdsQuery()));
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
		String dataSourceName = e.getAttributeValue(SqlServerXmlUtils.AN_RS_DATASOURCENAME);
		String dbConfiguration = e.getAttributeValue(SqlServerXmlUtils.AN_RS_DBCONFIGURATION);
		String idsQuery = e.getChildText(SqlServerXmlUtils.AN_RS_IDSQUERY);
		return new SqlServerRecordSource(fileName, model, dataSourceName, dbConfiguration, idsQuery);
	}
	
	static {
		ConnectionPoolDataSourceXmlConf.maybeInit();
	}
	
}
