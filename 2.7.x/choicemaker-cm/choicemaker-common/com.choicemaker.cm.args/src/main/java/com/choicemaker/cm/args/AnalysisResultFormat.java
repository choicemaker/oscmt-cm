/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.args;

/**
 * A type that defines how the result of the batch analysis will be organized
 * for output:
 * <ul>
 * <li>XML - as an xml file,
 * <li>H3L as a list of triplets &lt; i,j,k&gt; where i is a connected (by hold or
 * match) record set ID, j is a linked record set ID, k is a record ID.
 * <li>R3L as a list of triplets &lt; j,j,k&gt; where i is a record ID, j is a
 * linked record set ID, k is a connected record set ID.
 * </ul>
 * <p>
 *
 * @author emoussikaev
 */
public enum AnalysisResultFormat {

	// These file extensions must be kept synchronized with
	// com.choicemaker.cm.transitivity.core.TransitivitySortType
	// (Do not import TransitivitySortType, to keep this module independent
	// of the transitivity core module.)
	XML("xml"), SORT_BY_HOLD_GROUP("H3L"), SORT_BY_RECORD_ID("R3L");

	private final String fileExtension;
	private final String displayName;

	AnalysisResultFormat(String ext) {
		this(ext, null);
	}

	AnalysisResultFormat(String ext, String ds) {
		assert ext != null && ext.equals(ext.trim()) && !ext.isEmpty();
		assert ds == null || (ds.equals(ds.trim()) && !ds.isEmpty());
		this.fileExtension = ext;
		this.displayName = ds;
	}

	@Override
	public String toString() {
		return name();
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getDisplayName() {
		return displayName == null ? name() : displayName;
	}

	/**
	 * @param ext a file extension (excluding the leadning '.')
	 * @return may be null if <code>ext</code> is null
	 * @throws IllegalArgumentException
	 *             if <code>ext</code> is not recognized
	 */
	public static AnalysisResultFormat fromFileExtension(String ext) {
		AnalysisResultFormat retVal = null;
		if (ext != null) {
			ext = ext.trim();
			if (XML.getFileExtension().equalsIgnoreCase(ext)) {
				retVal = XML;
			} else if (SORT_BY_HOLD_GROUP.getFileExtension()
					.equalsIgnoreCase(ext)) {
				retVal = SORT_BY_HOLD_GROUP;
			} else if (SORT_BY_RECORD_ID.getFileExtension()
					.equalsIgnoreCase(ext)) {
				retVal = SORT_BY_RECORD_ID;
			} else {
				String msg = "invalid argument: '" + ext + "'";
				throw new IllegalArgumentException(msg);
			}
		}
		return retVal;
	}

}
