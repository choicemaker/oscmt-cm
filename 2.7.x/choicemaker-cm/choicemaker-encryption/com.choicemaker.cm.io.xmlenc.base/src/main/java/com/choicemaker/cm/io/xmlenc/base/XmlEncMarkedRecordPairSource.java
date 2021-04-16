/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSource;
import com.choicemaker.cm.io.xmlenc.mgmt.XmlEncryptionManager;
import com.choicemaker.util.Precondition;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.EncryptionScheme;

/**
 * @author rphall
 */
public class XmlEncMarkedRecordPairSource extends XmlMarkedRecordPairSource {

	private static final Logger logger = Logger
			.getLogger(XmlEncMarkedRecordPairSource.class.getName());

	private EncryptionScheme scheme;
	private CredentialSet credential;
	private XmlEncryptionManager xmlEncMgr;

	public XmlEncMarkedRecordPairSource() {
		this(null, null, null);
	}

	public XmlEncMarkedRecordPairSource(XmlEncryptionManager xcm) {
		this(null, null, xcm);
	}

	public XmlEncMarkedRecordPairSource(EncryptionScheme ep, CredentialSet ec,
			XmlEncryptionManager xcm) {
		super();
		// Precondition.assertNonNullArgument("null scheme", ep);
		// Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertNonNullArgument("null encryption manager", xcm);
		this.scheme = ep;
		this.credential = ec;
		this.xmlEncMgr = xcm;
	}

	public XmlEncMarkedRecordPairSource(String fileName, String rawXmlFileName,
			ImmutableProbabilityModel model, EncryptionScheme ep,
			CredentialSet ec, XmlEncryptionManager xcm) {
		this(null, fileName, rawXmlFileName, model, ep, ec, xcm);
	}

	public XmlEncMarkedRecordPairSource(InputStream is, String fileName,
			String rawXmlFileName, ImmutableProbabilityModel model,
			EncryptionScheme ep, CredentialSet ec, XmlEncryptionManager xcm) {
		super(is, fileName, rawXmlFileName, model);
		Precondition.assertNonNullArgument("null scheme", ep);
		Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertNonNullArgument("null encryption manager", xcm);
		this.scheme = ep;
		this.credential = ec;
		this.xmlEncMgr = xcm;
	}

	public void setEncryptionScheme(String id) {
		Precondition.assertNonNullArgument("null encryption scheme id", id);
		EncryptionScheme es = this.xmlEncMgr.getEncryptionScheme(id);
		if (es == null) {
			String msg = "No encryption scheme for id '" + id + "'";
			logger.warning(msg);
		}
		setEncryptionScheme(es);
	}

	public void setEncryptionScheme(EncryptionScheme es) {
		if (es == null) {
			String msg = "Setting encryption scheme to null";
			logger.warning(msg);
		}
		this.scheme = es;
	}

	public EncryptionScheme getEncryptionScheme() {
		return this.scheme;
	}

	public String getEncryptionSchemeId() {
		String retVal = scheme == null ? null : scheme.getSchemeId();
		return retVal;
	}

	public void setCredentialSet(String name) {
		Precondition.assertNonNullArgument("null encryption credential name",
				name);
		CredentialSet cs = this.xmlEncMgr.getCredentialSet(name);
		if (cs == null) {
			String msg = "No encryption credential for id '" + name + "'";
			logger.warning(msg);
		}
		setCredentialSet(cs);
	}

	public void setCredentialSet(CredentialSet cs) {
		if (cs == null) {
			String msg = "Setting credential set to null";
			logger.warning(msg);
		}
		this.credential = cs;
	}

	public CredentialSet getCredentialSet() {
		return this.credential;
	}

	public String getCredentialName() {
		String retVal = credential == null ? null : credential
				.getCredentialName();
		return retVal;
	}

	public XmlEncryptionManager getXmlEncryptionManager() {
		return xmlEncMgr;
	}

	public void setXmlEncryptionManager(XmlEncryptionManager xmlEncMgr) {
		this.xmlEncMgr = xmlEncMgr;
	}

	@Override
	public void open() {
		try {
			final XmlEncryptionManager xcm = getXmlEncryptionManager();
			final DocumentDecryptor decryptor = xcm.getDocumentDecryptor(
					scheme, credential);
			InputStream sourceDocument = null;
			if (getInputStream() != null) {
				sourceDocument = getInputStream();
			} else {
				sourceDocument = new FileInputStream(
						new File(getXmlFileName()).getAbsoluteFile());
			}
			InputStream decrypted = decrypt(decryptor, sourceDocument);
			super.setInputStream(decrypted);
			super.open();

		} catch (Throwable e) {
			String msg = "Exception or error thrown during open(); failure deferred: "
					+ e.toString();
			logger.warning(msg);
			setThrown(e);
			assert getSize() == 0;
			assert isMayHaveMore() == false;
			assert isReadMore() == false;
		}
	}

	protected static InputStream decrypt(DocumentDecryptor decryptor,
			InputStream is) throws Exception {

		DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
		Document doc = builder.parse(is);
		is.close();

		// Decrypt the input
		decryptor.decrypt(doc);

		// Output the result to a byte array
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		ByteArrayOutputStream boas = new ByteArrayOutputStream(BUFFER_SIZE);
		PrintStream ps = new PrintStream(boas);
		StreamResult result = new StreamResult(ps);
		transformer.transform(source, result);

		// Create the input stream that will be returned
		byte[] b = boas.toByteArray();
		ByteArrayInputStream retVal = new ByteArrayInputStream(b);

		return retVal;
	}

	@Override
	public Sink getSink() {
		XmlEncMarkedRecordPairSink retVal = new XmlEncMarkedRecordPairSink(
				fileName, getXmlFileName(), model, scheme, credential,
				xmlEncMgr);
		return retVal;
	}

}
