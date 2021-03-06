/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
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

import com.choicemaker.client.api.Decision;

/**
 * A replacement for the deprecated RecordPair class, which allows direct access
 * to field members.
 * 
 * @author rphall
 */
public class MutableRecordPair<T extends Comparable<T> & Serializable>
		extends RecordData implements IRecordPair<T> {

	/** The clues that fired on a pair */
	private ActiveClues af;

	/** The decision assigned by ChoiceMaker */
	private Decision cmDecision;

	/** The other record, usually called the match record */
	private Record<T> m;

	/** The probability assigned by ChoiceMaker */
	private float probability;

	/** One of the records, usually called the query record */
	private Record<T> q;

	public MutableRecordPair() {
	}

	/**
	 * Constructor.
	 *
	 * @param q
	 *            One of the records.
	 * @param m
	 *            The other record.
	 */
	public MutableRecordPair(Record<T> q, Record<T> m) {
		setQueryRecord(q);
		setMatchRecord(m);
	}

	@Override
	public ActiveClues getActiveClues() {
		return af;
	}

	@Override
	public Decision getCmDecision() {
		return cmDecision;
	}

	/**
	 * @see com.choicemaker.cm.core.base.RecordData#getFirstRecord()
	 */
	@Override
	public Record<T> getFirstRecord() {
		return getQueryRecord();
	}

	@Override
	public Record<T> getMatchRecord() {
		return m;
	}

	@Override
	public float getProbability() {
		return probability;
	}

	@Override
	public Record<T> getQueryRecord() {
		return q;
	}

	/**
	 * @see com.choicemaker.cm.core.base.RecordData#getSecondRecord()
	 */
	@Override
	public Record<T> getSecondRecord() {
		return getMatchRecord();
	}

	@Override
	public void setActiveClues(ActiveClues af) {
		this.af = af;
	}

	@Override
	public void setCmDecision(Decision cmDecision) {
		this.cmDecision = cmDecision;
	}

	@Override
	public void setMatchRecord(Record<T> m) {
		this.m = m;
	}

	@Override
	public void setProbability(float probability) {
		this.probability = probability;
	}

	@Override
	public void setQueryRecord(Record<T> q) {
		this.q = q;
	}

}
