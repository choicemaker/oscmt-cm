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
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConfigurator;
import com.choicemaker.cm.io.db.base.xmlconf.ConnectionPoolDataSourceXmlConf;
import com.choicemaker.cm.io.db.postgres2.PostgresParallelRecordSource;
import com.choicemaker.cm.io.db.postgres2.PostgresXmlUtils;

/**
 * Handling of Postgres Marked Record Pair sources.
 * Based on SqlServer class of similar name.
 *
 * @author rphall
 */
public class PostgresRecordSourceXmlConf implements RecordSourceXmlConfigurator {

	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.postgres2.postgresRsReader";

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return PostgresParallelRecordSource.class;
	}

	/**
	 * Add a Db record source to the configuration.
	 */
	@Override
	public void add(RecordSource s) throws XmlConfException {
		try {
			PostgresParallelRecordSource src = (PostgresParallelRecordSource) s;
			String fileName = src.getFileName();
			Element e = new Element(PostgresXmlUtils.EN_RECORDSOURCE);
			e.setAttribute(PostgresXmlUtils.AN_RS_CLASS, EXTENSION_POINT_ID);
			e.setAttribute(PostgresXmlUtils.AN_RS_DATASOURCENAME, src.getDataSourceName());
			e.setAttribute(PostgresXmlUtils.AN_RS_DBCONFIGURATION, src.getDbConfiguration());
			e.addContent(new Element(PostgresXmlUtils.AN_RS_IDSQUERY).setText(src.getIdsQuery()));
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
	public RecordSource getRecordSource(String fileName, Element e, ImmutableProbabilityModel model)
			throws XmlConfException {
		String dataSourceName = e.getAttributeValue(PostgresXmlUtils.AN_RS_DATASOURCENAME);
		String dbConfiguration = e.getAttributeValue(PostgresXmlUtils.AN_RS_DBCONFIGURATION);
		String idsQuery = e.getChildText(PostgresXmlUtils.AN_RS_IDSQUERY);
		return new PostgresParallelRecordSource(fileName, model, dataSourceName, dbConfiguration, idsQuery);
	}

	static {
		ConnectionPoolDataSourceXmlConf.maybeInit();
	}

}
