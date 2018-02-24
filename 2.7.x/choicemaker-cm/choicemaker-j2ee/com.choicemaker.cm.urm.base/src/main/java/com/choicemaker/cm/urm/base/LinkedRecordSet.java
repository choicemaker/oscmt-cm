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
 * A group of matching together and denoting the same physical entity.
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public class LinkedRecordSet<T extends Comparable<T> & Serializable>
		extends CompositeRecord<T> {

	private static final long serialVersionUID = -8988092145857498700L;

	private LinkCriteria criteria;

	public LinkedRecordSet(T id, IRecord<T>[] r, LinkCriteria c) {
		super(id,r);
		this.criteria = c;
	}


	public LinkCriteria getCriteria() {
		return criteria;
	}

	public void accept(IRecordVisitor ext){
		ext.visit(this);
	}

	/**
	 * @param criteria
	 */
	public void setCriteria(LinkCriteria criteria) {
		this.criteria = criteria;
	}

}
