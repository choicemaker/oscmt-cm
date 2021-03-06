/*******************************************************************************
 * Copyright (c) 2015, 2017 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import com.choicemaker.cm.core.base.MatchRecord2;

/**
 * This interface defines what edge properties to check for.
 * 
 * @author pcheung
 *
 * ChoiceMaker Technologies, Inc.
 */
@SuppressWarnings({"rawtypes" })
public interface EdgeProperty {

	/** This returns true if the match pair has the desired property.
	 * 
	 * @param mr
	 * @return boolean
	 */
	public boolean hasProperty (MatchRecord2 mr);

}
