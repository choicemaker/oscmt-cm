/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.filter;

import java.io.Serializable;

import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecord2;

/**
 * Checks if a MatchRecord2 pair satisfies a filter constraint
 * 
 * @author rphall
 * @version $Revision: 1.1 $ $Date: 2010/03/28 15:45:19 $
 */
public interface IMatchRecord2Filter<T extends Comparable<T>> extends
		Serializable {

	/**
	 * Checks if a pair satisfies a filter constraint
	 */
	boolean satisfy(MatchRecord2<T> pair);

}
