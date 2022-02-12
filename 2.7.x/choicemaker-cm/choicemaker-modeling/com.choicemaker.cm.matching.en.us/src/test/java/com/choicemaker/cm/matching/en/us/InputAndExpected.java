/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

public class InputAndExpected {
	public final String first;
	public final String middle;
	public final String last;
	public final AdhocName expected;

	public InputAndExpected(String fn, String mn, String ln, AdhocName pn) {
		this.first = fn;
		this.middle = mn;
		this.last = ln;
		this.expected = pn;
	}

	InputAndExpected(String fn, String mn, String ln, String pnFirst,
			String pnMiddle, String pnLast, String pnTitles,
			String pnMaiden, String pnMomFn) {
		this.first = fn;
		this.middle = mn;
		this.last = ln;
		this.expected = new AdhocName(pnFirst, pnMiddle, pnLast);
		this.expected.setTitles(pnTitles);
		this.expected.setPotentialMaidenName(pnMaiden);
		this.expected.setMothersFirstName(pnMomFn);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((expected == null) ? 0 : expected.hashCode());
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((last == null) ? 0 : last.hashCode());
		result =
			prime * result + ((middle == null) ? 0 : middle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InputAndExpected other = (InputAndExpected) obj;
		if (expected == null) {
			if (other.expected != null) {
				return false;
			}
		} else if (!expected.equals(other.expected)) {
			return false;
		}
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (last == null) {
			if (other.last != null) {
				return false;
			}
		} else if (!last.equals(other.last)) {
			return false;
		}
		if (middle == null) {
			if (other.middle != null) {
				return false;
			}
		} else if (!middle.equals(other.middle)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "InputAndExpected [first=" + first + ", middle=" + middle
				+ ", last=" + last + ", expected=" + expected + "]";
	}

}