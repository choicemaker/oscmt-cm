/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
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

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSink;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.util.FileUtilities;

/**
 * @author    Martin Buechi
 */
public class FlatFileMarkedRecordPairSink implements MarkedRecordPairSink {
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
	private FileOutputStream[] outFile;
	private Writer[] ws;
	private int[] descWidths;

	public FlatFileMarkedRecordPairSink() {
	}

	public FlatFileMarkedRecordPairSink(
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

	public void setRawFileNamePrefix(String fn) {
//		rawFileNamePrefix = fn;
		fileNamePrefix =
			FileUtilities.getAbsoluteFile(new File(name).getAbsoluteFile().getParentFile(), fn).toString();
	}

	public String getFileNameSuffix() {
		return fileNameSuffix;
	}

	@Override
	public void open() throws IOException {
		Accessor acc = model.getAccessor();
		FlatFileAccessor ffacc = (FlatFileAccessor) acc;
		descWidths = ffacc.getDescWidths();
		recordOutputter =
			ffacc.getFlatFileRecordOutputter(multiFile, singleLine, fixedLength, sep, tagged, descWidths[0], filter);
		String[] typeNames = ffacc.getFlatFileFileNames();
		int n = typeNames.length;
		outFile = new FileOutputStream[n];
		ws = new Writer[n];
		if (multiFile) {
			for (int i = 0; i < n; ++i) {
				FileOutputStream o =
					new FileOutputStream(new File(fileNamePrefix + typeNames[i] + fileNameSuffix).getAbsoluteFile());
				outFile[i] = o;
				ws[i] = new OutputStreamWriter(new BufferedOutputStream(o));
			}
		} else {
			FileOutputStream o =
				new FileOutputStream(new File(fileNamePrefix + typeNames[0] + fileNameSuffix).getAbsoluteFile());
			Writer w = new OutputStreamWriter(new BufferedOutputStream(o));
			for (int i = 0; i < n; ++i) {
				outFile[i] = o;
				ws[i] = w;
			}
			w.close();
		}
	}

	@Override
	public void close() throws IOException {
		if (multiFile) {
			for (int i = 0; i < outFile.length; ++i) {
				ws[i].flush();
				outFile[i].close();
			}
		} else {
			ws[0].flush();
			outFile[0].close();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	/**
	 * Get the value of tagged.
	 * @return value of tagged.
	 */
	public boolean isTagged() {
		return tagged;
	}

	/**
	 * Set the value of tagged.
	 * @param v  Value to assign to tagged.
	 */
	public void setTagged(boolean v) {
		this.tagged = v;
	}

	@Override
	public void put(ImmutableRecordPair r) throws IOException {
		putMarkedRecordPair((MutableMarkedRecordPair) r);
	}

	@Override
	public void putMarkedRecordPair(ImmutableMarkedRecordPair r) throws IOException {
		Writer w = ws[0];
		if (tagged) {
			FlatFileOutput.write(w, "MP", fixedLength, sep, filter, true, descWidths[0]);
		}
		FlatFileOutput.write(w, r.getMarkedDecision().toString(), fixedLength, sep, filter, !tagged, descWidths[3]);
		FlatFileOutput.write(w, DateHelper.format(r.getDateMarked()), fixedLength, sep, filter, false, descWidths[4]);
		FlatFileOutput.write(w, r.getUser(), fixedLength, sep, filter, false, descWidths[5]);
		FlatFileOutput.write(w, r.getSource(), fixedLength, sep, filter, false, descWidths[6]);
		FlatFileOutput.write(w, r.getComment(), fixedLength, sep, filter, false, descWidths[7]);
		if (!singleLine)
			w.write(Constants.LINE_SEPARATOR);
		recordOutputter.put(ws, r.getQueryRecord());
		recordOutputter.put(ws, r.getMatchRecord());
		if (singleLine)
			w.write(Constants.LINE_SEPARATOR);
	}

	@Override
	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	/** NOP for now */
	@Override
	public void flush() throws IOException {
	}

}
