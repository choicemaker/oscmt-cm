/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.io.blocking.automated.offline.core.IIDSet;
import com.choicemaker.cm.io.blocking.automated.offline.core.IIDSetSource;
import com.choicemaker.cm.io.blocking.automated.offline.core.ISuffixTreeSource;

/**
 * @author pcheung
 *
 */
public class IDTreeSetSource implements IIDSetSource {

	private ISuffixTreeSource bSource;

	// private IIDSet next;

	public IDTreeSetSource(ISuffixTreeSource bSource) {
		this.bSource = bSource;
	}

	@Override
	public IIDSet next() throws BlockingException {
		return bSource.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.ISource#exists()
	 */
	@Override
	public boolean exists() {
		return bSource.exists();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISource#open()
	 */
	@Override
	public void open() throws BlockingException {
		bSource.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.ISource#hasNext()
	 */
	@Override
	public boolean hasNext() throws BlockingException {
		return bSource.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.ISource#close()
	 */
	@Override
	public void close() throws BlockingException {
		bSource.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.ISource#getInfo()
	 */
	@Override
	public String getInfo() {
		return bSource.getInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.io.blocking.automated.offline.core.ISource#remove()
	 */
	@Override
	public void delete() throws BlockingException {
		bSource.delete();
	}

}
