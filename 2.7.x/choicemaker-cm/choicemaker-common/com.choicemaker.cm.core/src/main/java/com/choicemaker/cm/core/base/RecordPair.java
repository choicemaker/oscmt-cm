/*
 * Copyright (c) 2001, 2019 ChoiceMaker Technologies, Inc. and others.
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
@Deprecated
public class RecordPair<T extends Comparable<T> & Serializable> extends RecordData implements IRecordPair<T> {

	/**
	 * One of the records, usually called the query record.
	 * @deprecated use get/setQueryRecord() instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	@Deprecated
	public Record<T> q;

	/**
	 * The other record, usually called the match record.
	 * @deprecated use get/setMatchRecord() instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	@Deprecated
	public Record<T> m;

	/**
	 * The probability assigned by ChoiceMaker.
	 * @deprecated use get/setProbability instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	@Deprecated
	public float probability;

	/**
	 * The decision assigned by ChoiceMaker.
	 * @deprecated use get/setCmDecision instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	@Deprecated
	public Decision cmDecision;
	
	/**
	 * The clues that fired on a pair.
	 * @deprecated use get/setProbability instead. This is field
	 * is still used by generated code, but shouldn't used elsewhere.
	 */
	@Deprecated
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
	@Override
	public Record<T> getFirstRecord() {
		return getQueryRecord();
	}

	/**
	 * @see com.choicemaker.cm.core.base.RecordData#getSecondRecord()
	 */
	@Override
	public Record<T> getSecondRecord() {
		return getMatchRecord();
	}
	
	@Override
	public ActiveClues getActiveClues() {
		return af;	
	}
	
	@Override
	public void setActiveClues(ActiveClues af) {
		this.af = af;	
	}

	@Override
	public void setQueryRecord(Record<T> q) {
		this.q = q;
	}

	@Override
	public Record<T> getQueryRecord() {
		return q;
	}

	@Override
	public void setMatchRecord(Record<T> m) {
		this.m = m;
	}

	@Override
	public Record<T> getMatchRecord() {
		return m;
	}

	@Override
	public void setCmDecision(Decision cmDecision) {
		this.cmDecision = cmDecision;
	}

	@Override
	public Decision getCmDecision() {
		return cmDecision;
	}

	@Override
	public void setProbability(float probability) {
		this.probability = probability;
	}

	@Override
	public float getProbability() {
		return probability;
	}

}

