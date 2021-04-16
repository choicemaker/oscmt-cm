/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.io.File;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IRecValSink;
import com.choicemaker.cm.oaba.core.IRecValSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IRecValSource;

/**
 * @author pcheung
 *
 */
public class RecValSinkSourceFactory implements IRecValSinkSourceFactory {

	private static final EXTERNAL_DATA_FORMAT TYPE =
		EXTERNAL_DATA_FORMAT.STRING;

	private String fileDir;
	private String baseName;
	private String ext;
	private int indSink = 0;
	private int indSource = 0;

	/**
	 * This constructor takes in key parameters to create rec_id, val_id files
	 * as follows:
	 * 
	 * fileDir + baseName + ind + "." + ext
	 */
	public RecValSinkSourceFactory(String fileDir, String baseName, String ext) {
		this.fileDir = fileDir;
		this.baseName = baseName;
		this.ext = ext;
	}

	/**
	 * This creates the next sink in sequence. It creates a binary file, and no
	 * append.
	 */
	@Override
	public IRecValSink getNextSink() throws BlockingException {
		indSink++;
		return new RecValSink(fileDir + baseName + indSink + "." + ext, TYPE);
	}

	/** This creates the next source in sequence. It creates a binary file. */
	@Override
	public IRecValSource getNextSource() throws BlockingException {
		indSource++;
		return new RecValSource(fileDir + baseName + indSource + "." + ext,
				TYPE);
	}

	/** Creates an IRecValSource for an existing IRecValSink. */
	@Override
	public IRecValSource getSource(IRecValSink sink) throws BlockingException {
		return new RecValSource(sink.getInfo(), TYPE);
	}

	/** Creates an IRecValSink for an existing IRecValSource. */
	@Override
	public IRecValSink getSink(IRecValSource source) throws BlockingException {
		return new RecValSink(source.getInfo(), TYPE);
	}

	/**
	 * This method returns the number of rec_id, val_id data sink files that
	 * have been created.
	 */
	@Override
	public int getNumSink() {
		return indSink;
	}

	/**
	 * This method returns the number of rec_id, val_id data source files that
	 * have been created.
	 */
	@Override
	public int getNumSource() {
		return indSource;
	}

	@Override
	public void removeSink(IRecValSink sink) throws BlockingException {
		File f = new File(sink.getInfo());
		f.delete();
	}

	@Override
	public void removeSource(IRecValSource source) throws BlockingException {
		File f = new File(source.getInfo());
		f.delete();
	}

}
