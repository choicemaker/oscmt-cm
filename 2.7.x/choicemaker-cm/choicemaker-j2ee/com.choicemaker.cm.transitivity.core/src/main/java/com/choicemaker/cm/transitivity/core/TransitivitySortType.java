/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import java.util.Comparator;

import com.choicemaker.cm.transitivity.core.TransitivityResultSerializer.Record;
import com.choicemaker.cm.transitivity.core.TransitivityResultSerializer.SortByHoldMergeID;
import com.choicemaker.cm.transitivity.core.TransitivityResultSerializer.SortByID;

public enum TransitivitySortType {
	
	SORT_BY_ID("R3L", new SortByID()), SORT_BY_HOLD_MERGE_ID("H3L", new SortByHoldMergeID());

	private final String fileExtension;
	private final Comparator<Record> comparator;

	TransitivitySortType(String ext, Comparator<Record> trcs) {
		assert ext != null && ext.equals(ext.trim()) && !ext.isEmpty();
		assert trcs != null;
		fileExtension = ext;
		this.comparator = trcs;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public Comparator<Record> getComparator() {
		return comparator;
	}

}