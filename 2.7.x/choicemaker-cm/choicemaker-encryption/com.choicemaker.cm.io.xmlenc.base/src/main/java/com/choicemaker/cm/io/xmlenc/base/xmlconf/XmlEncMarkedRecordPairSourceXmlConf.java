/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.base.xmlconf;

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
import com.choicemaker.cm.io.xmlenc.base.XmlEncMarkedRecordPairSource;
import com.choicemaker.utilcopy01.Precondition;

/**
 * Handling of encrypted XML Marked Record Pair sources.
 *
 * @author rphall
 */
public class XmlEncMarkedRecordPairSourceXmlConf implements
		MarkedRecordPairSourceXmlConfigurator {

	// private static final Logger logger =
	// Logger.getLogger(XmlEncMarkedRecordPairSourceXmlConf.class.getName());

	public static final String EXTENSION_POINT_ID = "com.choicemaker.cm.io.xml.base.xmlencMrpsReader";

	private final XmlEncryptionManager crdsMgr;

	public XmlEncMarkedRecordPairSourceXmlConf(XmlEncryptionManager cm) {
		Precondition.assertNonNullArgument("null credentials manager", cm);
		this.crdsMgr = cm;
	}

	public Object getHandler() {
		return this;
	}

	public Class<?> getHandledType() {
		return XmlEncMarkedRecordPairSource.class;
	}

	/**
	 * Add a XML marked record pair source to the configuration.
	 */
	public void add(MarkedRecordPairSource s) throws XmlConfException {
		try {
			XmlEncMarkedRecordPairSource src = (XmlEncMarkedRecordPairSource) s;
			String fileName = src.getFileName();
			Element e = new Element("MarkedRecordPairSource");
			e.setAttribute("class", EXTENSION_POINT_ID);
			e.setAttribute("schemeId", src.getPolicyId());
			e.setAttribute("credentialName", src.getCredentialName());
			// e.addContent(new
			// Element("fileName").setText(src.getXmlFileName()));
			e.addContent(new Element("fileName").setText(src
					.getRawXmlFileName()));
			FileOutputStream fs = new FileOutputStream(
					new File(fileName).getAbsoluteFile());
			XMLOutputter o = new XMLOutputter("    ", true);
			o.setTextNormalize(true);
			o.output(new Document(e), fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error: " + ex.toString());
		}
	}

	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName,
			Element e, ImmutableProbabilityModel model) throws XmlConfException {
		String xmlFileName = e.getChildText("fileName");
		String schemeId = e.getChildText("schemeId");
		EncryptionScheme ep = this.crdsMgr.getEncryptionScheme(schemeId);
		String credentialName = e.getChildText("credentialName");
		EncryptionCredential ec = this.crdsMgr
				.getEncryptionCredential(credentialName);
		XmlEncMarkedRecordPairSource retVal = new XmlEncMarkedRecordPairSource(
				fileName, xmlFileName, model, ep, ec, this.crdsMgr);
		return retVal;
	}
}
