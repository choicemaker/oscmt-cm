/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base.xmlconf;

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
import com.choicemaker.cm.io.flatfile.base.FlatFileMarkedRecordPairSource;

/**
 * Handling of XML Marked Record Pair sources.
 *
 * @author    Martin Buechi
 */
public class FlatFileMarkedRecordPairSourceXmlConf implements MarkedRecordPairSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.flatfile.base.flatfileMrpsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return FlatFileMarkedRecordPairSource.class;
	}

	/**
	 * Add a FlatFile marked record pair source to the configuration.
	 */
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			FlatFileMarkedRecordPairSource src = (FlatFileMarkedRecordPairSource) s;
			String fileName = src.getFileName();
			Element e = new Element("MarkedRecordPairSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			e.addContent(new Element("fileNamePrefix").setText(String.valueOf(src.getRawFileNamePrefix())));
			e.addContent(new Element("fileNameSuffix").setText(String.valueOf(src.getFileNameSuffix())));
			e.addContent(new Element("multiFile").setText(String.valueOf(src.isMultiFile())));
			e.addContent(new Element("singleLine").setText(String.valueOf(src.isSingleLine())));
			e.addContent(new Element("fixedLength").setText(String.valueOf(src.isFixedLength())));
			e.addContent(new Element("separatorChar").setText(String.valueOf(src.getSeparator())));
			e.addContent(new Element("tagged").setText(String.valueOf(src.isTagged())));
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			XMLOutputter o = new XMLOutputter("    ", false);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException {
		String fileNamePrefix = e.getChildText("fileNamePrefix");
		String fileNameSuffix = e.getChildText("fileNameSuffix");
		boolean multiFile = Boolean.valueOf(e.getChildText("multiFile")).booleanValue();
		boolean singleLine = Boolean.valueOf(e.getChildText("singleLine")).booleanValue();
		boolean fixedLength = Boolean.valueOf(e.getChildText("fixedLength")).booleanValue();
		char sep = e.getChildText("separatorChar").charAt(0);
		boolean tagged = Boolean.valueOf(e.getChildText("tagged")).booleanValue();
		return new FlatFileMarkedRecordPairSource(
			fileName,
			fileNamePrefix,
			fileNameSuffix,
			multiFile,
			singleLine,
			fixedLength,
			sep,
			tagged,
			model);
	}
}
