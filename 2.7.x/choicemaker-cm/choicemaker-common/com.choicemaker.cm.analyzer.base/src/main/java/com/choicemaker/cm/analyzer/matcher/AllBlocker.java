/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.analyzer.matcher;

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
public class AllBlocker implements InMemoryBlocker {
	private List records;

	public void init(List records) {
		this.records = records;
	}

	public void clear() {
		this.records = null;
	}

	public RecordSource block(Record q) {
		return new RecordBinder(records);
	}

	public RecordSource block(Record q, int start) {
		return new RecordBinder(records, start);
	}
}
