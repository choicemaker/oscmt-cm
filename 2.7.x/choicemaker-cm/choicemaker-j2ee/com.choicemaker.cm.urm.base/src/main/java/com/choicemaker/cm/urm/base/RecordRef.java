/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.io.Serializable;

/**
 * A record represented by an identifier. It is assumed that the real location
 * of the record (database, file) is known from the context.
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class RecordRef<T extends Comparable<T> & Serializable> implements ISingleRecord<T> {

	private static final long serialVersionUID = 271;

	protected T id;
	
	/**
	 * Constructs a <code>RecordRef</code> with unknown (null) identifier.
	 */
	public RecordRef() {
		super();
	}

	/**
	 * Constructs a <code>RecordRef</code> with specified identifier.
	 * <p> 
	 * 
	 * @param id
	 */
	public RecordRef(T id) {
		this.id = id;
	}

	/**
	 * Returns record identifier. 
	 * <p>
	 * 
	 * @return
	 */
	@Override
	public T getId() {
		return id;
	}

	/**
	 * Sets record identifier.
	 * <p> 
	 * 
	 * @param id
	 */
	public void setId(T id) {
		this.id = id;
	}

	@Override
	public void accept(IRecordVisitor ext){
		ext.visit(this);
	}
}
