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

import java.io.Serializable;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.IRecordPair;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MutableRecordPair;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordData;


/**
 * Use the MutableRecordPair class instead, or better yet,
 * reference the mutable or immutable record pair interface.
 * @author    Martin Buechi
 * @deprecated
 * @see MutableRecordPair
 * @see IRecordPair
 * @see ImmutableRecordPair
 */
public class RecordPair<T extends Comparable<T> & Serializable> extends RecordData implements IRecordPair<T> {

	/**
	 * One of the records, usually called the query record.
	 * @deprecated use get/setQueryRecord() instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	public Record<T> q;

	/**
	 * The other record, usually called the match record.
	 * @deprecated use get/setMatchRecord() instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	public Record<T> m;

	/**
	 * The probability assigned by ChoiceMaker.
	 * @deprecated use get/setProbability instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	public float probability;

	/**
	 * The decision assigned by ChoiceMaker.
	 * @deprecated use get/setCmDecision instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	public Decision cmDecision;
	
	/**
	 * The clues that fired on a pair.
	 * @deprecated use get/setProbability instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	public ActiveClues af;
	
	public RecordPair() {
	}

	/**
	 * Constructor.
	 *
	 * @param   q  One of the records.
	 * @param   m  The other record.
	 */
	public RecordPair(Record<T> q, Record<T> m) {
		setQueryRecord(q);
		setMatchRecord(m);
	}

	/**
	 * @see com.choicemaker.cm.core.base.RecordData#getFirstRecord()
	 */
	public Record<T> getFirstRecord() {
		return getQueryRecord();
	}

	/**
	 * @see com.choicemaker.cm.core.base.RecordData#getSecondRecord()
	 */
	public Record<T> getSecondRecord() {
		return getMatchRecord();
	}
	
	public ActiveClues getActiveClues() {
		return af;	
	}
	
	public void setActiveClues(ActiveClues af) {
		this.af = af;	
	}

	public void setQueryRecord(Record<T> q) {
		this.q = q;
	}

	public Record<T> getQueryRecord() {
		return q;
	}

	public void setMatchRecord(Record<T> m) {
		this.m = m;
	}

	public Record<T> getMatchRecord() {
		return m;
	}

	public void setCmDecision(Decision cmDecision) {
		this.cmDecision = cmDecision;
	}

	public Decision getCmDecision() {
		return cmDecision;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}

	public float getProbability() {
		return probability;
	}

}

