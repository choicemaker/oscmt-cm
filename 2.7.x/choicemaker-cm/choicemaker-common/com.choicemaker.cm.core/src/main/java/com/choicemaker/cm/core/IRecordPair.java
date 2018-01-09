/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import com.choicemaker.client.api.Decision;

/**
 * @author rphall
 */
public interface IRecordPair extends ImmutableRecordPair {

	public abstract ActiveClues getActiveClues();
	public abstract void setActiveClues(ActiveClues af);
	public abstract void setQueryRecord(Record q);
	public abstract Record getQueryRecord();
	public abstract void setMatchRecord(Record m);
	public abstract Record getMatchRecord();
	public abstract void setCmDecision(Decision cmDecision);
	public abstract Decision getCmDecision();
	public abstract void setProbability(float probability);
	public abstract float getProbability();
}
