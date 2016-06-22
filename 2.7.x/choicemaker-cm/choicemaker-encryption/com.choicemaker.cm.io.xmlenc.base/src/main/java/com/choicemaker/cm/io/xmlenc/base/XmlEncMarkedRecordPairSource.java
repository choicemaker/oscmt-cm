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
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSource;
import com.choicemaker.util.Precondition;
import com.choicemaker.xmlencryption.DocumentDecryptor;

/**
 * Description
 *
 * @author Martin Buechi
 */
public class XmlEncMarkedRecordPairSource extends XmlMarkedRecordPairSource {

	private static final Logger logger = Logger
			.getLogger(XmlEncMarkedRecordPairSource.class.getName());

	private final DocumentDecryptor decryptor;

	public XmlEncMarkedRecordPairSource(String fileName, String rawXmlFileName,
			ImmutableProbabilityModel model, DocumentDecryptor decryptor) {
		super(fileName, rawXmlFileName, model);
		Precondition.assertNonNullArgument("null decryptor", decryptor);
		this.decryptor = decryptor;
	}

	public XmlEncMarkedRecordPairSource(InputStream is, String fileName,
			String rawXmlFileName, ImmutableProbabilityModel model,
			DocumentDecryptor decryptor) {
		super(is, fileName, rawXmlFileName, model);
		Precondition.assertNonNullArgument("null decryptor", decryptor);
		this.decryptor = decryptor;
	}

	public void open() {
		try {
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

		} catch (Exception e) {
			String msg = "Exception thrown during open(); failure deferred: "
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
}
