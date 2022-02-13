/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.io.File;
import java.io.Serializable;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IBlockSource;

/**
 * @author pcheung
 */
public class BlockSinkSourceFactory implements IBlockSinkSourceFactory,
		Serializable {

	static final long serialVersionUID = 271;

	private String fileDir;
	private String baseName;
	private String ext;
	private int indSink = 0;
	private int indSource = 0;

	/**
	 * This constructor takes in key parameters to create oversized block files
	 * as follows:
	 * 
   * <pre>
	 * fileDir + baseName + ind + "." + ext
   * </pre>
	 */
	public BlockSinkSourceFactory(String fileDir, String baseName, String ext) {
		this.fileDir = fileDir;
		this.baseName = baseName;
		this.ext = ext;
	}

	/**
	 * This creates the next sink in seqence. It creates a binary file, and no
	 * append.
	 */
	@Override
	public IBlockSink getNextSink() throws BlockingException {
		indSink++;
		return new BlockSink(fileDir + baseName + indSink + "." + ext,
				EXTERNAL_DATA_FORMAT.STRING);
	}

	/** This creates the next source in sequence. It creates a binary file. */
	@Override
	public IBlockSource getNextSource() throws BlockingException {
		indSource++;
		return new BlockSource(fileDir + baseName + indSource + "." + ext,
				EXTERNAL_DATA_FORMAT.STRING);
	}

	/** Creates an IOverSizedSource for an existing IOversizedSink. */
	@Override
	public IBlockSource getSource(IBlockSink sink) throws BlockingException {
		return new BlockSource(sink.getInfo(), EXTERNAL_DATA_FORMAT.STRING);
	}

	/** Creates an IOverSizedSource for an existing IOversizedSink. */
	@Override
	public IBlockSink getSink(IBlockSource source) throws BlockingException {
		return new BlockSink(source.getInfo(), EXTERNAL_DATA_FORMAT.STRING);
	}

	/**
	 * This method returns the number of oversized block data sink files that
	 * have been created.
	 */
	@Override
	public int getNumSink() {
		return indSink;
	}

	/**
	 * This method returns the number of oversized block data source files that
	 * have been created.
	 */
	@Override
	public int getNumSource() {
		return indSource;
	}

	@Override
	public void removeSink(IBlockSink sink) throws BlockingException {
		File f = new File(sink.getInfo());
		f.delete();
	}

	@Override
	public void removeSource(IBlockSource source) throws BlockingException {
		File f = new File(source.getInfo());
		f.delete();
	}

	@Override
	public String toString() {
		return "BlockSinkSourceFactory [fileDir=" + fileDir + ", baseName="
				+ baseName + ", ext=" + ext + ", indSink=" + indSink
				+ ", indSource=" + indSource + "]";
	}

}
