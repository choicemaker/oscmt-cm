/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
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
package com.choicemaker.client.api;

import java.io.Serializable;

import com.choicemaker.util.Precondition;

public class EvaluatedPair<T extends Comparable<T> & Serializable>
		implements Serializable {

	private static final long serialVersionUID = 271L;

	public static char DEFAULT_DELIMITER = ' ';

	/**
	 * Valid probabilities are in the range 0.0f to 1.0f, inclusive
	 * @param p the probability
	 * @return true if between 0.0 and 1.0, inclusive, false otherwise
	 */
	public static boolean isValidProbability(float p) {
		boolean retVal = (p >= 0.0f) && (p <= 1.0f);
		return retVal;
	}

	private final DataAccessObject<T> q;
	private final DataAccessObject<T> m;
	private final float p;
	private final Decision d;
	private final String[] notes;

	public EvaluatedPair(DataAccessObject<T> q, DataAccessObject<T> m, float p,
			Decision d) {
		this(q, m, p, d, null);
	}

	public EvaluatedPair(DataAccessObject<T> q, DataAccessObject<T> m, float p,
			Decision d, String[] notes) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null candidate", m);
		Precondition.assertBoolean("invalid probability: " + p,
				isValidProbability(p));
		Precondition.assertNonNullArgument("null decision", d);
		this.q = q;
		this.m = m;
		this.p = p;
		this.d = d;
		if (notes == null) {
			this.notes = new String[0];
		} else {
			this.notes = new String[notes.length];
			System.arraycopy(notes, 0, this.notes, 0, notes.length);
		}
	}

	public EvaluatedPair(EvaluatedPair<T> p) {
		this(p.getRecord1(), p.getRecord2(), p.getMatchProbability(),
				p.getMatchDecision(), p.getNotes());
	}

	public DataAccessObject<T> getRecord1() {
		return q;
	}

	public DataAccessObject<T> getRecord2() {
		return m;
	}

	public float getMatchProbability() {
		return p;
	}

	public Decision getMatchDecision() {
		return d;
	}

	public String[] getNotes() {
		String[] retVal = new String[notes.length];
		System.arraycopy(this.notes, 0, retVal, 0, this.notes.length);
		return retVal;
	}

	public String getNotesAsDelimitedString() {
		return getNotesAsDelimitedString(DEFAULT_DELIMITER);
	}

	public String getNotesAsDelimitedString(char delimiter) {
		String retVal = "";
		if (getNotes() != null) {
			StringBuilder sb = new StringBuilder();
			String[] notes = getNotes();
			for (int i = 0; i < notes.length; i++) {
				String note = notes[i];
				if (note != null) {
					note = note.trim();
					if (note.length() > 0) {
						sb.append(note);
						sb.append(delimiter);
					}
				}
			}
			retVal = sb.toString().trim();
		}
		return retVal;
	}

	@Override
	public String toString() {
		return "EvaluatedPair [q=" + (q == null ? null : q.getId()) + ", m="
				+ (m == null ? null : m.getId()) + ", p=" + p + ", d=" + d
				+ ", notes='" + getNotesAsDelimitedString() + "']";
	}

}
