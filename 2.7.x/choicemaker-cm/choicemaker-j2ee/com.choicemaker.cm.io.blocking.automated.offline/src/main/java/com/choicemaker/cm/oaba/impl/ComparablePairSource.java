/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IComparableSource;
import com.choicemaker.cm.oaba.core.IPairIDSource;
import com.choicemaker.cm.oaba.core.PairID;

/**
 * This wrapper object takes IPairIDSource and makes it look like a
 * IComparableSource.
 * 
 * @author pcheung
 *
 */
public class ComparablePairSource implements IComparableSource<PairID> {

	private IPairIDSource source;

	public ComparablePairSource(IPairIDSource source) {
		this.source = source;
	}

	@Override
	public PairID next() throws BlockingException {
		return source.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparableSource
	 * #getCount()
	 */
	@Override
	public int getCount() {
		return source.getCount();
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
