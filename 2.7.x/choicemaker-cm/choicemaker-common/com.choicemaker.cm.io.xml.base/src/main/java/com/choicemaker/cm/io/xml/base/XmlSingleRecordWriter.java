/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class XmlSingleRecordWriter {

	protected static Logger log = Logger.getLogger(XmlSingleRecordWriter.class.getName());

	public static String writeRecord(
		ImmutableProbabilityModel probabilityModel,
		Record record,
		boolean header) {
		StringWriter sw = new StringWriter();
		if (header) {
			sw.write(
				"<?xml version=\"1.0\" encoding=\""
					+ XmlMarkedRecordPairSink.getEncoding()
					+ "\"?>"
					+ Constants.LINE_SEPARATOR);
		}
		try {
			((XmlAccessor) probabilityModel.getAccessor())
				.getXmlRecordOutputter()
				.put(
				sw,
				record);
		} catch (IOException e) {
			String msg = "Unable to write record " + record.getId() + ": " + e.toString();
			log.severe(msg);
			sw.write("<error>" + msg + "</error>");
		}

		return sw.toString();
	}
}
