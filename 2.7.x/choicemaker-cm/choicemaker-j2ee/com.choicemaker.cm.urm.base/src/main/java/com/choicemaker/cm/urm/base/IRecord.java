/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.io.Serializable;

/**
 * A record
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public interface IRecord extends Serializable{
	
	/**
	 * Returns record identifier
	 * <p> 
	 * 
	 * @return record identifier
	 */
	public java.lang.Comparable getId();
	
	/**
	 * Applies visitor to a record.
	 * <p> 
	 * 
	 */
	public void accept(IRecordVisitor ext);
		
}
