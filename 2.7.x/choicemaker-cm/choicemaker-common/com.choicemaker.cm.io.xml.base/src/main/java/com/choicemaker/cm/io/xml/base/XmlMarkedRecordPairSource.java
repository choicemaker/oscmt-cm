/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.base.MutableMarkedRecordPair;
import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.cm.core.util.NameUtils;
import com.choicemaker.cm.core.xmlconf.XmlParserFactory;
import com.choicemaker.util.FileUtilities;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class XmlMarkedRecordPairSource extends XMLFilterImpl implements
		RecordHandler, Runnable, MarkedRecordPairSource {

	private static Logger logger = Logger
			.getLogger(XmlMarkedRecordPairSource.class.getName());

	public static final int BUFFER_SIZE = 1000;

	private String name;

	// If the inputStream is null, the xmlFileName must not be null
	private InputStream inputStream;
	private String xmlFileName;

	private String rawXmlFileName;
	protected String fileName;
	private boolean report;
	private Date curDate;
	private Record curQ;
	private Decision curDecision;
	private int loc;
	private static final int QR = 0;
	private static final int MR = 1;
	private static final int MA = 2;
	private static final int OUTSIDE = 3;
	private MutableMarkedRecordPair[] pairs = new MutableMarkedRecordPair[BUFFER_SIZE];
	private MutableMarkedRecordPair cur;
	private int out;
	private int size;
	private int depth;
	private boolean mayHaveMore;
	private boolean readMore;
	private Thread thread;
	protected ImmutableProbabilityModel model;
	private volatile Throwable thrown;

	public XmlMarkedRecordPairSource() {
	}

	public XmlMarkedRecordPairSource(String fileName, String rawXmlFileName,
			ImmutableProbabilityModel model) {
		this(null, fileName, rawXmlFileName, model);
	}

	public XmlMarkedRecordPairSource(InputStream is, String fileName,
			String rawXmlFileName, ImmutableProbabilityModel model) {
		this.setInputStream(is);
		setFileName(fileName);
		setRawXmlFileName(rawXmlFileName);
		setModel(model);
	}

	public boolean isReport() {
		return report;
	}

	public void setReport(boolean v) {
		this.report = v;
	}

	public void open() {
		setThrown(null);
		DefaultHandler handler = ((XmlAccessor) model.getAccessor()).getXmlReader();
		setContentHandler(handler);
		((XmlReader) handler).open(this);
		out = 0;
		setSize(0);
		depth = 0;
		setMayHaveMore(true);
		setReadMore(true);
		thread = new Thread(this);
		thread.start();
	}

	public synchronized boolean hasNext() throws IOException {
		try {
			while (getSize() == 0 && isMayHaveMore()) {
				wait();
			}
			if (getThrown() != null) {
				throw new IOException(getThrown());
			}
			return getSize() != 0;
		} catch (InterruptedException ex) {
			setReadMore(false);
			return false;
		}
	}

	public synchronized MutableMarkedRecordPair getNextMarkedRecordPair() throws IOException {
		if (getThrown() != null) {
			throw new IOException(getThrown());
		}
		try {
			while (getSize() == 0 && isMayHaveMore()) {
				wait();
			}
		} catch (InterruptedException ex) {
			throw new IOException(ex.toString());
		}
		MutableMarkedRecordPair r = pairs[out];
		setSize(getSize() - 1);
		out = (out + 1) % BUFFER_SIZE;
		this.notifyAll();
		return r;
	}

	public ImmutableRecordPair getNext() throws IOException {
		return getNextMarkedRecordPair();
	}

	public void run() {
		InputStream is = this.getInputStream();
		try {
			XMLReader reader = XmlParserFactory.createXMLReader();
			reader.setContentHandler(this);
			if (is == null) {
				is = new FileInputStream(new File(getXmlFileName()).getAbsoluteFile());
			}
			reader.parse(new InputSource(new BufferedInputStream(is)));
		} catch (SAXException ex) {
			if (!XmlRecordSource.FORCED_CLOSE.equals(ex.toString())) {
				if (!(report
					&& (ex.toString().equals(
						"org.xml.sax.SAXParseException: The element type \"Report\" must be terminated by the matching end-tag \"</Report>\"."))
						|| ex.toString().startsWith("org.xml.sax.SAXParseException: End of entity not allowed; an end tag is missing.") ||
						ex.toString().startsWith("org.xml.sax.SAXParseException: XML document structures must start and end within the same entity."))) {
					logger.severe(ex.toString());
					setThrown(ex);
				}
			}
		} catch (Exception ex) {
			logger.severe("Error reading file referenced by " + fileName + ": " + ex);
			setThrown(ex);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
			}
		}
		synchronized (this) {
			setMayHaveMore(false);
			this.notifyAll();
		}
	}

	/**
	 * Filter a start element event.
	 *
	 * @param uri The element's Namespace URI, or the empty string.
	 * @param localName The element's local name, or the empty string.
	 * @param qName The element's qualified (prefixed) name, or the empty
	 *        string.
	 * @param atts The element's attributes.
	 * @exception org.xml.sax.SAXException The client may throw
	 *            an exception during processing.
	 * @see org.xml.sax.ContentHandler#startElement
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		qName = qName.intern();
		++depth;
		if (depth == 1) {
			if (qName == "ChoiceMakerMarkedRecordPairs") {
				// ignore
			} else if (qName == "Report") {
				report = true;
				loc = OUTSIDE;
			} else {
				throw new SAXException("Illegal ChoiceMaker XML Source format.");
			}
		} else if (report) {
			if (depth == 2) {
				if (qName == "qu") {
					String st = atts.getValue("st");
					if(st != null && st.charAt(4) == '-') {
						curDate = DateHelper.parseSqlTimestamp(st);
					} else {
						curDate = new Date(Long.parseLong(st));
					}
				} else {
					throw new SAXException("Illegal ChoiceMaker XML Source format.");
				}
			} else if (depth == 3) {
				if (qName == "qr") {
					loc = QR;
				} else if (qName == "ma") {
					loc = MA;
					curDecision = Decision.valueOf(atts.getValue("de"));
				} else {
					loc = OUTSIDE;
				}
			} else if (depth == 4 && loc == MA) {
				if (qName == "mr") {
					loc = MR;
				}
			} else if (loc == QR || loc == MR) {
				getContentHandler().startElement(uri, localName, qName, atts);
			}
		} else { // !report
			if (depth == 2) {
				if (qName == "MarkedRecordPair") {
					cur = new MutableMarkedRecordPair();
					cur.setMarkedDecision(Decision.valueOf(atts.getValue("decision")));
					cur.setDateMarked(DateHelper.parse(atts.getValue("date")));
					cur.setUser(atts.getValue("user"));
					cur.setSource(atts.getValue("src"));
					cur.setComment(atts.getValue("comment"));
				} else {
					throw new SAXException("Illegal ChoiceMaker XML Source format.");
				}
			} else {
				getContentHandler().startElement(uri, localName, qName, atts);
			}
		}
	}

	/**
	 * Filter an end element event.
	 *
	 * @param uri The element's Namespace URI, or the empty string.
	 * @param localName The element's local name, or the empty string.
	 * @param qName The element's qualified (prefixed) name, or the empty
	 *        string.
	 * @exception org.xml.sax.SAXException The client may throw
	 *            an exception during processing.
	 * @see org.xml.sax.ContentHandler#endElement
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		qName = qName.intern();
		--depth;
		if (report) {
			if (depth == 0 || depth == 1) {
				// ignore
			} else if (depth == 2) {
				loc = OUTSIDE;
			} else if (depth == 3 && loc == MR) {
				loc = MA;
			} else if (loc == QR || loc == MR) {
				getContentHandler().endElement(uri, localName, qName);
			}
		} else {
			if (depth > 1) {
				getContentHandler().endElement(uri, localName, qName);
			}
		}
	}

	private void putPair(MutableMarkedRecordPair pair) throws SAXException {
		synchronized (this) {
			try {
				while (getSize() == BUFFER_SIZE && isReadMore()) {
					wait();
				}
				if (isReadMore()) {
					pairs[(out + getSize()) % BUFFER_SIZE] = pair;
					setSize(getSize() + 1);
					notifyAll();
				} else {
					notifyAll();
					throw new SAXException(XmlRecordSource.FORCED_CLOSE);
				}
			} catch (InterruptedException ex) {
			}
		}
	}

	public void handleRecord(Record r) throws SAXException {
		if (report) {
			if (loc == QR) {
				curQ = r;
			} else {
				putPair(new MutableMarkedRecordPair(curQ, r, curDecision, curDate, "", "Report", ""));
			}
		} else { // !report
			if (cur.getQueryRecord() == null) {
				cur.setQueryRecord(r);
			} else {
				cur.setMatchRecord(r);
				putPair(cur);
			}
		}
	}

	public synchronized void close() {
		setReadMore(false);
		setMayHaveMore(false);
		this.notifyAll(); // make sure that thread ends
	}

	protected int getSize() {
		return size;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	protected boolean isReadMore() {
		return readMore;
	}

	protected void setReadMore(boolean readMore) {
		this.readMore = readMore;
	}

	protected boolean isMayHaveMore() {
		return mayHaveMore;
	}

	protected void setMayHaveMore(boolean mayHaveMore) {
		this.mayHaveMore = mayHaveMore;
	}

	protected Throwable getThrown() {
		return thrown;
	}

	protected void setThrown(Throwable thrown) {
		this.thrown = thrown;
	}

	protected InputStream getInputStream() {
		return inputStream;
	}

	protected void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Get the value of name.
	 * @return value of name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name.
	 * @param v  Value to assign to name.
	 */
	public void setName(String v) {
		this.name = v;
	}

	/**
	 * Get the value of xmlFileName.
	 * @return value of xmlFileName.
	 */
	public String getXmlFileName() {
		return xmlFileName;
	}
	
	/**
	 * This method is protected since the difference between "rawXmlFileName"
	 * and "xmlFileName" is confusing and ambiguous.
	 * 
	 * @param s
	 */
	protected void setXmlFileName(String s) {
		this.xmlFileName = s;
	}

	/**
	 * Set the value of xmlFileName.
	 * @param fn  Value to assign to xmlFileName.
	 */
	public void setRawXmlFileName(String fn) {
		this.rawXmlFileName = fn;
		String s = FileUtilities.getAbsoluteFile(new File(fileName).getParentFile(), fn).toString();
		setXmlFileName(s);
	}

	public String getRawXmlFileName() {
		return rawXmlFileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setName(NameUtils.getNameFromFilePath(fileName));
	}

	public String getFileName() {
		return fileName;
	}

	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}

	public ImmutableProbabilityModel getModel() {
		return model;
	}

	public String toString() {
		return name;
	}

	public boolean hasSink() {
		return true;
	}

	public Sink getSink() {
		return new XmlMarkedRecordPairSink(fileName, getXmlFileName(), model);
	}
}
