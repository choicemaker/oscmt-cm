/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.ComparisonTreeSet;
import com.choicemaker.cm.oaba.core.IComparisonSet;
import com.choicemaker.cm.oaba.core.IComparisonSetSource;
import com.choicemaker.cm.oaba.core.IComparisonTreeSource;

/**
 * @author pcheung
 *
 */
public class ComparisonTreeSetSource<T extends Comparable<T>> implements
		IComparisonSetSource<T> {

	private IComparisonTreeSource<T> source;

	public ComparisonTreeSetSource(IComparisonTreeSource<T> source) {
		this.source = source;
	}

	@Override
	public IComparisonSet<T> next() throws BlockingException {
		return new ComparisonTreeSet<T>(source.next());
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
