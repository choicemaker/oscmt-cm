/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

/**
 * This interface handles the reading of ComparisonGroups.
 * 
 * @author pcheung
 *
 */
public interface IComparisonTreeSource<T extends Comparable<T>> extends
		ISource<ComparisonTreeNode<T>> {

	/** Returns the number of ComparisonTree read so far. */
	public int getCount();

}
