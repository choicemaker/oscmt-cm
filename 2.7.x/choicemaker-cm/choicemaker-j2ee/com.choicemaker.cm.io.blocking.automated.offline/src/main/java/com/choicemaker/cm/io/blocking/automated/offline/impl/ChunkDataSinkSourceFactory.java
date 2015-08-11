/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import java.io.File;
import java.util.ArrayList;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConf;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkDataSinkSourceFactory;
import com.choicemaker.cm.io.flatfile.base.FlatFileRecordSource;

/**
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class ChunkDataSinkSourceFactory implements IChunkDataSinkSourceFactory {

	// private static final Logger log =
	// Logger.getLogger(ChunkDataSinkSourceFactory.class.getName());

	private String fileDir;
	private String baseName;
	private ImmutableProbabilityModel model;
	private int ind = 0;
	private int indSource = 0;
	private ArrayList removeFiles = new ArrayList();

	public ChunkDataSinkSourceFactory(String fileDir, String baseName,
			ImmutableProbabilityModel model) {
		this.fileDir = fileDir;
		this.baseName = baseName;
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.IChunkDataSinkFactory
	 * #getNextSink()
	 */
	@Override
	public RecordSink getNextSink() throws BlockingException {
		// create sink
		ind++;
		String rsDescriptorName = fileDir + baseName + ind + ".rs";
		File F = new File(rsDescriptorName);
		rsDescriptorName = F.getAbsolutePath();
		removeFiles.add(rsDescriptorName);

		F = new File(fileDir + baseName + ind);
		String dataFile = F.getAbsolutePath();

		FlatFileRecordSource tmpRs =
			new FlatFileRecordSource(rsDescriptorName, dataFile, ".txt", false,
					false, false, '|', true, model);
		RecordSink sink = (RecordSink) tmpRs.getSink();
		removeFiles.add(dataFile + ".txt");
		try {
			RecordSourceXmlConf.add(tmpRs);
		} catch (XmlConfException e) {
			throw new BlockingException(e.getMessage(), e);
		}

		return sink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.IChunkDataSinkFactory
	 * #getNumSink()
	 */
	@Override
	public int getNumSink() {
		return ind;
	}

	/**
	 * Gets the next record source. This only returns a source from a previously
	 * created sink.
	 */
	@Override
	public RecordSource getNextSource() throws BlockingException {
		RecordSource rs = null;
		try {
			indSource++;
			File f = new File(fileDir + baseName + indSource + ".rs");
			String str = f.toURL().toString();
			rs = RecordSourceXmlConf.getRecordSource(str);
		} catch (Exception ex) {
			throw new BlockingException(ex.toString());
		}
		return rs;
	}

	/** Gets the number of sequence source created. */
	@Override
	public int getNumSource() {
		return indSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.IChunkDataSinkFactory
	 * #removeSink(com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IChunkRecordIdSink)
	 */
	@Override
	public void removeAllSinks() throws BlockingException {
		for (int i = 0; i < removeFiles.size(); i++) {
			String name = (String) removeFiles.get(i);
			File f = new File(name);
			f.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IChunkDataSinkSourceFactory#removeAllSinks(int)
	 */
	@Override
	public void removeAllSinks(int numChunks) throws BlockingException {
		for (int i = 1; i <= numChunks; i++) {
			String name = fileDir + baseName + i + ".rs";
			File f = new File(name);
			f.delete();

			name = fileDir + baseName + i + ".txt";
			f = new File(name);
			f.delete();

		}

	}

}
