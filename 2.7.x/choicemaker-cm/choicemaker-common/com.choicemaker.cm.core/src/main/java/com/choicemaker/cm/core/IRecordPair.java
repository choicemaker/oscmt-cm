/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.Serializable;

import com.choicemaker.client.api.Decision;

/**
 * @author rphall
 */
public interface IRecordPair<T extends Comparable<T> & Serializable> extends ImmutableRecordPair<T> {

	@Override
	public abstract ActiveClues getActiveClues();
	public abstract void setActiveClues(ActiveClues af);
	public abstract void setQueryRecord(Record<T> q);
	@Override
	public abstract Record<T> getQueryRecord();
	public abstract void setMatchRecord(Record<T> m);
	@Override
	public abstract Record<T> getMatchRecord();
	public abstract void setCmDecision(Decision cmDecision);
	@Override
	public abstract Decision getCmDecision();
	public abstract void setProbability(float probability);
	@Override
	public abstract float getProbability();
}
