/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSource;
import com.choicemaker.cm.io.xmlenc.base.XmlEncMarkedRecordPairSource;
import com.choicemaker.cm.io.xmlenc.xmlconf.InMemoryXmlEncManager;
import com.choicemaker.cm.io.xmlenc.xmlconf.XmlEncryptionManager;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;
import com.choicemaker.xmlencryption.AwsKmsEncryptionScheme;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

/**
 * Description
 *
 * @author rphall
 */
public class XmlEncMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {

	private static final Logger logger = Logger
			.getLogger(XmlEncMarkedRecordPairSourceGuiFactory.class.getName());

	public static EncryptionScheme getDefaultScheme() {
		EncryptionScheme retVal = new AwsKmsEncryptionScheme();
		return retVal;
	}

	public static CredentialSet getDefaultCredentialSet() {
		final String NAME = "default";
		final String CREDENTIALS = "/Users/rphall/Documents/git/oscmt-cm/2.7.x/choicemaker-cmit/cmit-encryption/src/test/resources/santuario-kms_local.properties";
		final String PN_CREDENTIALS = "cm.xmlenc.credentials";
		String credsfn = System.getProperty(PN_CREDENTIALS, CREDENTIALS);
		File f = new File(credsfn);
		Properties credProps = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			credProps = new Properties();
			credProps.load(fis);
		} catch (IOException x) {
			String msg = "Unable to open default credentials: " + x.toString();
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.warning("Unable to close '" + credsfn + "': " + e.toString());
				}
				fis = null;
			}
		}
		assert credProps != null;
		CredentialSet cs = AwsKmsEncryptionScheme.createCredentialSet(NAME,
				credProps);
		return cs;
	}

	private EncryptionScheme scheme;
	private CredentialSet credentials;
	private XmlEncryptionManager xmlEncMgr;

	public XmlEncMarkedRecordPairSourceGuiFactory() {
		this(getDefaultScheme(), getDefaultCredentialSet(),
				InMemoryXmlEncManager.getInstance());
		xmlEncMgr.putEncryptionCredential(credentials);
		xmlEncMgr.putEncryptionScheme(scheme);
	}

	public XmlEncMarkedRecordPairSourceGuiFactory(EncryptionScheme es,
			CredentialSet cs, XmlEncryptionManager xem) {
		this.scheme = es;
		this.credentials = cs;
		this.xmlEncMgr = xem;
	}

	public String getName() {
		return "XML ENC";
	}

	public SourceGui createGui(ModelMaker parent, Source s) {
		return new XmlEncMarkedRecordPairSourceGui(parent,
				(MarkedRecordPairSource) s, false);
	}

	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new XmlEncMarkedRecordPairSource(scheme, credentials, xmlEncMgr));
	}

	public SourceGui createSaveGui(ModelMaker parent) {
		return new XmlEncMarkedRecordPairSourceGui(parent,
				new XmlEncMarkedRecordPairSource(scheme, credentials, xmlEncMgr), true);
	}

	public Object getHandler() {
		return this;
	}

	public Class<?> getHandledType() {
		return XmlEncMarkedRecordPairSource.class;
	}

	public String toString() {
		return "XML EMRPS";
	}

	public boolean hasSink() {
		return true;
	}
}
