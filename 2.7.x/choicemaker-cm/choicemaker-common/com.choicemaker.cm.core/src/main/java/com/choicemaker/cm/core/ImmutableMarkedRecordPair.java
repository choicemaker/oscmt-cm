/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.Serializable;
import java.util.Date;

import com.choicemaker.client.api.Decision;

/**
 * @author rphall
 */
public interface ImmutableMarkedRecordPair<T extends Comparable<T> & Serializable> extends ImmutableRecordPair<T> {

	/** Get a comment. */
	public abstract String getComment();

	/** Get the date the decision was made or last revised. */
	public abstract Date getDateMarked();
	/**
	 * The <code>Decision</code> that was marked by a human reviewer about 
	 * whether this pair matches or not. This distinct from the {@link IRecordPair#getCmDecision Decision}
	 * that ChoiceMaker assigns.
	 */
	public abstract Decision getMarkedDecision();

	/** Get the source of this record. */
	public abstract String getSource();

	/** Get the user who made the decision/last revised it. */
	public abstract String getUser();

}
