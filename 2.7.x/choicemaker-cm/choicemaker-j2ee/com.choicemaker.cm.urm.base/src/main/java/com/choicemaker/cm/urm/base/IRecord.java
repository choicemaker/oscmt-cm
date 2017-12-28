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
 * See <code>com.choicemaker.cm.core.Record</code>
 *
 * @author emoussikaev
 */
public interface IRecord<T extends Comparable<T>> extends Serializable {
	
	/**
	 * Returns a key that uniquely identifies an entity. If two records have
	 * different identifiers, then they represent different entities (in the
	 * absence of duplicates).
	 * <p>
	 * See <code>com.choicemaker.cm.core.Identifiable</code>
	 */
	public T getId();
	
	/**
	 * Applies visitor to a record.
	 * <p> 
	 * 
	 */
	public void accept(IRecordVisitor ext);
		
}
