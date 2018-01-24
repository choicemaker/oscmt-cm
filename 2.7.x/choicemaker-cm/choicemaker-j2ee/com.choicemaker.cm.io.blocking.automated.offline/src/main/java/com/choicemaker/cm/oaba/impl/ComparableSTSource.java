/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.util.List;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IComparableSource;
import com.choicemaker.cm.oaba.core.ISuffixTreeSource;
import com.choicemaker.cm.oaba.core.PairID;
import com.choicemaker.cm.oaba.core.SuffixTreeNode;
import com.choicemaker.cm.oaba.utils.SuffixTreeUtils;

/**
 * This is a wrapper object on SuffixTreeSource to make it look like
 * IComparableSource. It reads a suffix tree and build an array of PairID.
 * 
 * @author pcheung
 *
 */
public class ComparableSTSource implements IComparableSource<PairID> {

	private ISuffixTreeSource source;
	private int count = 0;
	private List<PairID> pairs = null;
	private int ind = 0;

	public ComparableSTSource(ISuffixTreeSource source) {
		this.source = source;
	}

	@Override
	public PairID next() {
		PairID p = pairs.get(ind);
		ind++;
		return p;
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
		return count;
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
		boolean ret = false;

		if (pairs == null) {
			if (source.hasNext()) {
				SuffixTreeNode root = source.next();
				pairs = SuffixTreeUtils.getPairs(root);
				ret = true;
				ind = 0;
			}
		} else {
			if (ind < pairs.size())
				ret = true;
			else {
				// get the next tree
				if (source.hasNext()) {
					SuffixTreeNode root = source.next();
					pairs = SuffixTreeUtils.getPairs(root);
					ret = true;
					ind = 0;
				}
			}
		}

		return ret;
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
