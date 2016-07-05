/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.xmlconf;

import java.util.Properties;
import java.util.logging.Logger;

import org.jasypt.encryption.StringEncryptor;
import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.amazonaws.util.StringInputStream;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ConfigurationUtils;
import com.choicemaker.cm.core.util.StringUtils;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.io.xmlenc.mgmt.InMemoryXmlEncManager;
import com.choicemaker.cm.io.xmlenc.mgmt.XmlEncryptionManager;
import com.choicemaker.xmlencryption.CredentialSet;

/**
 * XML configuration for an in-memory XML EncryptionManager.
 */
public class XmlEncInitializer implements XmlModuleInitializer {

	private static Logger logger = Logger.getLogger(XmlEncInitializer.class
			.getName());

	/**
	 * Returns the singleton instance of the in-memory XML encryption manager.
	 * FIXME: this should be set by an application, not by a library.
	 */
	protected static XmlEncryptionManager getDefaultEncryptionManager() {
		XmlEncryptionManager retVal = InMemoryXmlEncManager.getInstance();
		return retVal;
	}

	public final static XmlEncInitializer instance = new XmlEncInitializer();

	private XmlEncInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor)
			throws XmlConfException {
		if (e != null && !e.getName().equals("xmlenc")) {
			Document d = e.getDocument();
			Element r = d == null ? null : d.getRootElement();
			Element p = r == null ? null : r.getChild("plugin");
			e = p == null ? null : p.getChild("xmlenc");
		}
		if (e != null) {
			for (Object o : e.getChildren("credentialSet")) {
				try {
					// Cast the child object to an Element
					assert o != null;
					Element cs = (Element) o;

					// Get the name attribute of the element, or skip
					Attribute a = cs.getAttribute("name");
					String csName = a == null ? null : a.getValue();
					csName = csName == null ? null : csName.trim();
					if (csName == null || csName.isEmpty()) {
						String msg = "Skipping unnamed credential set";
						logger.warning(msg);
						continue;
					}

					// Create an invalid, empty CredentialSet
					assert StringUtils.nonEmptyString(csName);
					CredentialSet credentials = new CredentialSet(csName);

					// Create an XML document with the properties of this set
					Element ep = cs.getChild("properties");
					DocType dt = new DocType("properties",
							"http://java.sun.com/dtd/properties.dtd");
					Document d = new Document((Element) ep.clone(), dt);
					XMLOutputter outp = new XMLOutputter();
					String s = outp.outputString(d);

					// Load a set of properties from the XML
					Properties p = new Properties();
					StringInputStream sis = new StringInputStream(s);
					p.loadFromXML(sis);

					// Add the properties to the credential set
					if (encryptor == null) {
						credentials.putAll(p);
					} else {
						for (Object o2 : p.keySet()) {
							String pn = (String) o2;
							String epv = p.getProperty(pn);
							if (epv == null) {
								continue;
							}
							String pv = ConfigurationUtils.decryptText(
									epv.trim(), encryptor);
							credentials.put(pn, pv);
						}
					}

					// Add the credentials to the default credentials manager
					getDefaultEncryptionManager().putCredentialSet(credentials);

				} catch (Exception ex) {
					logger.warning("Error creating credential set: " + ex);
				}
			}
		}
	}

}
