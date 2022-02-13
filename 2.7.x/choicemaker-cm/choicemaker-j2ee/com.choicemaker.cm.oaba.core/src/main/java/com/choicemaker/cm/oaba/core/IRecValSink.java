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
 * This interface handles writting out record_id, value_id pairs.
 * 
 * @author pcheung
 *
 */
public interface IRecValSink extends ISink {

	/** Writes a block to the sink. */
	public void writeRecordValue(long recID, IntArrayList values)
			throws BlockingException;

}
