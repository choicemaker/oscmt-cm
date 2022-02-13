/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IComparisonArraySink;
import com.choicemaker.cm.oaba.core.IComparisonArraySinkSourceFactory;
import com.choicemaker.cm.oaba.core.IComparisonArraySource;

/**
 * This is file implementation of IComparisonGroupSinkSourceFactory.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({ "rawtypes" })
public class ComparisonArraySinkSourceFactory implements
		IComparisonArraySinkSourceFactory {

	private String fileDir;
	private String nameBase;
	private String ext;
	private int indSink = 0;
	private int indSource = 0;

	/**
	 * This constructor takes in key parameters to create RecordIDSink or
	 * RecordIDSource files as follows:
	 * 
	 * fileDir + nameBase + ind + "." + ext
	 * 
	 * @param fileDir
	 * @param nameBase
	 * @param ext
	 */
	public ComparisonArraySinkSourceFactory(String fileDir, String nameBase,
			String ext) {
		this.fileDir = fileDir;
		this.nameBase = nameBase;
		this.ext = ext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory#getNextSink()
	 */
	@Override
	public IComparisonArraySink getNextSink() throws BlockingException {
		indSink++;
		return new ComparisonArraySink(
				fileDir + nameBase + indSink + "." + ext,
				EXTERNAL_DATA_FORMAT.STRING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory#getNextSource()
	 */
	@Override
	public IComparisonArraySource getNextSource() throws BlockingException {
		indSource++;
		return new ComparisonArraySource(fileDir + nameBase + indSource + "."
				+ ext, EXTERNAL_DATA_FORMAT.STRING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory#getNumSink()
	 */
	@Override
	public int getNumSink() {
		return indSink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory#getNumSource()
	 */
	@Override
	public int getNumSource() {
		return indSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory
	 * #getSource(com.choicemaker.cm.io.blocking
	 * .automated.offline.core.IComparisonGroupSink)
	 */
	@Override
	public IComparisonArraySource getSource(IComparisonArraySink sink)
			throws BlockingException {
		return new ComparisonArraySource(sink.getInfo(),
				EXTERNAL_DATA_FORMAT.BINARY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory
	 * #getSink(com.choicemaker.cm.aba
	 * .offline.core.IComparisonGroupSource)
	 */
	@Override
	public IComparisonArraySink getSink(IComparisonArraySource source)
			throws BlockingException {
		return new ComparisonArraySink(source.getInfo(),
				EXTERNAL_DATA_FORMAT.BINARY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory
	 * #removeSink(com.choicemaker.cm.io.blocking
	 * .automated.offline.core.IComparisonGroupSink)
	 */
	@Override
	public void removeSink(IComparisonArraySink sink) throws BlockingException {
		sink.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonGroupSinkSourceFactory
	 * #removeSource(com.choicemaker.cm.io.blocking
	 * .automated.offline.core.IComparisonGroupSource)
	 */
	@Override
	public void removeSource(IComparisonArraySource source)
			throws BlockingException {
		source.delete();
	}

}
