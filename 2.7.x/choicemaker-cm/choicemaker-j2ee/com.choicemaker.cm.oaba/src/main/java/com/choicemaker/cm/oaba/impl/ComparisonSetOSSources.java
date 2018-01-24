/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.util.ArrayList;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IComparisonArraySource;
import com.choicemaker.cm.oaba.core.IComparisonSetSource;
import com.choicemaker.cm.oaba.core.IComparisonSetSources;

/**
 * This is a set of sources for oversized comparison sets.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class ComparisonSetOSSources implements IComparisonSetSources {
	private ComparisonArraySinkSourceFactory sFactory;
	private IComparisonArraySource next;
	private ArrayList sources = new ArrayList();
	private int maxBlockSize;

	public ComparisonSetOSSources(ComparisonArraySinkSourceFactory sFactory,
			int maxBlockSize) {
		this.sFactory = sFactory;
		this.maxBlockSize = maxBlockSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparisonSetSources
	 * #getNextSource()
	 */
	@Override
	public IComparisonSetSource getNextSource() {
		return new ComparisonSetOSSource(next, maxBlockSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparisonSetSources
	 * #cleanUp()
	 */
	@Override
	public void cleanUp() throws BlockingException {
		for (int i = 0; i < sources.size(); i++) {
			next = (IComparisonArraySource) sources.get(i);
			next.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IComparisonSetSources
	 * #hasNextSource()
	 */
	@Override
	public boolean hasNextSource() throws BlockingException {
		next = sFactory.getNextSource();
		sources.add(next);
		return next.exists();
	}

}
