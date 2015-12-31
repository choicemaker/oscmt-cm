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
package com.choicemaker.cm.core.base;

import java.util.Date;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.IMarkedRecordPair;
import com.choicemaker.cm.core.IRecordPair;
import com.choicemaker.cm.core.Record;

/**
 * @deprecated use MutableMarkedRecordPair instead
 * @author    Martin Buechi
 */
public class MarkedRecordPair extends RecordPair implements IMarkedRecordPair {

	/** @deprecated */
	public Decision decision;

	/** @deprecated */
	public Date date;

	/** @deprecated */
	public String user;

	/** @deprecated */
	public String src;

	/** @deprecated */
	public String comment;

	public MarkedRecordPair() {
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
	public MarkedRecordPair(
		Record q,
		Record m,
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

	/**
	 * The mark a pair as to whether it matches or not. Marking is performed
	 * by human reviewers, as opposed to the decision
	 * {@link IRecordPair#setCmDecision assignments} made by
	 * ChoiceMaker.
	 */
	public void setMarkedDecision(Decision decision) {
		this.decision = decision;
	}

	/**
	 * The <code>Decision</code> that was marked by a human reviewer about 
	 * whether this pair matches or not. This distinct from the {@link IRecordPair#getCmDecision Decision}
	 * that ChoiceMaker assigns.
	 */
	public Decision getMarkedDecision() {
		return decision;
	}

	/**
	 * Set the date the decision was made or last revised.
	 * This field is <em>not</em> updated automatically
	 * when the decision field is modified.
	 */
	public void setDateMarked(Date date) {
		this.date = date;
	}

	/** Get the date the decision was made or last revised. */
	public Date getDateMarked() {
		return date;
	}

	/** Set the user who made the decision/last revised it. */
	public void setUser(String user) {
		this.user = user;
	}

	/** Get the user who made the decision/last revised it. */
	public String getUser() {
		return user;
	}

	/** Set the source of this record. */
	public void setSource(String src) {
		this.src = src;
	}

	/** Get the source of this record. */
	public String getSource() {
		return src;
	}

	/** Get a comment. */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/** Set a comment. */
	public String getComment() {
		return comment;
	}

	/**
	 * @deprecated use getMarkedDecision instead
	 */
	public Decision getDecision() {
		return getMarkedDecision();
	}

	/**
	 * @deprecated use setMarkedDecision instead
	 */
	public void setDecision(Decision d) {
		setMarkedDecision(d);
	}

	public float getProbability() {
		return getProbability();
	}

}
