/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.io.Serializable;
import java.util.List;

/**
 * A group of records combined together by some property. Usually this property
 * is a fact or an assumption that the records represent the same physical
 * entity. (e.g., person, company, etc.)
 *
 * @author emoussikaev
 */
public abstract class CompositeRecord<T extends Comparable<T> & Serializable>
		implements IRecord<T> {

	private static final long serialVersionUID = 271;
	private IRecord<T>[] records;
	private T id;

	public CompositeRecord(T id, IRecord<T>[] records) {
		this.id = id;
		this.records = records;
	}

	@SuppressWarnings("unchecked")
	public CompositeRecord(T id, List<? extends IRecord<T>> recordList) {
		this.id = id;
		this.records = (IRecord<T>[]) (recordList == null ? new IRecord<?>[0]
				: recordList.toArray(new IRecord<?>[recordList.size()]));
	}

	@Override
	public T getId() {
		return id;
	}

	public void setId(T comparable) {
		id = comparable;
	}

	public IRecord<T>[] getRecords() {
		return records;
	}

	public void setRecords(IRecord<T>[] records) {
		this.records = records;
	}

}
