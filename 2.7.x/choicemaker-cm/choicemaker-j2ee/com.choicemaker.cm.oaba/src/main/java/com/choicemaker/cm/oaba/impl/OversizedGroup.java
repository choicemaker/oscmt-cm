/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.util.ArrayList;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.BlockSet;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IBlockSource;
import com.choicemaker.cm.oaba.core.IOversizedGroup;
import com.choicemaker.util.IntArrayList;

/**
 * @author pcheung
 *
 *         This object hides the detail list of oversized sinks from rest of the
 *         program and treats them as one. It breaks the oversized blocks into
 *         files with the same max column number.
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class OversizedGroup implements IOversizedGroup {

	int numColumn;
	IBlockSinkSourceFactory bFactory;
	ArrayList sinks;

	public OversizedGroup(int numColumn, IBlockSinkSourceFactory bFactory)
			throws BlockingException {
		this.numColumn = numColumn;
		this.bFactory = bFactory;

		sinks = new ArrayList(numColumn);

		for (int i = 0; i < numColumn; i++) {
			IBlockSink sink = bFactory.getNextSink();
			sinks.add(sink);
		}
	}

	/**
	 * This method finds the maximum column id of the block set.
	 */
	private int findMax(BlockSet bs) {
		IntArrayList columns = bs.getColumns();
		// 2014-04-24 rphall: Commented out unused local variable.
		// int s = columns.size();

		// columns are sorted so just return the last one.
		return columns.get(columns.size() - 1);
	}

	@Override
	public void writeBlock(BlockSet bs) throws BlockingException {
		IBlockSink sink = (IBlockSink) sinks.get(findMax(bs));
		sink.writeBlock(bs);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IOversizedGroup
	 * #IBlockSource(int)
	 */
	@Override
	public IBlockSource getSource(int maxColumn) throws BlockingException {
		return bFactory.getSource((IBlockSink) sinks.get(maxColumn));
	}

	/*
	 * Initializes and opens all the sinks
	 */
	@Override
	public void openAllSinks() throws BlockingException {
		for (int i = 0; i < numColumn; i++) {
			IBlockSink sink = (IBlockSink) sinks.get(i);
			sink.open();
			// System.out.println ("Open " + sink.getInfo());
		}

	}

	/*
	 * Initializes and opens all the sinks for append
	 */
	@Override
	public void appendAllSinks() throws BlockingException {
		for (int i = 0; i < numColumn; i++) {
			IBlockSink sink = (IBlockSink) sinks.get(i);
			sink.append();
			// System.out.println ("Append " + sink.getInfo());
		}

	}

	/*
	 * closes all the sinks
	 */
	@Override
	public void closeAllSinks() throws BlockingException {
		for (int i = 0; i < numColumn; i++) {
			IBlockSink sink = (IBlockSink) sinks.get(i);
			sink.close();
		}
	}

	@Override
	public void cleanUp() throws BlockingException {
		for (int i = 0; i < numColumn; i++) {
			IBlockSink sink = (IBlockSink) sinks.get(i);
			bFactory.removeSink(sink);

			// System.out.println ("Remove " + sink.getInfo());
		}

	}

}
