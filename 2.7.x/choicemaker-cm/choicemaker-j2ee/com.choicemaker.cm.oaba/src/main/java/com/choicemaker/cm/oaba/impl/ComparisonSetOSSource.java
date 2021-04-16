/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.ComparisonArrayOS;
import com.choicemaker.cm.oaba.core.IComparisonArraySource;
import com.choicemaker.cm.oaba.core.IComparisonSet;
import com.choicemaker.cm.oaba.core.IComparisonSetSource;

/**
 * This is a source for an oversized comparison set.
 * 
 * @author pcheung
 *
 */
public class ComparisonSetOSSource<T extends Comparable<T>> implements
		IComparisonSetSource<T> {

	private IComparisonArraySource<T> source;
	private int maxBlockSize;

	public ComparisonSetOSSource(IComparisonArraySource<T> source,
			int maxBlockSize) {
		this.source = source;
		this.maxBlockSize = maxBlockSize;
	}

	@Override
	public IComparisonSet<T> next() throws BlockingException {
		return new ComparisonArrayOS<T>(source.next(), maxBlockSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#exists()
	 */
	@Override
	public boolean exists() {
		return source.exists();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISource#open()
	 */
	@Override
	public void open() throws BlockingException {
		source.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#hasNext()
	 */
	@Override
	public boolean hasNext() throws BlockingException {
		return source.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#close()
	 */
	@Override
	public void close() throws BlockingException {
		source.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#getInfo()
	 */
	@Override
	public String getInfo() {
		return source.getInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#remove()
	 */
	@Override
	public void delete() throws BlockingException {
		source.delete();
	}

}
