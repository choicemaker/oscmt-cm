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
import com.choicemaker.cm.oaba.core.IComparisonSetSource;
import com.choicemaker.cm.oaba.core.IComparisonSetSources;
import com.choicemaker.cm.oaba.core.IComparisonTreeSource;

/**
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class ComparisonTreeSetSources implements IComparisonSetSources {

	private ComparisonTreeSinkSourceFactory sFactory;
	private IComparisonTreeSource next;
	private ArrayList sources = new ArrayList();

	public ComparisonTreeSetSources(ComparisonTreeSinkSourceFactory sFactory) {
		this.sFactory = sFactory;
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
		return new ComparisonTreeSetSource(next);
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
			next = (IComparisonTreeSource) sources.get(i);
			next.delete();
		}
	}

}
