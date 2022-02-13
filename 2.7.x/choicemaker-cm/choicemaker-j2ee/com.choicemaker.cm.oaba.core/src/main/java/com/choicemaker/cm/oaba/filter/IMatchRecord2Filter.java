/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.filter;

import java.io.Serializable;

import com.choicemaker.cm.core.base.MatchRecord2;

/**
 * Checks if a MatchRecord2 pair satisfies a filter constraint
 * 
 * @author rphall
 */
public interface IMatchRecord2Filter<T extends Comparable<T>> extends
		Serializable {

	/**
	 * Checks if a pair satisfies a filter constraint
	 */
	boolean satisfy(MatchRecord2<T> pair);

}
