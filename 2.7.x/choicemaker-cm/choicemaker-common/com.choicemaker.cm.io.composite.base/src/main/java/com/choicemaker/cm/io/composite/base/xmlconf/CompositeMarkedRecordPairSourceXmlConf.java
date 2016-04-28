/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConf;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConfigurator;
import com.choicemaker.cm.io.composite.base.CompositeMarkedRecordPairSource;
import com.choicemaker.util.FileUtilities;

/**
 * Handling of composite Marked Record Pair sources.
 *
 * @author    Martin Buechi
 */
public class CompositeMarkedRecordPairSourceXmlConf implements MarkedRecordPairSourceXmlConfigurator {
	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.composite.base.compositeMrpsReader";

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return CompositeMarkedRecordPairSource.class;
	}

	/**
	 * Add a Composite marked record pair source to the configuration.
	 * @param   s  The composite marked record pair source.
	 */
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			CompositeMarkedRecordPairSource src = (CompositeMarkedRecordPairSource) s;
			String fileName = src.getFileName();
			File rel = new File(fileName).getAbsoluteFile().getParentFile();
			Element e = new Element("MarkedRecordPairSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			int numSources = src.getNumSources();
			for (int i = 0; i < numSources; ++i) {
				Element cons = new Element("constituent");
				MarkedRecordPairSource mrps = (MarkedRecordPairSource) src.getSource(i);
				if (src.saveAsRelative(i)) {
					cons.setAttribute("name", FileUtilities.getRelativeFile(rel, mrps.getFileName()).toString());
				} else {
					cons.setAttribute("name", mrps.getFileName());
				}
				e.addContent(cons);
			}
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
		CompositeMarkedRecordPairSource comp = new CompositeMarkedRecordPairSource();
		comp.setFileName(fileName);
		List cons = e.getChildren("constituent");
		Iterator i = cons.iterator();
		File rel = new File(fileName).getAbsoluteFile().getParentFile();
		while (i.hasNext()) {
			Element conEl = (Element) i.next();
			String conFileName = conEl.getAttributeValue("name");
			String absConFileName = FileUtilities.getAbsoluteFile(rel, conFileName).toString();
			boolean saveAsRel = !FileUtilities.isFileAbsolute(conFileName);
			comp.add(MarkedRecordPairSourceXmlConf.getMarkedRecordPairSource(absConFileName), saveAsRel);
		}
		return comp;
	}

	public String toString() {
		return "Composite MRPS";
	}
}
