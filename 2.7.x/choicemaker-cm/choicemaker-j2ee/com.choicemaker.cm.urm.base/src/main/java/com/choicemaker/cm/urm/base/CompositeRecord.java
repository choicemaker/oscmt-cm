/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

/**
 * A group of records combined together by some property. Usually this property is a fact or an assumption
 * that the records represent the same physical entity. (e.g., person, company, etc.)
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public abstract class CompositeRecord implements IRecord {

	private static final long serialVersionUID = 2046401598800163936L;
	private IRecord[]			records;
	private Comparable		   id;


	/**
	 * Constructs a <code>CompositeRecord</code>
	 * <p>
	 *
	 *
	 */
	public CompositeRecord(Comparable id, IRecord[]	records) {
		this.id  = id;
		this.records = records;
	}


	public Comparable getId() {
		return id;
	}

	/**
	 * <code>setId</code>
	 * <p>
	 *
	 * @param comparable
	 */
	public void setId(Comparable comparable) {
		id = comparable;
	}

	/**
	 * @return
	 */
	public IRecord[] getRecords() {
		return records;
	}

	/**
	 * @param records
	 */
	public void setRecords(IRecord[] records) {
		this.records = records;
	}

}
