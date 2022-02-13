/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.base.MatchRecord2;

/**
 * This is a source that reads MatchRecord2.
 * 
 * @author pcheung
 *
 */
public interface IMatchRecord2Source<T extends Comparable<T>> extends
		ISource<MatchRecord2<T>> {

	/** Returns the number of MatchRecords read so far. */
	public int getCount();

}
