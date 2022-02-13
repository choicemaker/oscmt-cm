/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.choicemaker.cm.oaba.core.IndexedFileObserver;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.TransitivityResult;
import com.choicemaker.cm.transitivity.core.TransitivityResultCompositeSerializer;

/**
 * This is an enhanced version of XMLSerializer. It splits the output into
 * several files each smaller than the given parameter.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes" })
public class CompositeXMLSerializer extends XMLSerializer
		implements TransitivityResultCompositeSerializer {

	private static final long serialVersionUID = 271L;

	public static final String DEFAULT_FILE_EXTENSION = "xml";

	/** Defines the output file size is checked */
	private static final int INTERVAL = 2000;

	public static void notify(IndexedFileObserver ifo, int index,
			String fileName) {
		if (ifo != null && fileName != null) {
			ifo.fileCreated(index, fileName);
		}
	}

	/** An extension appended to the base name of the output file */
	private final String fileExt;

	private final IndexedFileObserver indexedFileObserver;

	/** The one-based index of the current output file */
	private int currentIndex;

	private String currentFile;

	public CompositeXMLSerializer() {
		this(DEFAULT_FILE_EXTENSION, null);
	}

	/**
	 * @param fileExt
	 *            - extension for the output files
	 */
	public CompositeXMLSerializer(String fileExt) {
		this(fileExt, null);
	}

	public CompositeXMLSerializer(IndexedFileObserver ifo) {
		this(DEFAULT_FILE_EXTENSION, ifo);
	}

	public CompositeXMLSerializer(String fileExt, IndexedFileObserver ifo) {
		if (fileExt == null || !fileExt.equals(fileExt.trim())
				|| fileExt.isEmpty()) {
			String msg = "Invalid file extension: '" + fileExt + "'";
			throw new IllegalArgumentException(msg);
		}
		this.fileExt = fileExt;
		this.indexedFileObserver = ifo;
		this.currentIndex = 1;
	}

	/**
	 * Serializes a transitivity result to a writer
	 * 
	 * @param result
	 *            a non-null transitivity result
	 * @param fileBase
	 *            The name stem of an output file, excluding any index,
	 *            extension or qualifying path
	 * @param maxFileSize
	 *            the approximate maximum number of records in an output file
	 * @throws IOException
	 */
	@Override
	public void serialize(TransitivityResult result, String fileBase,
			int maxFileSize) throws IOException {
		if (result == null) {
			throw new IllegalArgumentException("null transivity result");
		}
		if (fileBase == null || !fileBase.equals(fileBase.trim())
				|| fileBase.isEmpty()) {
			String msg = "Invalid file name stem: '" + fileBase + "'";
			throw new IllegalArgumentException(msg);
		}
		if (maxFileSize < 1) {
			String msg = "Invalid max file size: " + maxFileSize;
			throw new IllegalArgumentException(msg);
		}

		final String fn =
			FileUtils.getFileName(fileBase, fileExt, currentIndex);
		setCurrentFileName(fn);
		notify(indexedFileObserver, currentIndex, fn);
		Writer writer = new FileWriter(getCurrentFileName(), false);

		writeHeader(result, writer);

		int count = 0;
		int fileCount = 0;
		Iterator it = result.getNodes();
		while (it.hasNext()) {
			StringBuffer sb = new StringBuffer();

			CompositeEntity ce = (CompositeEntity) it.next();

			sb.append(writeCompositeEntity(ce, writer));
			sb.append(NEW_LINE);
			writer.write(sb.toString());
			++count;
			++fileCount;

			if (count % INTERVAL == 0) {
				writer.flush();
			}
			if (fileCount >= maxFileSize) {
				writeFooter(writer);
				writer.flush();
				writer.close();

				currentIndex++;
				final String fn2 =
					FileUtils.getFileName(fileBase, fileExt, currentIndex);
				setCurrentFileName(fn2);
				fileCount = 0;
				notify(indexedFileObserver, currentIndex, fn2);
				writer = new FileWriter(getCurrentFileName(), false);
				writeHeader(result, writer);
			}
		}

		writeFooter(writer);
		writer.flush();
		writer.close();
	}

	@Override
	public String getCurrentFileName() {
		return currentFile;
	}

	protected void setCurrentFileName(String currentFile) {
		this.currentFile = currentFile;
	}

}
