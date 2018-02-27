/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.util.List;

import com.choicemaker.util.Precondition;

/**
 * A matching score between a single and a composite record that consists of the
 * scores between the single record and records included in the composite record. 
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class CompositeMatchScore implements IMatchScore {
	
	// 2013 -08-07 rphall
	private static final long serialVersionUID = 1L;

	protected MatchScore[] innerScores;
	
	public CompositeMatchScore() {
		super();
	}

	public CompositeMatchScore(MatchScore[] is) {
		Precondition.assertNonNullArgument(is);
		this.innerScores = is;
	}

	public CompositeMatchScore(List<MatchScore> is) {
		this(is.toArray(new MatchScore[is.size()]));
	}

	public MatchScore[] getInnerScores() {
		return innerScores;
	}

	public Decision3 getConservativeDecision() {
		return null;
	}

	public float getAverageProbability(){
		float avProb = 0;
		for(int n=0; n<this.innerScores.length; n++ ){
			avProb += this.innerScores[n].probability;
		}
		return  avProb/this.innerScores.length; 
	}

	public Decision3 getDecision() {
		return getConservativeDecision(); 
	}


	public float getProbability() {
		return getAverageProbability();
	}

	@Override
	public String getNote() {
		return new String().intern();
	}

}
