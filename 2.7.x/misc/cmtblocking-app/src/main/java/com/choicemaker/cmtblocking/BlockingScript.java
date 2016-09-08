/*
 * @(#)$RCSfile: BlockingScript.java,v $        $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author rphall
 * @version $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingScript {

	private static final String DEFAULT_SCRIPT =
		"com/choicemaker/cmtblocking/default.script";
	private final File file;

	public BlockingScript(String scriptFileName) {
		if (scriptFileName != null && scriptFileName.trim().length() > 0) {
			this.file = new File(scriptFileName);
		} else {
			this.file = null;
		}
	}

	private LineNumberReader getDefaultLineReader() {
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(DEFAULT_SCRIPT);
		Reader r = new InputStreamReader(is);
		LineNumberReader retVal = new LineNumberReader(r);
		return retVal;
	}

	private LineNumberReader getLineReader(File file)
			throws FileNotFoundException {
		FileReader r = new FileReader(file);
		LineNumberReader retVal = new LineNumberReader(r);
		return retVal;
	}

	private static class LineIterator implements Iterator<String> {
		private final LineNumberReader lnr;
		private String currentLine = null;

		public LineIterator(LineNumberReader r) {
			this.lnr = r;
			nextLine();
		}

		public boolean hasNext() {
			return this.currentLine != null;
		}

		public String next() {
			if (this.currentLine == null) {
				throw new NoSuchElementException("no more lines");
			}
			String retVal = this.currentLine;
			nextLine();
			return retVal;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void nextLine() {
			try {
				currentLine = lnr.readLine();
			} catch (IOException x) {
				logException("Unexpected IOException", x);
				throw new RuntimeException("Unexpected IOException", x);
			}
		}
	}

	public Iterator<String> getIterator() throws FileNotFoundException {
		final LineNumberReader r =
			file == null ? getDefaultLineReader() : getLineReader(file);
		return new LineIterator(r);
	}

	void logInfo() {
		if (file == null) {
			logInfo("using default script");
		} else {

			logInfo("using script at '" + file.getAbsolutePath() + "'");
		}
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo("BlockingScript", msg);
	}

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException("BlockingScript", msg, x);
	}

}
