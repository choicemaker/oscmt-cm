/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import java.io.Serializable;

/**
 * This represents a set of pairs to be compared.
 * 
 * @author pcheung
 *
 */
public interface IComparisonSet<T extends Comparable<T>> extends Serializable {

	/**
	 * This returns true if there are more pairs to compare in this set.
	 * 
	 * @return boolean
	 */
	public boolean hasNextPair();

	/**
	 * This gets the next pair of ids to be compared. It returns a Pair object.
	 * You should call hasNextPair before calling this method.
	 * 
	 * @return ComparisonPair
	 */
	public ComparisonPair<T> getNextPair();

	/**
	 * This method returns a string of all the elements in this comparison set
	 * for debugging purposes.
	 */
	public String writeDebug();

}
