/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.util.Date;

import com.choicemaker.client.api.Decision;

/**
 * @author rphall
 */
public interface IMarkedRecordPair extends IRecordPair, ImmutableMarkedRecordPair {
	/**
	 * The mark a pair as to whether it matches or not. Marking is performed
	 * by human reviewers, as opposed to the decision
	 * {@link IRecordPair#setCmDecision assignments} made by
	 * ChoiceMaker.
	 */
	public abstract void setMarkedDecision(Decision decision);
	/**
	 * Set the date the decision was made or last revised.
	 * This field is <em>not</em> updated automatically
	 * when the decision field is modified.
	 */
	public abstract void setDateMarked(Date date);
	/** Set the user who made the decision/last revised it. */
	public abstract void setUser(String user);
	/** Set the source of this record. */
	public abstract void setSource(String src);
	/** Set a comment. */
	public abstract void setComment(String comment);
}
