/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;


/**
 * A result of matching of two records that includes a match probability, decision and note. 
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class MatchScore implements IMatchScore{

	/** As of 2010-11-12 */
	static final long serialVersionUID = -5999924061483754148L;

	protected Decision3 decision;
	protected float probability;
	protected String note;

	public MatchScore() {
		super();
	}
	
	public MatchScore(float probability, Decision3 decision, String note) {
		this.decision = decision;
		this.probability = probability;
		this.note = note;
	}

	@Override
	public Decision3 getDecision() {
		return decision;
	}

	@Override
	public String getNote() {
		return note;
	}

	@Override
	public float getProbability() {
		return probability;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((decision == null) ? 0 : decision.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + Float.floatToIntBits(probability);
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
		MatchScore other = (MatchScore) obj;
		if (decision == null) {
			if (other.decision != null)
				return false;
		} else if (!decision.equals(other.decision))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (Float.floatToIntBits(probability) != Float
				.floatToIntBits(other.probability))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MatchScore [decision=" + decision + ", probability="
				+ probability + "]";
	}

}
