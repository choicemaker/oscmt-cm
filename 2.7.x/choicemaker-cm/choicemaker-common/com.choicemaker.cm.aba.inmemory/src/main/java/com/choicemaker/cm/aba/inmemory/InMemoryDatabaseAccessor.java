/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.inmemory;

import java.io.IOException;
import java.util.Iterator;

import javax.sql.DataSource;

import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.core.Record;

/**
 * @author ajwinkel
 *
 */
public class InMemoryDatabaseAccessor implements DatabaseAccessor {

	protected InMemoryDataSource imds;
	protected int start;

	protected Iterator itBlocked;
	
	public InMemoryDatabaseAccessor(InMemoryDataSource imds, int start) {
		this.imds = imds;
		this.start = start;
	}

	@Override
	public DatabaseAccessor cloneWithNewConnection()
		throws CloneNotSupportedException {
		throw new CloneNotSupportedException("not yet implemented");
	}

	@Override
	public void setDataSource(DataSource dataSource) { 
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCondition(Object condition) { 
		throw new UnsupportedOperationException();
	}

	@Override
	public void open(AutomatedBlocker blocker, String unused) throws IOException {
		itBlocked = imds.select(blocker.getBlockingSets(), start);
	}

	@Override
	public boolean hasNext() {
		return itBlocked.hasNext();
	}

	@Override
	public Record getNext() throws IOException {
		return (Record)itBlocked.next();
	}

	@Override
	public void close() throws IOException {
		itBlocked = null;
	}

}
