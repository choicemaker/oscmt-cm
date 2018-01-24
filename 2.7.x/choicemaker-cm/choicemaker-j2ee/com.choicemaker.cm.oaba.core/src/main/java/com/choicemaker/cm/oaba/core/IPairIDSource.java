/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

/**
 * @author pcheung
 *
 */
public interface IPairIDSource extends ISource<PairID> {

	/** Returns the number of PairID read so far. */
	public int getCount();

}
