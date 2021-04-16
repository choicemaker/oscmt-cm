/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IComparisonTreeSink;
import com.choicemaker.cm.oaba.core.IComparisonTreeSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IComparisonTreeSource;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

/**
 * This is a file implementation of IComparisonTreeSinkSourceFactory.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({ "rawtypes" })
public class ComparisonTreeSinkSourceFactory implements
		IComparisonTreeSinkSourceFactory {

	private String fileDir;
	private String nameBase;
	private String ext;
	private int indSink = 0;
	private int indSource = 0;
	private RECORD_ID_TYPE dataType;

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
	public ComparisonTreeSinkSourceFactory(String fileDir, String nameBase,
			String ext, RECORD_ID_TYPE dataType) {
		this.fileDir = fileDir;
		this.nameBase = nameBase;
		this.ext = ext;
		this.dataType = dataType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory#getNextSink()
	 */
	@Override
	public IComparisonTreeSink getNextSink() throws BlockingException {
		indSink++;
		return new ComparisonTreeSink(fileDir + nameBase + indSink + "." + ext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory#getNextSource()
	 */
	@Override
	public IComparisonTreeSource getNextSource() throws BlockingException {
		indSource++;
		return new ComparisonTreeSource(fileDir + nameBase + indSource + "."
				+ ext, dataType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory#getNumSink()
	 */
	@Override
	public int getNumSink() {
		return indSink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory#getNumSource()
	 */
	@Override
	public int getNumSource() {
		return indSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory
	 * #getSource(com.choicemaker.cm.io.blocking.
	 * automated.offline.core.IComparisonTreeSink)
	 */
	@Override
	public IComparisonTreeSource getSource(IComparisonTreeSink sink)
			throws BlockingException {
		return new ComparisonTreeSource(sink.getInfo(), dataType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory
	 * #getSink(com.choicemaker.cm.aba
	 * .offline.core.IComparisonTreeSource)
	 */
	@Override
	public IComparisonTreeSink getSink(IComparisonTreeSource source)
			throws BlockingException {
		return new ComparisonTreeSink(source.getInfo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory
	 * #removeSink(com.choicemaker.cm.io.blocking
	 * .automated.offline.core.IComparisonTreeSink)
	 */
	@Override
	public void removeSink(IComparisonTreeSink sink) throws BlockingException {
		sink.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparisonTreeSinkSourceFactory
	 * #removeSource(com.choicemaker.cm.io.blocking
	 * .automated.offline.core.IComparisonTreeSource)
	 */
	@Override
	public void removeSource(IComparisonTreeSource source)
			throws BlockingException {
		source.delete();
	}

}
