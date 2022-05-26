/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.analyzer.matcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSink;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class SimpleRecordSink implements MarkedRecordPairSink {
	private static DecimalFormat df = new DecimalFormat("##0.0000");
	
	private File file;
	private BufferedWriter w;
	private FileWriter fw;
	
	public SimpleRecordSink(File file) {
		this.file = file;
	}

	/**
	 * @see com.choicemaker.cm.core.base.MarkedRecordPairSink#putMarkedRecordPair(com.choicemaker.cm.core.base.MarkedRecordPair)
	 */
	@Override
	public void putMarkedRecordPair(ImmutableMarkedRecordPair r) throws IOException {
		w.write(r.getQueryRecord().getId() + "," + r.getMatchRecord().getId() + "," + r.getMarkedDecision() + "," + df.format(r.getProbability()) + Constants.LINE_SEPARATOR);
	}

	/**
	 * @see com.choicemaker.cm.core.base.RecordPairSink#put(com.choicemaker.cm.core.base.RecordPair)
	 */
	@Override
	public void put(ImmutableRecordPair r) throws IOException {
		putMarkedRecordPair((ImmutableMarkedRecordPair)r);
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#open()
	 */
	@Override
	public void open() throws IOException {
		fw = new FileWriter(file);
		w = new BufferedWriter(fw);
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#close()
	 */
	@Override
	public void close() throws IOException {
		w.flush();
		fw.flush();
		w.close();
		fw.close();
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#flush()
	 */
	@Override
	public void flush() throws IOException {
		w.flush();
		fw.flush();
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#getModelName()
	 */
	@Override
	public String getName() {
		return null;
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#setModelName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#getModel()
	 */
	@Override
	public ImmutableProbabilityModel getModel() {
		return null;
	}

	/**
	 * @see com.choicemaker.cm.core.base.Sink#setModel(com.choicemaker.cm.core.base.ProbabilityModel)
	 */
	@Override
	public void setModel(ImmutableProbabilityModel m) {
	}

}
