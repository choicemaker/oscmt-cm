/*******************************************************************************
 * Copyright (c) 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.base;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.io.xml.base.XmlDiagnosticException;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSink;
import com.choicemaker.cm.io.xmlenc.mgmt.XmlEncryptionManager;
import com.choicemaker.util.MessageUtil;
import com.choicemaker.utilcopy01.Precondition;
//import com.choicemaker.utilcopy01.XMLUtils;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.DocumentEncryptor;
import com.choicemaker.xmlencryption.EncryptionScheme;

/**
 * XML sink that encrypts its output
 *
 * @author rphall
 */
public class XmlEncMarkedRecordPairSink extends XmlMarkedRecordPairSink {

	private static final Logger logger = Logger
			.getLogger(XmlEncMarkedRecordPairSink.class.getName());

	private final EncryptionScheme scheme;
	private final CredentialSet credential;
	private final XmlEncryptionManager xmlEncMgr;
	private StringWriter docWriter;

	public XmlEncMarkedRecordPairSink(String name, String rawXmlFileName,
			ImmutableProbabilityModel model, EncryptionScheme ep,
			CredentialSet ec, XmlEncryptionManager xcm) {
		super(name, rawXmlFileName, model);
		Precondition.assertNonNullArgument("null scheme", ep);
		Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertNonNullArgument("null encryption manager", xcm);
		this.scheme = ep;
		this.credential = ec;
		this.xmlEncMgr = xcm;
		this.docWriter = new StringWriter();
		super.setWriter(docWriter);
	}

	public String getPolicyId() {
		return scheme.getSchemeId();
	}

	public String getCredentialName() {
		return credential.getCredentialName();
	}

	protected Writer createWriter() {
		docWriter = new StringWriter();
		return docWriter;
	}

	public void close() throws IOException, XmlDiagnosticException {
		super.finishRootEntity();
		super.getWriter().flush();
		String docString = docWriter.toString();
		FileOutputStream fos = createFileOutputStream();
		DocumentEncryptor encryptor =
			xmlEncMgr.getDocumentEncryptor(scheme, credential);
		encrypt(encryptor, docString, fos);
		finishRootEntity();
		fos.close();
	}

	protected static void encrypt(DocumentEncryptor encryptor,
			String docString, FileOutputStream fos) throws IOException,
			XmlDiagnosticException {
		try {
			byte[] b = docString.getBytes();
			ByteArrayInputStream sourceDocument = new ByteArrayInputStream(b);
			DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
			Document doc = builder.parse(sourceDocument);

			encryptor.encrypt(doc);

			// Output the result
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			PrintStream ps = new PrintStream(fos);
			StreamResult result = new StreamResult(ps);
			transformer.transform(source, result);
		} catch (ParserConfigurationException | SAXException
				| TransformerException | XMLSecurityException e) {
			String elidedDocString = MessageUtil.elideString(docString, 50);
			final String msg = "Failed to encrypt document: " + elidedDocString;
			logger.severe(msg + ": " + e.toString());
			if (e instanceof ParserConfigurationException) {
				throw new XmlDiagnosticException(msg, (ParserConfigurationException)e);
			} else if (e instanceof SAXException) {
				throw new XmlDiagnosticException(msg, (SAXException) e);
			} else if (e instanceof TransformerException) {
				throw new XmlDiagnosticException(msg, (TransformerException) e);
			} else if (e instanceof XMLSecurityException) {
				throw new XmlEncDiagnosticException(msg, (XMLSecurityException) e);
			} else {
				String msg2 = "Unexpected exception type: " + e.getClass().getSimpleName() +": " + msg;
				throw new XmlDiagnosticException(msg, e);
			}
		}
	}

}
