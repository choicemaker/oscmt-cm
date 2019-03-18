/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.exact.base;

import java.util.HashSet;
import java.util.List;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.RecordBinder;
import com.choicemaker.cm.core.blocking.InMemoryBlocker;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class CompositeExactInMemoryBlocker implements InMemoryBlocker {
	private ExactInMemoryBlocker[] constituents;
	private PositionMap positionMap;
	
	public CompositeExactInMemoryBlocker(ExactInMemoryBlocker[] constituents, PositionMap positionMap) {
		this.constituents = constituents;
		this.positionMap = positionMap;
	}

	@Override
	public void init(List records) {
		positionMap.setRecords(records);
		for (int i = 0; i < constituents.length; i++) {
			constituents[i].init(records);
		}
	}

	@Override
	public void clear() {
		positionMap.clear();
		for (int i = 0; i < constituents.length; i++) {
			constituents[i].clear();
		}
	}

	@Override
	public RecordSource block(Record q) {
		HashSet res = new HashSet();
		for (int i = 0; i < constituents.length; i++) {
			constituents[i].block(q, res);
		}
		return new RecordBinder(res);
	}

	@Override
	public RecordSource block(Record q, int start) {
		HashSet res = new HashSet();
		for (int i = 0; i < constituents.length; i++) {
			constituents[i].block(q, res, start);
		}
		return new RecordBinder(res);
	}

}
