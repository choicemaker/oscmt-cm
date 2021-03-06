/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
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
public abstract class AbstractScalarStatGroup implements FieldProfiler {

	@Override
	public abstract void reset();
	@Override
	public abstract void processRecord(Record r);

	@Override
	public abstract int getScalarStatCount();
	@Override
	public abstract String getScalarStatName(int index);
	@Override
	public abstract Object getScalarStatValue(int index);
	@Override
	public abstract boolean filterRecordForScalarStat(int index, Record r);

	@Override
	public int getTabularStatCount() {
		return 0;
	}

	@Override
	public String getTabularStatName(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] getTabularStatColumnHeaders(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[][] getTabularStatTableData(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean filterRecordForTableStat(int statIndex, Set values, Record r) {
		throw new UnsupportedOperationException();
	}

}
