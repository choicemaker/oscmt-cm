/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.ComparisonTreeNode;
import com.choicemaker.cm.oaba.core.IComparisonTreeSink;

/**
 * This is actually a sink of sinks that writes ComparisonTreeNodes to a bunch
 * of sinks in a round robin manner.
 * 
 * This is used by the parallel matcher code.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class ComparisonTreeGroupSink implements IComparisonTreeSink {

	// This is the array of round robin sinks.
	private IComparisonTreeSink[] sinks = null;

	// This is the round robin counter
	private int current = 0;

	// this counts the number of ComparisonTreeNodes written so far
	private int count = 0;

	/**
	 * This constructor takes two parameters: 1. A factory to create a group of
	 * sinks. 2. The number of sinks to create.
	 * 
	 * @param factory
	 * @param num
	 */
	public ComparisonTreeGroupSink(ComparisonTreeSinkSourceFactory factory,
			int num) throws BlockingException {

		sinks = new IComparisonTreeSink[num];
		for (int i = 0; i < num; i++) {
			sinks[i] = factory.getNextSink();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparisonTreeSink
	 * #
	 * writeComparisonTree(com.choicemaker.cm.oaba.core
	 * .ComparisonTreeNode)
	 */
	@Override
	public void writeComparisonTree(ComparisonTreeNode tree)
			throws BlockingException {
		sinks[current].writeComparisonTree(tree);
		count++;
		current++;
		if (current == sinks.length)
			current = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#exists()
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#open()
	 */
	@Override
	public void open() throws BlockingException {
		for (int i = 0; i < sinks.length; i++) {
			sinks[i].open();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#append()
	 */
	@Override
	public void append() throws BlockingException {
		for (int i = 0; i < sinks.length; i++) {
			sinks[i].append();
		}
	}

	@Override
	public boolean isOpen() {
		boolean retVal = sinks.length > 0;
		for (int i = 0; retVal && i < sinks.length; i++) {
			IComparisonTreeSink sink = sinks[i];
			retVal = sink != null && sink.isOpen();
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#close()
	 */
	@Override
	public void close() throws BlockingException {
		for (int i = 0; i < sinks.length; i++) {
			sinks[i].close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISink#getCount()
	 */
	@Override
	public int getCount() {
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISink#getInfo()
	 */
	@Override
	public String getInfo() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < sinks.length; i++) {
			sb.append((sinks[i].getInfo()));
			sb.append('|');
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISink#remove()
	 */
	@Override
	public void remove() throws BlockingException {
		for (int i = 0; i < sinks.length; i++) {
			sinks[i].remove();
		}
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
