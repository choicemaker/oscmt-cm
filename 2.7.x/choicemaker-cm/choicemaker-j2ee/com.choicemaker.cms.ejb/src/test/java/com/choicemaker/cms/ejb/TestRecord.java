/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.io.Serializable;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;

public class TestRecord<T extends Comparable<T> & Serializable>
		implements Record<T>, DataAccessObject<T> {

	private static final long serialVersionUID = 271L;

	private final T id;

	public TestRecord(T id) {
		// May be null
		this.id = id;
	}

	@Override
	public T getId() {
		return id;
	}

	@Override
	public void computeValidityAndDerived(DerivedSource src) {
	}

	@Override
	public void resetValidityAndDerived(DerivedSource src) {
	}

	@Override
	public void computeValidityAndDerived() {
	}

	@Override
	public DerivedSource getDerivedSource() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestRecord<?> other = (TestRecord<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestRecord[" + id + "]";
	}

}
