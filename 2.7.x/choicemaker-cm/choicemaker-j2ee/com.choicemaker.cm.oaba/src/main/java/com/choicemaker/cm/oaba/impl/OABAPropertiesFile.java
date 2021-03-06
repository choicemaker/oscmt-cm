/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.oaba.core.IOABAProperties;

/**
 * This object gets the OABA properties from a file. It's important than the
 * property names in the files match those in this object.
 * 
 * @author pcheung
 *
 */
public class OABAPropertiesFile implements IOABAProperties {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(OABAPropertiesFile.class
			.getName());

	private static final String FILE_NAME = "OABA.properties";

	// private static OABAPropertiesFile oProperties = null;

	// The default values are meant to get over written.
	private int numProc = 1;
	private String tempFileDir = "/";
	private int maxBlocks = 50;
	private int maxOS = 1000;
	private int minFields = 5;
	private int interval = 5;
	private int maxChunkSize = 200000;
	private int maxChunkFiles = 800;
	private int maxMatchSize = 5000000;

	/**
	 * This constructor sets the values from the properties file.
	 */
	public OABAPropertiesFile() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(FILE_NAME));
		} catch (FileNotFoundException e) {
			log.severe("Could not open properties files: " + FILE_NAME);
			log.severe("Use Default values.");
			log.severe(e.toString());
		} catch (IOException e) {
			log.severe("Could not read properties files: " + FILE_NAME);
			log.severe("Use Default values.");
			log.severe(e.toString());
		}
		numProc = Integer.parseInt(prop.getProperty("numProcessors", "1"));
		tempFileDir = prop.getProperty("fileDir", "/");
		maxBlocks = Integer.parseInt(prop.getProperty("maxBlockSize", "50"));
		maxOS = Integer.parseInt(prop.getProperty("maxOversized", "1000"));
		minFields = Integer.parseInt(prop.getProperty("minFields", "5"));
		interval = Integer.parseInt(prop.getProperty("interval", "5"));
		maxChunkSize =
			Integer.parseInt(prop.getProperty("maxChunkSize", "200000"));
		maxChunkFiles =
			Integer.parseInt(prop.getProperty("maxChunkFiles", "800"));
		maxMatchSize =
			Integer.parseInt(prop.getProperty("maxMatchSize", "5000000"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getNumProcessors()
	 */
	@Override
	public int getNumProcessors() {
		return numProc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getTempDir()
	 */
	@Override
	public String getTempDir() {
		return tempFileDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getMaxBlockSize()
	 */
	@Override
	public int getMaxBlockSize() {
		return maxBlocks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getMaxOversized()
	 */
	@Override
	public int getMaxOversized() {
		return maxOS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getMinFields()
	 */
	@Override
	public int getMinFields() {
		return minFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getBlockDedupInterval()
	 */
	@Override
	public int getBlockDedupInterval() {
		return interval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getMaxChunkSize()
	 */
	@Override
	public int getMaxChunkSize() {
		return maxChunkSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getMaxChunkFilesOpen()
	 */
	@Override
	public int getMaxChunkFiles() {
		return maxChunkFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOABAProperties
	 * #getMaxMatchSize()
	 */
	@Override
	public int getMaxMatchSize() {
		return maxMatchSize;
	}

}
