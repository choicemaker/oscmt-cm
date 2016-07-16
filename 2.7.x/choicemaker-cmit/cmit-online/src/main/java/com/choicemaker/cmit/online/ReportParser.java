/*
 * @(#)$RCSfile$        $Revision$ $Date$
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 41 East 11th Street, New York, NY 10003
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */

package com.choicemaker.cmit.online;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.ActiveClues;
import com.choicemaker.cm.core.base.Match;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.cm.core.xmlconf.XmlParserFactory;
import com.choicemaker.cm.io.xml.base.RecordHandler;
import com.choicemaker.cm.io.xml.base.XmlAccessor;
import com.choicemaker.cm.io.xml.base.XmlReader;
import com.choicemaker.cm.io.xml.base.XmlRecordSource;

/**
 * Parses a Report file into individual queries, and then parses each query into
 * parameters, metrics, query record, and the resulting matches or errors.
 *
 * @author Rick Hall
 * @version $Revision$ $Date$
 */
public class ReportParser extends XMLFilterImpl implements RecordHandler,
		Runnable, Iterator<Query> {

	private static Logger logger = Logger.getLogger(ReportParser.class
			.getName());

	/** XML parser */
	private static final String READER = "org.apache.xerces.parsers.SAXParser";
	private static final String SAX_ERROR_MSG_1 =
		"org.xml.sax.SAXParseException: The element type \"Report\" must be terminated by the matching end-tag \"</Report>\".";
	private static final String SAX_ERROR_MSG_2 =
		"org.xml.sax.SAXParseException: End of entity not allowed; an end tag is missing.";
	private static final String SAX_ERROR_MSG_3 =
		"org.xml.sax.SAXParseException: XML document structures must start and end within the same entity.";

	/**
	 * A location flag indicating that parsing is at an entity that contains a
	 * query record
	 */
	private static final int QR = 0;

	/**
	 * A location flag indicating that parsing is at an entity that contains a
	 * match record
	 */
	private static final int MR = 1;

	/**
	 * A location flag indicating that parsing is at an entity that contains a
	 * match result (decision plus probability plus match record)
	 */
	private static final int MA = 2;

	/**
	 * A location flag indicating that parsing is at an entity that contains a
	 * reported exception
	 */
	private static final int EX = 3;

	/**
	 * A location flag indicating that parsing is at an entity that contains a
	 * error result (zero or more reported exceptions)
	 */
	private static final int ER = 4;

	/**
	 * A location flag indicating that parsing is at an entity that is not a(n
	 * immediate) record container
	 */
	private static final int OUTSIDE = 3;

	/** The number of Query instances cached in memory */
	private static final int BUF_SIZE = 1000;

	// Values used in case of parsing errors
	private static final Decision DEFAULT_DECISION = Decision.DIFFER;
	private static final float DEFAULT_PROBABILITY = 0f;

	private File xmlFile;
	private int location;
	private Decision currentDecision;
	private float currentProbability;
	private QueryError currentQueryError;
	private Query currentQuery;
	private Query[] queries = new Query[BUF_SIZE];
	private int out;
	private int size;
	private int depth;
	private boolean mayHaveMore;
	private boolean readMore;
	private Thread thread;
	private ImmutableProbabilityModel currentModel;
	private volatile Throwable thrown;

	public ReportParser(File rawXmlFileName) {
		setXmlFile(rawXmlFileName);
	}

	public void open() {
		thrown = null;
		out = 0;
		size = 0;
		depth = 0;
		mayHaveMore = true;
		readMore = true;
		thread = new Thread(this);
		thread.start();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public synchronized boolean hasNext() {
		try {
			while (size == 0 && mayHaveMore) {
				wait();
			}
			if (thrown != null) {
				logger.severe(thrown.getMessage() + ": " + thrown.toString());
				throw new RuntimeException(thrown.toString());
			}
			return size != 0;
		} catch (InterruptedException ex) {
			readMore = false;
			return false;
		}
	}

	public Query next() {
		return getNextQuery();
	}

	public synchronized Query getNextQuery() {
		if (thrown != null) {
			throw new NoSuchElementException(thrown.toString());
		}
		try {
			while (size == 0 && mayHaveMore) {
				wait();
			}
		} catch (InterruptedException ex) {
			throw new NoSuchElementException(ex.toString());
		}
		Query r = queries[out];
		--size;
		out = (out + 1) % BUF_SIZE;
		this.notifyAll();
		return r;
	}

	public void run() {
		FileInputStream fs = null;
		try {
			XMLReader reader = XmlParserFactory.createXMLReader(READER);
			reader.setContentHandler(this);
			fs = new FileInputStream(this.getXmlFile());
			reader.parse(new InputSource(new BufferedInputStream(fs)));
		} catch (SAXException ex) {
			if (!XmlRecordSource.FORCED_CLOSE.equals(ex.toString())) {
				if (!(ex.toString().equals(SAX_ERROR_MSG_1)
						|| ex.toString().startsWith(SAX_ERROR_MSG_2) || ex
						.toString().startsWith(SAX_ERROR_MSG_3))) {
					logger.severe(ex.toString());
					thrown = ex;
				}
			}
		} catch (Exception ex) {
			logger.severe("Error reading file referenced by '"
					+ this.getXmlFile().toString() + "': " + ex);
			thrown = ex;
		} finally {
			logger.fine("Here");
			try {
				if (fs != null) {
					fs.close();
				}
			} catch (Exception e) {
			}
		}
		synchronized (this) {
			mayHaveMore = false;
			this.notifyAll();
		}
	}

	/**
	 * Filter a start element event.
	 *
	 * @param uri
	 *            The element's Namespace URI, or the empty string.
	 * @param localName
	 *            The element's local name, or the empty string.
	 * @param qName
	 *            The element's qualified (prefixed) name, or the empty string.
	 * @param atts
	 *            The element's attributes.
	 * @exception org.xml.sax.SAXException
	 *                The client may throw an exception during processing.
	 * @see org.xml.sax.ContentHandler#startElement
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		qName = qName.intern();
		++depth;
		if (depth == 1) {
			if (qName == "Report") {
				location = OUTSIDE;
			} else {
				throw new SAXException("Illegal ChoiceMaker Report format.");
			}
		} else {
			if (depth == 2) {
				if (qName == "qu") {

					// Starting a new query
					QueryParams queryParams = new QueryParams();
					QueryMetrics queryMetrics = new QueryMetrics();
					parseQueryAttributes(uri, localName, qName, atts,
							queryParams, queryMetrics);
					currentQuery = new Query(queryParams, queryMetrics);

				} else {
					throw new SAXException("Illegal ChoiceMaker Report format.");
				}
			} else if (depth == 3) {
				if (qName == "qr") {
					location = QR;
				} else if (qName == "ma") {

					location = MA;

					// Decision
					currentDecision = DEFAULT_DECISION;
					String de = atts.getValue("de");
					if (de != null) {
						try {
							currentDecision = Decision.valueOf(de);
						} catch (Exception x) {
							logger.warning("Unable to parse decision '" + de
									+ "'; using default value instead");
						}
					}

					// Probability
					currentProbability = DEFAULT_PROBABILITY;
					String pr = atts.getValue("pr");
					if (pr != null) {
						try {
							currentProbability = Float.parseFloat(pr);
						} catch (Exception x) {
							logger.warning("Unable to parse probability '" + pr
									+ "'; using default value instead");
						}
					}
				} else if (qName == "error") {
					location = ER;

				} else {
					location = OUTSIDE;
				}
			} else if (depth == 4 && location == MA) {
				if (qName == "mr") {
					location = MR;
				}
			} else if (depth == 4 && location == ER) {
				if (qName == "ex") {
					location = EX;
					// Starting a new reported exception
					currentQueryError = null;
					{
						String cls = atts.getValue("cls");
						if (cls == null) {
							logger.warning("Unable to find value for class name of exception, using default value instead");
							cls = Exception.class.getName();
						}
						currentQueryError = new QueryError(cls);
					}
					{
						String msg = atts.getValue("msg");
						if (msg == null) {
							logger.warning("Unable to find value for class name of exception, using default value instead");
							msg = Exception.class.getName();
						}
						currentQueryError.setMessage(msg);
					}
				}
			} else if (location == QR || location == MR) {
				getContentHandler().startElement(uri, localName, qName, atts);
			}
			// NOTE: root cause of a QueryError is not yet parsed
		}
	}

	public void parseQueryAttributes(String uri, String localName,
			String qName, Attributes atts, QueryParams queryParams,
			QueryMetrics queryMetrics) throws SAXException {

		// Lower threshold
		{
			String lt = atts.getValue("lt");
			float lowerThreshold = QueryParams.DEFAULT_DIFFER_THRESHOLD;
			try {
				if (lt != null) {
					lowerThreshold = Float.parseFloat(lt);
				} else {
					logger.warning("Unable to find lower threshold for query; using default value instead");
				}
			} catch (Exception x) {
				logger.warning("Unable to parse '" + lt
						+ "' as a threshold; using default value instead: " + x);
			}
			queryParams.setDifferThreshold(new Float(lowerThreshold));
		}

		// Upper threshold
		{
			String ut = atts.getValue("ut");
			float upperThreshold = QueryParams.DEFAULT_DIFFER_THRESHOLD;
			try {
				if (ut != null) {
					upperThreshold = Float.parseFloat(ut);
				} else {
					logger.warning("Unable to find upper threshold for query; using default value instead");
				}
			} catch (Exception x) {
				logger.warning(
						"Unable to parse '"
								+ ut
								+ "' as a upper threshold; using default value instead: " +
						x);
			}
			queryParams.setMatchThreshold(new Float(upperThreshold));
		}

		// Maximum number of matches
		{
			String mm = atts.getValue("mm");
			int maxNumMatches = QueryParams.DEFAULT_MAX_NUM_MATCHES;
			try {
				if (mm != null) {
					maxNumMatches = Integer.parseInt(mm);
				} else {
					logger.warning("Unable to find maximum matches for query; using default value instead");
				}
			} catch (Exception x) {
				logger.warning("Unable to parse '" + mm
						+ "' as maximum matches; using default value instead: " +
						x);
			}
			queryParams.setMaxNumMatches(new Integer(maxNumMatches));
		}

		// Model used
		{
			String mo = atts.getValue("mo");
			ImmutableProbabilityModel model =
				PMManager.getImmutableModelInstance(mo);
			if (model == null) {
				throw new IllegalStateException("unknown model '" + mo + "'");
			}
			if (model != getCurrentModel()) {
				setCurrentModel(model);
				DefaultHandler handler =
					((XmlAccessor) getCurrentModel().getAccessor())
							.getXmlReader();
				setContentHandler(handler);
				((XmlReader) handler).open(this);
			}
			queryParams.setModel(getCurrentModel());
		}

		// External ID (a.k.a. query title)
		{
			String qt = atts.getValue("qt");
			if (qt == null) {
				logger.warning("Unable to find value for external ID of query; using blank value instead");
				qt = "";
			}
			queryParams.setExternalId(qt);
		}

		// Timestamp for start of query
		{
			String st = atts.getValue("st");
			Date queryDate = new Date();
			try {
				if (st != null && st.charAt(4) == '-') {
					queryDate = DateHelper.parseSqlTimestamp(st);
				} else if (st != null) {
					queryDate = new Date(Long.parseLong(st));
				} else {
					logger.warning("Unable to find query timestamp; using today's date instead");
				}
			} catch (Exception x) {
				logger.warning("Unable to parse '" + st
						+ "' as a date; using today's date instead:" + x);
			}
			queryMetrics.setTimestamp(queryDate);
		}

		// Query duration
		{
			String du = atts.getValue("du");
			long duration = QueryMetrics.UNKNOWN_DURATION;
			try {
				if (du != null) {
					duration = Integer.parseInt(du);
				} else {
					logger.warning("Unable to find duration for query; using marker for unknown value instead");
				}
			} catch (Exception x) {
				logger.warning(
						"Unable to parse '"
								+ du
								+ "' as a duration; using marker for unknown value instead: " +
						x);
			}
			queryMetrics.setDuration(duration);
		}

		// Maximum number of matches
		{
			String rb = atts.getValue("rb");
			int numberBlocked = QueryParams.DEFAULT_MAX_NUM_MATCHES;
			try {
				if (rb != null) {
					numberBlocked = Integer.parseInt(rb);
				} else {
					logger.warning("Unable to find number of records blocked for query; using marker for unknown value instead");
				}
			} catch (Exception x) {
				logger.warning(
						"Unable to parse '"
								+ rb
								+ "' as number of records blocked for query; using marker for unknown value instead: " +
						x);
			}
			queryMetrics.setNumberBlocked(numberBlocked);
		}

	}

	/**
	 * Filter an end element event.
	 *
	 * @param uri
	 *            The element's Namespace URI, or the empty string.
	 * @param localName
	 *            The element's local name, or the empty string.
	 * @param qName
	 *            The element's qualified (prefixed) name, or the empty string.
	 * @exception org.xml.sax.SAXException
	 *                The client may throw an exception during processing.
	 * @see org.xml.sax.ContentHandler#endElement
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		qName = qName.intern();
		--depth;
		if (depth == 0) {
			// </Report> detected here (depth == 0)
			// Log but otherwise ignore
			logger.fine("End of element '" + qName + "'");
		} else if (depth == 1) {
			// </qu> detected here (depth == 1)
			putQuery(currentQuery);
			currentQuery = null;
		} else if (location == ER) {
			// </error> detected here
			currentQuery.addError(currentQueryError);
			currentQueryError = null;
			location = OUTSIDE;
		} else if (depth == 2) {
			// </qr>, </ma> and </blocking> detected here
			location = OUTSIDE;
		} else if (depth == 3 && location == MR) {
			location = MA;
		} else if (location == QR || location == MR) {
			getContentHandler().endElement(uri, localName, qName);
		} else if (depth == 3 && location == EX) {
			location = ER;
		}
	}

	private void putQuery(Query query) throws SAXException {
		synchronized (this) {
			try {
				while (size == BUF_SIZE && readMore) {
					wait();
				}
				if (readMore) {
					queries[(out + size) % BUF_SIZE] = query;
					++size;
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
		if (location == QR) {
			currentQuery.setQueryRecord(r);
			if (StatusUtil.isDisplayDetails()) {
				StatusUtil.printDebug("QR parsed Record class = '"
						+ r.getClass().getName() + "'");
				StatusUtil.printDebug("QR parsed Record class loader = '"
						+ r.getClass().getClassLoader() + "'");
				StatusUtil.printDebug("currentQuery Record class = '"
						+ currentQuery.getQueryRecord().getClass().getName()
						+ "'");
				StatusUtil.printDebug("currentQuery Record class loader = '"
						+ currentQuery.getQueryRecord().getClass()
								.getClassLoader() + "'");
			}

		} else {
			Record currentMatchRecord = r;
			Comparable<?> currentMatchId = currentMatchRecord.getId();
			final ActiveClues currentActiveClues = null; // unknown
			Match m =
				new Match(currentDecision, currentProbability, currentMatchId,
						currentMatchRecord, currentActiveClues);
			currentQuery.addMatch(m);
			if (StatusUtil.isDisplayDetails()) {
				StatusUtil.printDebug("MR parsed Record class = '"
						+ r.getClass().getName() + "'");
				StatusUtil.printDebug("MR parsed Record class loader = '"
						+ r.getClass().getClassLoader() + "'");
				StatusUtil.printDebug("currentMatchRecord class = '"
						+ currentMatchRecord.getClass().getName() + "'");
				StatusUtil.printDebug("currentMatchRecord class loader = '"
						+ currentMatchRecord.getClass().getClassLoader() + "'");
			}
		}
	}

	public synchronized void close() {
		readMore = false;
		mayHaveMore = false;
		this.notifyAll(); // make sure that thread ends
	}

	/**
	 * Set the value of xmlFileName.
	 * 
	 * @param v
	 *            Value to assign to xmlFileName.
	 */
	public void setXmlFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("null xml file");
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("'" + file.getAbsoluteFile()
					+ "' is not a file");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("'"
					+ file.getAbsoluteFile().toString() + "' cannot be read");
		}
		this.xmlFile = file.getAbsoluteFile();
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public void setCurrentModel(ImmutableProbabilityModel model) {
		this.currentModel = model;
	}

	public ImmutableProbabilityModel getCurrentModel() {
		return currentModel;
	}

}
