/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.webapp.util;

import com.choicemaker.util.Precondition;

public final class NameValueComparison {

	private final String name;
	private final String value;
	private final String comparison;
	private final boolean equal;

	public NameValueComparison(String pn, String pv, String pc) {
		Precondition.assertNonEmptyString("invalid name", pn);
		this.name = pn;
		this.value = pv;
		this.comparison = pc;
		boolean b = true;
		if (pv == null) {
			if (pc != null)
				b = false;
		} else if (!pv.equals(pc))
			b = false;
		this.equal = b;

	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getComparison() {
		return comparison;
	}

	public boolean getEqual() {
		return equal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((comparison == null) ? 0 : comparison.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		NameValueComparison other = (NameValueComparison) obj;
		if (comparison == null) {
			if (other.comparison != null)
				return false;
		} else if (!comparison.equals(other.comparison))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NameValueComparison [name=" + name + ", value=" + value
				+ ", comparison=" + comparison + ", equal=" + equal + "]";
	}

}
