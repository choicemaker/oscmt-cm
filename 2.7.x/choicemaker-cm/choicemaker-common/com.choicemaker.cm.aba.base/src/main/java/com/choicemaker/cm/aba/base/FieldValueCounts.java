/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.choicemaker.cm.aba.IFieldValueCounts;

/**
 *
 * @author mbuechi (CM 2.3)
 * @author rphall (CM 2.7 revision)
 */
public class FieldValueCounts implements Serializable, IFieldValueCounts {

	private static final long serialVersionUID = 271;

	private static final int NUM_INTS = 1000;
	private static final Integer[] ints = new Integer[NUM_INTS];

	/**
	 * A memory optimization that works like Integer.valueOf(int), but with a
	 * guarantee that values 0 to NUM_INTS are cached. In contrast, the API for
	 * Integer.valueOf(int) only guarantees that values between -127 to 127 are
	 * cached (as of Java 1.8).
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

	private int defaultCount;
	private int tableSize;
	private Map<String, Integer> valueCountMap;
	private String column;
	private String view;
	private String uniqueId;

	@Override
	public Map<String, Integer> getValueCountMap() {
		return Collections.unmodifiableMap(valueCountMap);
	}

	public void setValueCountMap(Map<String, Integer> valueCountMap) {
		this.valueCountMap = valueCountMap;
	}

	public void setDefaultCount(int defaultCount) {
		this.defaultCount = defaultCount;
	}

	public void setTableSize(int tableSize) {
		this.tableSize = tableSize;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setView(String view) {
		this.view = view;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public FieldValueCounts() {
		this.valueCountMap = new HashMap<>();
		this.defaultCount = 0;
		this.tableSize = 0;
		this.column = null;
		this.view = null;
		this.uniqueId = null;
	}

	public FieldValueCounts(IFieldValueCounts ifvc) {
		Map<String, Integer> vcMap = ifvc.getValueCountMap();
		this.valueCountMap = new HashMap<>(vcMap.size());
		this.valueCountMap.putAll(vcMap);
		this.defaultCount = ifvc.getDefaultCount();
		this.tableSize = ifvc.getTableSize();
		this.column = ifvc.getColumn();
		this.view = ifvc.getView();
		this.uniqueId = ifvc.getUniqueId();
	}

	public FieldValueCounts(int mapSize, int defaultCount, int tableSize,
			String column, String view, String uniqueId) {
		if (mapSize > 1) {
			valueCountMap = new HashMap<>(mapSize);

		} else {
			valueCountMap = new HashMap<>();
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
			valueCountMap.put(value, count);
		}
	}

	@Override
	public void putAll(Map<String, Integer> m) {
		if (m != null) {
			for (Entry<String, Integer> entry : m.entrySet()) {
				String value = entry.getKey();
				Integer count = entry.getValue();
				if (value != null && count != null) {
					if (!(value instanceof String)
							|| !(count instanceof Integer)) {
						String msg = "Invalid entry: " + value + "/" + count;
						throw new IllegalArgumentException(msg);
					}
					this.valueCountMap.put(value, count);
				}
			}
		}
	}

	@Override
	public int getValueCountSize() {
		assert valueCountMap != null;
		int retVal = valueCountMap.size();
		return retVal;
	}

	@Override
	public Integer getCountForValue(String value) {
		Integer retVal = null;
		if (value != null) {
			retVal = valueCountMap.get(value);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + defaultCount;
		result = prime * result + tableSize;
		result =
			prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
		result = prime * result
				+ ((valueCountMap == null) ? 0 : valueCountMap.hashCode());
		result = prime * result + ((view == null) ? 0 : view.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldValueCounts other = (FieldValueCounts) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (defaultCount != other.defaultCount)
			return false;
		if (tableSize != other.tableSize)
			return false;
		if (uniqueId == null) {
			if (other.uniqueId != null)
				return false;
		} else if (!uniqueId.equals(other.uniqueId))
			return false;
		if (valueCountMap == null) {
			if (other.valueCountMap != null)
				return false;
		} else if (!valueCountMap.equals(other.valueCountMap))
			return false;
		if (view == null) {
			if (other.view != null)
				return false;
		} else if (!view.equals(other.view))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FieldValueCounts [view=" + view + ", field=" + column + "]";
	}

}
