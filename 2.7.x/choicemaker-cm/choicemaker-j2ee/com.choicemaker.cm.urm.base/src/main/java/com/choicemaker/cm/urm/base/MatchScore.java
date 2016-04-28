/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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

	public Decision3 getDecision() {
		return decision;
	}

	public String getNote() {
		return note;
	}

	public float getProbability() {
		return probability;
	}

}
