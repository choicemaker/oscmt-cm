/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IndexedFileObserver;
import com.choicemaker.util.Precondition;

/**
 * This is a more intelligent version of MatchRecord2Sink. It allows the user to
 * define how big a file can get. The file name follow this mattern:
 * [base]_i.[extension].
 * 
 * [base]_1.[extension] is the first created. If more files are needed, i gets
 * incremented.
 * 
 * Please note that file size checking is inexact due to write buffer. The file
 * size could jump from 0 to 17.6 MB. If you set the file limit to 100MB, you
 * could get a file that is 117 MB.
 * 
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class MatchRecord2CompositeSink implements IMatchRecord2Sink {

	public static final int DEFAULT_INTERVAL = 100000;

	/** Returns the minimum of maxFileSize and DEFAULT_INTERVAL */
	public static int computeInterval(final long maxFileSize) {
		Precondition.assertBoolean(maxFileSize > 0);
		int retVal = DEFAULT_INTERVAL;
		if (maxFileSize < DEFAULT_INTERVAL) {
			retVal = (int) maxFileSize;
		}
		assert retVal <= DEFAULT_INTERVAL;
		return retVal;
	}

	public static void notify(IndexedFileObserver ifo, int index,
			String fileName) {
		if (ifo != null && fileName != null) {
			ifo.fileCreated(index, fileName);
		}
	}

	private final String fileBase;
	private final String fileExt;
	private final long maxFileSize;
	private final int interval;
	private final IndexedFileObserver indexedFileObserver;

	private IMatchRecord2Sink currentFile;
	private int numberOfFiles = 0;

	private int count = 0;

	private boolean isAppend = false;

	/**
	 * Equivalent to invoking the 4-arg constructor with the interval set
	 * to the maxFileSize or DEFAULT_INTERVAL, whichever is less:<pre>
	 * MatchRecord2CompositeSink(
	 *     fileBase, fileExt, maxFileSize, computeInterval(maxFileSize))
	 * </pre>
	 * @param fileBase
	 *            - the base name of the MatchRecord2 sink files.
	 * @param fileExt
	 *            - the file extension of the MatchRecord2 sink files.
	 * @param maxFileSize
	 *            - The maximum size of each sink file. When the file size gets
	 *            above this threshold, a new file is created.
	 */
	public MatchRecord2CompositeSink(String fileBase, String fileExt,
			long maxFileSize) {
		this(fileBase, fileExt, maxFileSize, computeInterval(maxFileSize),
				null);
	}

	public MatchRecord2CompositeSink(String fileBase, String fileExt,
			long maxFileSize, IndexedFileObserver ifo) {
		this(fileBase, fileExt, maxFileSize, computeInterval(maxFileSize),
				ifo);
	}

	/**
	 * This constructor takes these arguments.
	 * 
	 * @param fileBase
	 *            - the base name of the MatchRecord2 sink files.
	 * @param fileExt
	 *            - the file extension of the MatchRecord2 sink files.
	 * @param maxFileSize
	 *            - The maximum size of each sink file. When the file size gets
	 *            above this threshold, a new file is created.
	 * @param interval
	 *            - This controls how often to check the size of the size. If
	 *            the interval is greater than the maxFileSize, it is truncated
	 *            to the maxFileSize
	 */
	public MatchRecord2CompositeSink(String fileBase, String fileExt,
			long maxFileSize, int interval) {
		this(fileBase, fileExt, maxFileSize, interval, null);
	}

	public MatchRecord2CompositeSink(String fileBase, String fileExt,
			long maxFileSize, int interval, IndexedFileObserver ifo) {

		Precondition.assertBoolean(maxFileSize > 0);
		Precondition.assertBoolean(interval > 0);

		this.fileBase = fileBase;
		this.fileExt = fileExt;
		this.maxFileSize = maxFileSize;
		this.interval = Math.min((int)maxFileSize,interval);
		this.indexedFileObserver = ifo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IMatchRecord2Sink
	 * #writeMatches(java.util.ArrayList)
	 */
	@Override
	public void writeMatches(List matches) throws BlockingException {
		writeMatches(matches.iterator());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IMatchRecord2Sink
	 * #writeMatches(java.util.Collection)
	 */
	@Override
	public void writeMatches(Collection c) throws BlockingException {
		writeMatches(c.iterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IMatchRecord2Sink
	 * #writeMatches(java.util.Iterator)
	 */
	@Override
	public void writeMatches(Iterator it) throws BlockingException {
		while (it.hasNext()) {
			MatchRecord2 mr = (MatchRecord2) it.next();
			writeMatch(mr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IMatchRecord2Sink
	 * #writeMatch
	 * (com.choicemaker.cm.oaba.data.MatchRecord2)
	 */
	@Override
	public void writeMatch(MatchRecord2 match) throws BlockingException {
		currentFile.writeMatch(match);
		count++;

		if (count % interval == 0 && isFull()) {
			currentFile.close();
			numberOfFiles++;
			currentFile =
				new MatchRecord2Sink(getFileName(numberOfFiles),
						EXTERNAL_DATA_FORMAT.STRING);

			if (isAppend)
				currentFile.append();
			else
				currentFile.open();
		}
	}

	/**
	 * This method checks to see if the current file is full.
	 */
	private boolean isFull() throws BlockingException {
		currentFile.flush();

		File f = new File(getFileName(numberOfFiles));

		if (f.length() >= maxFileSize) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean exists() {
		if (numberOfFiles > 0)
			return true;
		else
			return false;
	}

	@Override
	public void open() throws BlockingException {
		numberOfFiles = 1;
		final String fileName = getFileName(numberOfFiles);
		currentFile =
			new MatchRecord2Sink(fileName, EXTERNAL_DATA_FORMAT.STRING);
		notify(indexedFileObserver, numberOfFiles, fileName);
		currentFile.open();
	}

	/**
	 * This method produces the file name for the given file number.
	 * 
	 * @param fileNum
	 *            - fileNum
	 * @return String - fileName
	 */
	private String getFileName(int fileNum) {
		return fileBase + "_" + fileNum + "." + fileExt;
	}

	@Override
	public void append() throws BlockingException {
		numberOfFiles = 1;
		currentFile =
			new MatchRecord2Sink(getFileName(numberOfFiles),
					EXTERNAL_DATA_FORMAT.STRING);
		currentFile.append();
		isAppend = true;
	}

	@Override
	public boolean isOpen() {
		return currentFile != null && currentFile.isOpen();
	}

	@Override
	public void close() throws BlockingException {
		currentFile.close();
	}

	@Override
	public void flush() throws BlockingException {
		currentFile.flush();
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public String getInfo() {
		return fileBase + "." + fileExt;
	}

	@Override
	public void remove() throws BlockingException {
		for (int i = 1; i <= numberOfFiles; i++) {
			MatchRecord2Sink mrs =
				new MatchRecord2Sink(getFileName(i),
						EXTERNAL_DATA_FORMAT.STRING);
			mrs.remove();
		}
	}

}
