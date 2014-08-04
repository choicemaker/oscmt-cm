/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.offline.core;

import com.choicemaker.cm.core.BlockingException;

/**
 * This object is a source that get record ids.  The records id can be Integer, Long, or String.
 * 
 * @author pcheung
 *
 */
public interface IRecordIDSource extends ISource {
	
	/** This method returns the next record ID.
	 * 
	 * @return Object - next record ID.
	 */
	public Comparable getNextID () throws BlockingException;
	
	
	/** This returns the object type of the record ID.  See IRecordIDSink.
	 * 
	 * @return int - Object type of record ID
	 */
	public int getRecordIDType() throws BlockingException;

}