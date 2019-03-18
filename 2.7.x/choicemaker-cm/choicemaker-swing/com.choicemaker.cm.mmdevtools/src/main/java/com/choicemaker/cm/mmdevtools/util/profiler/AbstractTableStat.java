/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools.util.profiler;

import java.util.Set;

import com.choicemaker.cm.core.Record;

/**
 * @author Owner
 *
 */
public abstract class AbstractTableStat implements FieldProfiler {

	@Override
	public abstract void reset();
	@Override
	public abstract void processRecord(Record r);
	
	public abstract String getName();
	public abstract Object[] getColumnHeaders();
	public abstract Object[][] getData();
	public abstract boolean filterRecord(Set values, Record r);
		
	@Override
	public final int getScalarStatCount() {
		return 0;
	}
	
	@Override
	public final String getScalarStatName(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final Object getScalarStatValue(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean filterRecordForScalarStat(int index, Record r) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final int getTabularStatCount() {
		return 1;
	}

	@Override
	public final String getTabularStatName(int index) {
		if (index != 0) {
			throw new IllegalArgumentException("Index: " + index);
		} else {
			return getName();
		}
	}

	@Override
	public final Object[] getTabularStatColumnHeaders(int index) {
		if (index != 0) {
			throw new IllegalArgumentException("Index: " + index);
		} else {
			return getColumnHeaders();
		}
	}

	@Override
	public final Object[][] getTabularStatTableData(int index) {
		if (index != 0) {
			throw new IllegalArgumentException("Index: " + index);
		} else {
			return getData();
		}
	}
	
	@Override
	public final boolean filterRecordForTableStat(int index, Set values, Record r) {
		if (index != 0) {
			throw new IllegalArgumentException("Index: " + index);
		} else {
			return filterRecord(values, r);
		}
	}

}
