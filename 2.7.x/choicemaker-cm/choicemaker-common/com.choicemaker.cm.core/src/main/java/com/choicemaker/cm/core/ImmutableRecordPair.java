/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
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
public interface ImmutableRecordPair<T extends Comparable<T> & Serializable> {
	public abstract ActiveClues getActiveClues();
	public abstract Decision getCmDecision();
	public abstract Record<T> getMatchRecord();
	public abstract float getProbability();
	public abstract Record<T> getQueryRecord();
}
