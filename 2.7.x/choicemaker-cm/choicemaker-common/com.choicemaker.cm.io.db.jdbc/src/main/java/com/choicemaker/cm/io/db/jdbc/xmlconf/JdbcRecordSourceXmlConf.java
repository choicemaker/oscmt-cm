/*
 * @(#)$RCSfile: JdbcRecordSourceXmlConf.java,v $        $Revision: 1.1.78.1 $ $Date: 2009, 2017/11/18 01:00:19 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 41 East 11th Street, New York, NY 10003
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */

package com.choicemaker.cm.io.db.jdbc.xmlconf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConfigurator;
import com.choicemaker.cm.core.xmlconf.XmlConfException;
import com.choicemaker.cm.io.db.base.xmlconf.ConnectionPoolDataSourceXmlConf;
import com.choicemaker.cm.io.db.jdbc.JdbcRecordSource;

/**
 * Handling of Db Marked Record Pair sources.
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.1.78.1 $ $Date: 2009/11/18 01:00:19 $
 */
public class JdbcRecordSourceXmlConf implements RecordSourceXmlConfigurator {
	
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.db.db2.DB2RsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return JdbcRecordSource.class;
	}

	/**
	 * Add a Db record source to the configuration.
	 *
	 * @param   name  The name of the source.
	 * @param   probabilityModel  The name of the probability accessProvider.
	 * @param   probabilityModelConfig  The name of the configuration containing the probability accessProvider.
	 * @param   selection  The selection.
	 * @param   connectionName  The name of the connection to access this source.
	 * @param   replace  Whether an exiting probability accessProvider of the same name should be replaced.
	 *            If the value of <code>replace</code> is <code>false</code> and a accessProvider of the
	 *            same name already exists, an exception is thrown.
	 * @throws  XmlConfException  if an exception occurs.
	 */
	public void add(RecordSource s) throws XmlConfException {
		try {
			JdbcRecordSource src = (JdbcRecordSource) s;
			String fileName = src.getFileName();
			Element e = new Element("RecordSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			e.setAttribute("dataSourceName", src.getDataSourceName());
			e.setAttribute("dbConfiguration", src.getDbConfiguration());
			e.addContent(new Element("idsQuery").setText(src.getIdsQuery()));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			XMLOutputter o = new XMLOutputter("    ", true);
			o.setTextNormalize(true);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	public RecordSource getRecordSource(String fileName, Element e, IProbabilityModel model)
		throws XmlConfException {
		String dataSourceName = e.getAttributeValue("dataSourceName");
		String dbConfiguration = e.getAttributeValue("dbConfiguration");
		String idsQuery = e.getChildText("idsQuery");
		return new JdbcRecordSource(fileName, model, dataSourceName, dbConfiguration, idsQuery);
	}
	
	static {
		ConnectionPoolDataSourceXmlConf.maybeInit();
	}
	
}
