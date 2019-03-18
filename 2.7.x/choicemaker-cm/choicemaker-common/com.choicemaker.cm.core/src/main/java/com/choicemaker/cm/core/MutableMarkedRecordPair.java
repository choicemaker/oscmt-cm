/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core;

import java.io.Serializable;
import java.util.Date;

import com.choicemaker.client.api.Decision;

/**
 * A record pair marked with a decision. Mostly used for human
 * marked training/testing pairs.
 *
 * @author    Martin Buechi
 */
public class MutableMarkedRecordPair<T extends Comparable<T> & Serializable>
	extends MutableRecordPair<T>
	implements IMarkedRecordPair<T> {

	/** A comment. */
	private String comment;

	private Date date;

	private Decision decision;

	private String src;

	private String user;

	public MutableMarkedRecordPair() {
	}

	/**
	 * Constructor for a marked record pair.
	 *
	 * @param   q  One of the records.
	 * @param   m  The other record.
	 * @param   decision  The <code>Decision</code>.
	 * @param   date  The date this decision was made/last revised.
	 * @param   user  The user who made the decision/last revised it.
	 * @param   src   The source of this record.
	 * @param   comment  A comment.
	 */
	public MutableMarkedRecordPair(
		Record<T> q,
		Record<T> m,
		Decision decision,
		Date date,
		String user,
		String src,
		String comment) {
		super(q, m);
		setMarkedDecision(decision);
		setDateMarked(date);
		setUser(user);
		setSource(src);
		setComment(comment);
	}

	/** Set a comment. */
	@Override
	public String getComment() {
		return comment;
	}

	/** Get the date the decision was made or last revised. */
	@Override
	public Date getDateMarked() {
		return date;
	}

	/**
	 * The <code>Decision</code> that was marked by a human reviewer about 
	 * whether this pair matches or not. This distinct from the {@link IRecordPair#getCmDecision Decision}
	 * that ChoiceMaker assigns.
	 */
	@Override
	public Decision getMarkedDecision() {
		return decision;
	}

	/** Get the source of this record. */
	@Override
	public String getSource() {
		return src;
	}

	/** Get the user who made the decision/last revised it. */
	@Override
	public String getUser() {
		return user;
	}

	/** Get a comment. */
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Set the date the decision was made or last revised.
	 * This field is <em>not</em> updated automatically
	 * when the decision field is modified.
	 */
	@Override
	public void setDateMarked(Date date) {
		this.date = date;
	}

	/**
	 * The mark a pair as to whether it matches or not. Marking is performed
	 * by human reviewers, as opposed to the decision
	 * {@link IRecordPair#setCmDecision assignments} made by
	 * ChoiceMaker.
	 */
	@Override
	public void setMarkedDecision(Decision decision) {
		this.decision = decision;
	}

	/** Set the source of this record. */
	@Override
	public void setSource(String src) {
		this.src = src;
	}

	/** Set the user who made the decision/last revised it. */
	@Override
	public void setUser(String user) {
		this.user = user;
	}

}
