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
public interface ImmutableRecordPair {
	public abstract ActiveClues getActiveClues();
	public abstract Decision getCmDecision();
	public abstract Record getMatchRecord();
	public abstract float getProbability();
	public abstract Record getQueryRecord();
}
