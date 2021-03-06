/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.util.IntArrayList;

/**
 * This is a source from which we read Rec_id, val_id pairs.
 * 
 * @author pcheung
 *
 */
public interface IRecValSource extends ISource<Long> {

	/** Gets the next RecordID. */
	public long getNextRecID() throws BlockingException;

	/**
	 * Gets the next stacked values corresponding to the recID. Always call
	 * getNextRecID and getNextValues in conjunction.
	 */
	public IntArrayList getNextValues() throws BlockingException;

	/** True is this source exists and not null. */
	@Override
	public boolean exists();

}
