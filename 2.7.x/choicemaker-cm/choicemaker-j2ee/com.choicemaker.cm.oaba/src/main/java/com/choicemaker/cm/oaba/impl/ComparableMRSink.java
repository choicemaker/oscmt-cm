/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.util.Iterator;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.oaba.core.IComparableSink;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;

/**
 * This wrapper object takes a MatchRecord2Sink and makes it look like
 * IComparableSink.
 * 
 * @author pcheung
 *
 */
public class ComparableMRSink<T extends Comparable<T>> implements
		IComparableSink<MatchRecord2<T>> {

	private IMatchRecord2Sink<T> sink;

	public ComparableMRSink(IMatchRecord2Sink<T> sink) {
		this.sink = sink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparableSink
	 * #writeComparables(java.util.Iterator)
	 */
	@Override
	public void writeComparables(Iterator<MatchRecord2<T>> it)
			throws BlockingException {
		sink.writeMatches(it);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparableSink
	 * #writeComparable(java.lang.Comparable)
	 */
	@Override
	public void writeComparable(MatchRecord2<T> C) throws BlockingException {
		sink.writeMatch(C);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#exists()
	 */
	@Override
	public boolean exists() {
		return sink.exists();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#open()
	 */
	@Override
	public void open() throws BlockingException {
		sink.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#append()
	 */
	@Override
	public void append() throws BlockingException {
		sink.append();
	}

	@Override
	public boolean isOpen() {
		return sink.isOpen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#close()
	 */
	@Override
	public void close() throws BlockingException {
		sink.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISink#getCount()
	 */
	@Override
	public int getCount() {
		return sink.getCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISink#getInfo()
	 */
	@Override
	public String getInfo() {
		return sink.getInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#remove()
	 */
	@Override
	public void remove() throws BlockingException {
		sink.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparableSink
	 * #getBaseObject()
	 */
	@Override
	public Object getBaseObject() {
		return sink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#flush()
	 */
	@Override
	public void flush() throws BlockingException {
	}

}
