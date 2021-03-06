/*
 * Copyright (c) 2014, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
import java.util.logging.Logger;

/**
 *
 * @author rphall
 * @version $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingScript {

	private static class LineIterator implements Iterator<String> {
		private final LineNumberReader lnr;
		private String currentLine = null;

		public LineIterator(LineNumberReader r) {
			this.lnr = r;
			nextLine();
		}

		@Override
		public boolean hasNext() {
			return this.currentLine != null;
		}

		@Override
		public String next() {
			if (this.currentLine == null) {
				throw new NoSuchElementException("no more lines");
			}
			String retVal = this.currentLine;
			nextLine();
			return retVal;
		}

		private void nextLine() {
			try {
				currentLine = lnr.readLine();
			} catch (IOException x) {
				logException("Unexpected IOException", x);
				throw new RuntimeException("Unexpected IOException", x);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static final Logger logger =
		Logger.getLogger(BlockingScript.class.getName());

	private static final String DEFAULT_SCRIPT =
		"com/choicemaker/cmtblocking/default.script";

	private static void logException(String msg, Throwable x) {
		LogUtil.logExtendedException(logger, msg, x);
	}

	private static void logInfo(String msg) {
		LogUtil.logExtendedInfo(logger, msg);
	}

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

	public Iterator<String> getIterator() throws FileNotFoundException {
		final LineNumberReader r =
			file == null ? getDefaultLineReader() : getLineReader(file);
		return new LineIterator(r);
	}

	private LineNumberReader getLineReader(File file)
			throws FileNotFoundException {
		FileReader r = new FileReader(file);
		LineNumberReader retVal = new LineNumberReader(r);
		return retVal;
	}

	void logInfo() {
		if (file == null) {
			logInfo("using default script");
		} else {

			logInfo("using script at '" + file.getAbsolutePath() + "'");
		}
	}

}
