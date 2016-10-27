/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.choicemaker.cm.io.blocking.automated.IFieldValueCounts;

/**
 *
 * @author    mbuechi (CM 2.3)
 * @author    rphall (CM 2.7 revision)
 */

public class FieldValueCounts implements Serializable, IFieldValueCounts {

	private static final long serialVersionUID = 271;

	private static final int NUM_INTS = 1000;
	private static final Integer[] ints = new Integer[NUM_INTS];

	/**
	 * A memory optimization that works like Integer.valueOf(int), but with a
	 * guarantee that values 0 to NUM_INTS are cached. In contrast, the API
	 * for Integer.valueOf(int) only guarantees that values between -127 to 127
	 * are cached (as of Java 1.8).
	 *
	 * @param value
	 * @return
	 */
	public static Integer valueOf(int value) {
		if (value < NUM_INTS) {
			Integer i = ints[value];
			if (i != null) {
				return i;
			} else {
				return (ints[value] = new Integer(value));
			}
		} else {
			return new Integer(value);
		}
	}

	private final int defaultCount;
	private final int tableSize;
	private final Map<String,Integer> valueCount;
	private final String column;
	private final String view;
	private final String uniqueId;

	public FieldValueCounts(int mapSize, int defaultCount, int tableSize, String column, String view, String uniqueId) {
		if (mapSize > 1) {
			valueCount = new HashMap<>(mapSize);

		} else {
			valueCount = new HashMap<>();
		}
		this.defaultCount = defaultCount;
		this.tableSize = tableSize;
		this.column = column;
		this.view = view;
		this.uniqueId = uniqueId;
	}

	public FieldValueCounts(int mapSize, int defaultCount, int tableSize) {
		this(mapSize, defaultCount, tableSize, null, null, null);
	}

	@Override
	public void putValueCount(String value, Integer count) {
		if (value != null && count != null) {
			valueCount.put(value, count);
		}
	}

	@Override
	public void putAll(Map<String,Integer> m) {
		if (m != null) {
			for (Entry<String,Integer> entry : m.entrySet()) {
				String value = entry.getKey();
				Integer count = entry.getValue();
				if (value != null && count != null) {
					if (!(value instanceof String) || !(count instanceof Integer)) {
						String msg = "Invalid entry: " + value + "/" + count;
						throw new IllegalArgumentException(msg);
					}
					this.valueCount.put(value, count);
				}
			}
		}
	}

	@Override
	public int getValueCountSize() {
		assert valueCount != null;
		int retVal = valueCount.size();
		return retVal;
	}

	@Override
	public Integer getCountForValue(String value) {
		Integer retVal = null;
		if (value != null) {
			retVal = (Integer) valueCount.get(value);
		}
		return retVal;
	}

	@Override
	public int getDefaultCount() {
		return defaultCount;
	}

	@Override
	public int getTableSize() {
		return tableSize;
	}

	@Override
	public String getColumn() {
		return column;
	}

	@Override
	public String getView() {
		return view;
	}

	@Override
	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public String toString() {
		return "CountField [tableSize=" + tableSize + ", column=" + column
				+ ", view=" + view + ", uniqueId=" + uniqueId + "]";
	}

}
