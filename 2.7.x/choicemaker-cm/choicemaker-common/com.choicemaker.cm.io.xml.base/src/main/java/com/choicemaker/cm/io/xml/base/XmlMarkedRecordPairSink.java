/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSink;
import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.cm.core.util.XmlOutput;
import com.choicemaker.util.FileUtilities;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class XmlMarkedRecordPairSink implements MarkedRecordPairSink {
	private String name;
	private String xmlFileName;
	private String rawXmlFileName;
	private ImmutableProbabilityModel model;
	private XmlRecordOutputter recordOutputter;
	private FileOutputStream outputStream;
	private Writer writer;

	private static String encoding;
	static {
		if (Locale.getDefault().getLanguage().equals("ja")) {
			encoding = "Shift_JIS";
		} else {
			encoding = "UTF-8";
		}
	}
	
	public static String getEncoding() {
		return encoding;
	}

	public XmlMarkedRecordPairSink(String name, String rawXmlFileName, ImmutableProbabilityModel model) {
		this.name = name;
		setRawXmlFileName(rawXmlFileName);
		setModel(model);
	}
	
	public void setRawXmlFileName(String fn) {
		this.rawXmlFileName = fn;
		this.xmlFileName = FileUtilities.getAbsoluteFile(new File(name).getParentFile(), fn).toString();
	}

	public String getRawXmlFileName() {
		return rawXmlFileName;
	}

	protected void startRootEntity() throws IOException {
		getWriter().write("<ChoiceMakerMarkedRecordPairs>" + Constants.LINE_SEPARATOR);
	}

	protected FileOutputStream createFileOutputStream()
			throws FileNotFoundException {
		FileOutputStream retVal = new FileOutputStream(
				new File(xmlFileName).getAbsoluteFile());
		return retVal;
	}

	protected Writer createWriter() throws FileNotFoundException,
			UnsupportedEncodingException {
		FileOutputStream os = createFileOutputStream();
		Writer retVal = new OutputStreamWriter(os, getEncoding());
		return retVal;
	}

	@Override
	public void open() throws IOException {
		recordOutputter = ((XmlAccessor) model.getAccessor()).getXmlRecordOutputter();
		FileOutputStream fos = createFileOutputStream();
		setOutputStream(fos);
		Writer w = createWriter();
		setWriter(w);
		getWriter().write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + Constants.LINE_SEPARATOR);
		startRootEntity();
		getWriter().flush();
	}

	protected void finishRootEntity() throws IOException {
		getWriter().write("</ChoiceMakerMarkedRecordPairs>");
	}

	@Override
	public void close() throws IOException, XmlDiagnosticException {
		finishRootEntity();
		getWriter().flush();
		getOutputStream().close();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}

	private FileOutputStream getOutputStream() {
		return outputStream;
	}

	private void setOutputStream(FileOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	protected void setWriter(Writer writer) {
		this.writer = writer;
	}

	protected Writer getWriter() {
		return writer;
	}

	@Override
	public void put(ImmutableRecordPair r) throws IOException {
		putMarkedRecordPair((ImmutableMarkedRecordPair) r);
	}

	protected void startRecordPairEntity() throws IOException {
		getWriter().write("<MarkedRecordPair");
	}
	
	protected void finishRecordPairEntity() throws IOException {
		getWriter().write("</MarkedRecordPair>" + Constants.LINE_SEPARATOR);
	}

	@Override
	public void putMarkedRecordPair(ImmutableMarkedRecordPair r) throws IOException {
		startRecordPairEntity();
		XmlOutput.writeAttribute(getWriter(), "decision", r.getMarkedDecision().toString());
		XmlOutput.writeAttribute(getWriter(), "date", DateHelper.format(r.getDateMarked()));
		XmlOutput.writeAttribute(getWriter(), "user", r.getUser());
		XmlOutput.writeAttribute(getWriter(), "src", r.getSource());
		XmlOutput.writeAttribute(getWriter(), "comment", r.getComment());
		putAdditionalAttributes(r);
		getWriter().write(">" + Constants.LINE_SEPARATOR);
		recordOutputter.put(getWriter(), r.getQueryRecord());
		recordOutputter.put(getWriter(), r.getMatchRecord());
		finishRecordPairEntity();
	}

	/**
	 * Callback for subclasses
	 * @param mrp Non-null marked record pair
	 */
	protected void putAdditionalAttributes(ImmutableMarkedRecordPair mrp)  throws IOException {
	}

	@Override
	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	@Override
	public void flush() throws IOException {
		getWriter().flush();
	}

}
