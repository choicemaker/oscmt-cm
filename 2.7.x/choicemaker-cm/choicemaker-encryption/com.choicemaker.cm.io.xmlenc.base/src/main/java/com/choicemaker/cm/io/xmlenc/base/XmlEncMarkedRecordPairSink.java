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
import java.util.Collections;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSink;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.EncryptionCredential;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.EncryptionScheme;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.XmlEncryptionManager;
import com.choicemaker.utilcopy01.Precondition;
import com.choicemaker.xmlencryption.DocumentEncryptor;

/**
 * XML sink that encrypts its output
 *
 * @author rphall
 */
public class XmlEncMarkedRecordPairSink extends XmlMarkedRecordPairSink {

	private final EncryptionScheme policy;
	private final EncryptionCredential credential;
	private final XmlEncryptionManager xmlEncMgr;
	private StringWriter docWriter;

	public XmlEncMarkedRecordPairSink(String name, String rawXmlFileName,
			ImmutableProbabilityModel model, EncryptionScheme ep,
			EncryptionCredential ec, XmlEncryptionManager xcm) {
		super(name, rawXmlFileName, model);
		Precondition.assertNonNullArgument("null policy", ep);
		Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertNonNullArgument("null encryption manager", xcm);
		this.policy = ep;
		this.credential = ec;
		this.xmlEncMgr = xcm;
		this.docWriter = new StringWriter();
		super.setWriter(docWriter);
	}

	public String getPolicyId() {
		return policy.getSchemeId();
	}

	public String getCredentialName() {
		return credential.getCredentialName();
	}

	protected Writer createWriter() {
		docWriter = new StringWriter();
		return docWriter;
	}

	public void close() throws IOException {
		super.finishRootEntity();
		super.getWriter().flush();
		String docString = docWriter.toString();
		FileOutputStream fos = createFileOutputStream();
		String algorithmName = policy.getDefaultAlgorithmName();
		Map<String, String> EMPTY = Collections.emptyMap();
		DocumentEncryptor encryptor = xmlEncMgr.getDocumentEncryptor(policy,
				algorithmName, credential, EMPTY);
		encrypt(encryptor, docString, fos);
		finishRootEntity();
		fos.close();
	}

	protected static void encrypt(DocumentEncryptor encryptor,
			String docString, FileOutputStream fos) throws IOException {
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
		} catch (IOException x) {
			throw x;
		} catch (Exception x) {
			throw new IOException(x);
		}
	}

}
