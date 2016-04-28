/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base;

import java.util.ArrayList;
import java.util.List;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.SinkFactory;
import com.choicemaker.cm.core.Source;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class FlatFileMarkedRecordPairSinkFactory implements SinkFactory {
	private String fileNameBase;
	private String flatfileFileName;
	private String extension;
	private boolean multiFile;
	private boolean singleLine;
	private boolean fixedLength;
	private char sep;
	private boolean tagged;
	private boolean filter;
	private ImmutableProbabilityModel model;
	private int num;
	private List sources;

	public FlatFileMarkedRecordPairSinkFactory(
		String fileNameBase,
		String flatfileFileName,
		String extension,
		boolean multiFile,
		boolean singleLine,
		boolean fixedLength,
		char sep,
		boolean tagged,
		boolean filter,
		ImmutableProbabilityModel model) {
		this.fileNameBase = fileNameBase;
		this.flatfileFileName = flatfileFileName;
		this.extension = extension;
		this.multiFile = multiFile;
		this.singleLine = singleLine;
		this.fixedLength = fixedLength;
		this.sep = sep;
		this.tagged = tagged;
		this.filter = filter;
		this.model = model;
		num = 0;
		this.sources = new ArrayList();
	}

	public Sink getSink() {
		String tName = fileNameBase + num + "." + Constants.MRPS_EXTENSION;
		String tFileName = flatfileFileName + num;
		++num;
		sources.add(
			new FlatFileMarkedRecordPairSource(
				tName,
				tFileName,
				extension,
				multiFile,
				singleLine,
				fixedLength,
				sep,
				tagged,
				model));
		return new FlatFileMarkedRecordPairSink(
			tName,
			tFileName,
			extension,
			multiFile,
			singleLine,
			fixedLength,
			sep,
			tagged,
			filter,
			model);
	}

	public Source[] getSources() {
		return (Source[]) sources.toArray(new Source[sources.size()]);
	}
}
