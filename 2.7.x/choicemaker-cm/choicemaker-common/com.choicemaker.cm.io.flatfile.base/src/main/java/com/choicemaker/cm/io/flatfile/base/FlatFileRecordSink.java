/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.util.FileUtilities;

/**
 * Description
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.2 $ $Date: 2010/03/28 09:10:40 $
 */
public class FlatFileRecordSink implements RecordSink {

	private static final Logger log = Logger.getLogger(FlatFileRecordSink.class.getName());

	private String name;
	private String fileNamePrefix;
	private String fileNameSuffix;
	private boolean multiFile;
	private boolean singleLine;
	private boolean fixedLength;
	private char sep;
	private boolean tagged;
	private boolean filter;
	private ImmutableProbabilityModel model;
	private FlatFileRecordOutputter recordOutputter;
	private String[] fileNames;
	private Writer[] ws;
	private int[] descWidths;

	public FlatFileRecordSink() {
	}

	public FlatFileRecordSink(
		String name,
		String rawFileNamePrefix,
		String fileNameSuffix,
		boolean multiFile,
		boolean singleLine,
		boolean fixedLength,
		char sep,
		boolean tagged,
		boolean filter,
		ImmutableProbabilityModel model) {
		this.name = name;
		setRawFileNamePrefix(rawFileNamePrefix);
		this.fileNameSuffix = fileNameSuffix;
		this.multiFile = multiFile;
		this.singleLine = singleLine;
		this.fixedLength = fixedLength;
		this.sep = sep;
		this.tagged = tagged;
		this.filter = filter;
		setModel(model);
	}

	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}

	public ImmutableProbabilityModel getModel() {
		return model;
	}

	public void setRawFileNamePrefix(String fn) {
		fileNamePrefix = FileUtilities.getAbsoluteFile(new File(name).getAbsoluteFile().getParentFile(), fn).toString();
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public String getFileNameSuffix() {
		return fileNameSuffix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public boolean isTagged() {
		return tagged;
	}

	public void setTagged(boolean v) {
		this.tagged = v;
	}

	public void open() throws IOException {
		FlatFileAccessor ffacc = (FlatFileAccessor) model.getAccessor();
		descWidths = ffacc.getDescWidths();
		recordOutputter = ffacc.getFlatFileRecordOutputter(multiFile, singleLine, fixedLength, sep, tagged, descWidths[0], filter);
		final String[] typeNames = ffacc.getFlatFileFileNames();
		final int n = typeNames.length;
		fileNames = new String[n];
		ws = new Writer[n];
		if (multiFile) {
			for (int i = 1; i < n; ++i) {
				fileNames[i] = fileNamePrefix + typeNames[i] + fileNameSuffix;
				File f = new File(fileNames[i]).getAbsoluteFile();
				if (f.exists()) {
					log.severe("Overwriting an existing file: " + fileNames[i]);
				}
				// FIXME ??
				FileOutputStream o = new FileOutputStream(f);
				// log.warning("BUG: does not handle unicode? " + o);
				// ENDFIXME
				ws[i] = new OutputStreamWriter(new BufferedOutputStream(o));
			}
		} else {
			fileNames[0] = fileNamePrefix + typeNames[0] + fileNameSuffix;
			File f = new File(fileNames[0]).getAbsoluteFile();
			if (f.exists()) {
				log.severe("Overwriting an existing file: " + fileNames[0]);
			}
			// FIXME ??
			FileOutputStream o = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(o);
			// log.warning("BUG: does not handle unicode? " + o);
			// ENDFIXME
			Writer w = new OutputStreamWriter(bos);
			for (int i = 0; i < n; ++i) {
//				ws[i] = new OutputStreamWriter(bos);
				ws[i] = w;
			}
		}
	}

	public void close() throws IOException {
		if (multiFile) {
			for (int i = 1; i < ws.length; ++i) {
				ws[i].flush();
				ws[i].close();
				log.fine("FlatFileRecordSink closed: " + fileNames[i]);
			}
		} else {
			ws[0].flush();
			ws[0].close();
			log.fine("FlatFileRecordSink closed: " + fileNames[0]);
		}
	}

	public void put(Record r) throws IOException {
		recordOutputter.put(ws, r);
	}

	/**
	 * @see com.choicemaker.cm.core.Sink#flush()
	 */
	public void flush() throws IOException {
		if (multiFile) {
			for (int i = 1; i < ws.length; ++i) {
				ws[i].flush();
			}
		} else {
			ws[0].flush();
		}
	}

}
