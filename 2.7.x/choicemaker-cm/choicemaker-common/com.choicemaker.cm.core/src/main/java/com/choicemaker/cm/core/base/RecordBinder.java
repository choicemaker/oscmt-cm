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
package com.choicemaker.cm.core.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class RecordBinder implements RecordSource {
	
	public static List getList(RecordSource rs) throws IOException {
		rs.open();
		List list = new ArrayList();
		while (rs.hasNext()) {
			list.add(rs.getNext());
		}
		rs.close();
		
		return list;
	}

	public static void store(Collection l, RecordSink snk) throws Exception {
		snk.open();
		Iterator i = l.iterator();
		while (i.hasNext()) {
			snk.put((Record) i.next());
		}
		snk.close();
	}
	
	private Collection collection;
	private Iterator iterator;
	private String name;
	private ImmutableProbabilityModel probabilityModel;
	private int startPosition;
	
	public RecordBinder(Collection collection) {
		this.collection = collection;
	}
	
	public RecordBinder(List collection, int startPosition) {
		this.collection = collection;
		this.startPosition = startPosition;
	}

	@Override
	public Record getNext() throws IOException {
		return (Record)iterator.next();
	}

	@Override
	public void open() throws IOException {
		if(startPosition == 0) {
			iterator = collection.iterator();
		} else {
			iterator = ((List)collection).listIterator(startPosition);
		}
	}

	@Override
	public void close() throws IOException {
		iterator = null;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return probabilityModel;
	}

	@Override
	public void setModel(ImmutableProbabilityModel probabilityModel) {
		this.probabilityModel = probabilityModel;
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		return null;
	}

	@Override
	public String getFileName() {
		return null;
	}

	/** NOP for now */
	public void flush() {
	}

}
