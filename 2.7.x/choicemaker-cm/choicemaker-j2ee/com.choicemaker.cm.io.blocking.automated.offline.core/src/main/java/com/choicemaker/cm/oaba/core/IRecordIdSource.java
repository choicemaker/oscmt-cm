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
 * This object is a source that get record ids. The records id can be Integer,
 * Long, or String.
 * 
 * @author pcheung
 *
 */
public interface IRecordIdSource<T extends Comparable<T>> extends ISource<T> {

	/**
	 * This returns the object type of the record ID. See IRecordIdSink.
	 * 
	 * @return int - Object type of record ID
	 */
	public RECORD_ID_TYPE getRecordIDType() throws BlockingException;

}
