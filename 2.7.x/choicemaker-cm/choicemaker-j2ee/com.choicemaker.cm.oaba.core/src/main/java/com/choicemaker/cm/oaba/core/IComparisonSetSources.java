/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;

/**
 * This is a collection of IComparisonSetSources.
 * 
 * @author pcheung
 *
 */
public interface IComparisonSetSources<T extends Comparable<T>> {

	/**
	 * This returns the next comparison set source.
	 * 
	 * @return IComparisonSetSource
	 */
	public IComparisonSetSource<T> getNextSource();

	/**
	 * This returns true if this collection has more sources.
	 * 
	 * @return boolean
	 */
	public boolean hasNextSource() throws BlockingException;

	/**
	 * This method cleans up and frees up resources.
	 * 
	 *
	 */
	public void cleanUp() throws BlockingException;

}
