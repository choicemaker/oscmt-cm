/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.TransitivityResult;
import com.choicemaker.cm.transitivity.core.TransitivityResultCompositeSerializer;
import com.choicemaker.cm.transitivity.core.TransitivityResultSerializer;
import com.choicemaker.cm.transitivity.core.TransitivitySortType;

/**
 * This object takes a TransitivityResult and a Writer and outputs the clusters
 * as RECORD_ID, MATCH_GROUP_ID, HOLD_GROUP_ID. I can split the result into many
 * files in order to get around the Windows file size limit.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({ "rawtypes" })
public class CompositeTextSerializer extends TextSerializer implements
		TransitivityResultCompositeSerializer {

	private static final long serialVersionUID = 271L;

	/** Defines the output file size is checked */
	private static final int INTERVAL = 2000;

	/** An extension appended to the base name of the output file */
	private final String fileExt;

	/** The one-based index of the current output file */
	private int currentIndex;

	/** The one-based index of the current output file */
	private String currentFileName;

	public CompositeTextSerializer(TransitivitySortType transitivitySortType) {
		super(transitivitySortType.getComparator());
		assert transitivitySortType != null;
		fileExt = transitivitySortType.getFileExtension();
		currentIndex = 1;
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
		Writer writer = new FileWriter(getCurrentFileName(), false);

		// first get all the record IDs from the clusters.
		List<Record> records = new ArrayList<>();
		Iterator it = result.getNodes();
		while (it.hasNext()) {
			CompositeEntity ce = (CompositeEntity) it.next();
			getCompositeEntity(ce, records);
		}

		// second, sort them accordingly
		Object[] recs = handleSort(records);

		// free memory
		records = null;

		// third, write them out.
		int s = recs.length;
		for (int i = 0; i < s; i++) {
			TransitivityResultSerializer.Record r =
				(TransitivityResultSerializer.Record) recs[i];
			writer.write(printRecord(r));
			if (i % INTERVAL == 0) {
				writer.flush();
				if (FileUtils.isFull(fileBase, fileExt, currentIndex,
						maxFileSize)) {
					writer.close();
					currentIndex++;
					writer =
						new FileWriter(FileUtils.getFileName(fileBase, fileExt,
								currentIndex), false);
				}
			}
		} // end for

		writer.flush();
		writer.close();

		// free up memory
		recs = null;
	}

	@Override
	public String getCurrentFileName() {
		return currentFileName;
	}

	protected void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

}
