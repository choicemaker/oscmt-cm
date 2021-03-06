/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.composite.base.xmlconf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConf;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConfigurator;
import com.choicemaker.cm.io.composite.base.CompositeRecordSource;
import com.choicemaker.util.FileUtilities;

/**
 * Handling of composite Marked Record Pair sources.
 *
 * @author    Adam Winkel
 */
public class CompositeRecordSourceXmlConf implements RecordSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.composite.base.compositeRsReader";

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return CompositeRecordSource.class;
	}

	/**
	 * Add a Composite record source to the configuration.
	 * @param   s  The composite record source.
	 */
	@Override
	public void add(RecordSource s) throws XmlConfException {
		try {
			CompositeRecordSource src = (CompositeRecordSource) s;
			String fileName = src.getFileName();
			File rel = new File(fileName).getAbsoluteFile().getParentFile();
			Element e = new Element("RecordSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			int numSources = src.getNumSources();
			for (int i = 0; i < numSources; ++i) {
				Element cons = new Element("constituent");
				RecordSource mrps = src.getSource(i);
				if (src.saveAsRelative(i)) {
					cons.setAttribute("name", FileUtilities.getRelativeFile(rel, mrps.getFileName()).toString());
				} else {
					cons.setAttribute("name", mrps.getFileName());
				}
				e.addContent(cons);
			}
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
	public RecordSource getRecordSource(String fileName, Element e, ImmutableProbabilityModel model) throws XmlConfException {
		CompositeRecordSource comp = new CompositeRecordSource();
		comp.setFileName(fileName);
		List cons = e.getChildren("constituent");
		Iterator i = cons.iterator();
		File rel = new File(fileName).getAbsoluteFile().getParentFile();
		while (i.hasNext()) {
			Element conEl = (Element) i.next();
			String conFileName = conEl.getAttributeValue("name");
			String absConFileName = FileUtilities.getAbsoluteFile(rel, conFileName).toString();
			boolean saveAsRel = !FileUtilities.isFileAbsolute(conFileName);
			comp.add(RecordSourceXmlConf.getRecordSource(absConFileName), saveAsRel);
		}
		return comp;
	}

	@Override
	public String toString() {
		return "Composite RS";
	}
}
