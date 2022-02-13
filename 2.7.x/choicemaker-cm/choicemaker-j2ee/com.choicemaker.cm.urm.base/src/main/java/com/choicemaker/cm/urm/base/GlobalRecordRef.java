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
 * A record in a referenced record collection (database, file) that represented by its ID.
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class GlobalRecordRef<T extends Comparable<T> & Serializable> implements ISingleRecord<T> {
	
	private static final long serialVersionUID = -612416872534337942L;

	private T id;
	private RefRecordCollection recCollRef;
		
	public GlobalRecordRef(T id, RefRecordCollection recColl) {
		super();
		this.id = id;
		this.recCollRef = recColl;
	}
	/**
	 * <code>getId</code>
	 * <p> 
	 * 
	 * @return
	 */
	@Override
	public T getId() {
		return id;
	}

	/**
	 * <code>getRecCollRef</code>
	 * <p> 
	 * 
	 * @return
	 */
	public RefRecordCollection getRecCollRef() {
		return recCollRef;
	}

	/**
	 * <code>setId</code>
	 * <p> 
	 * 
	 * @param comparable
	 */
	public void setId(T comparable) {
		id = comparable;
	}

	/**
	 * <code>setRecCollRef</code>
	 * <p> 
	 * 
	 * @param collection
	 */
	public void setRecCollRef(RefRecordCollection collection) {
		recCollRef = collection;
	}

	@Override
	public void accept(IRecordVisitor ext){
		ext.visit(this);
	}	
}
