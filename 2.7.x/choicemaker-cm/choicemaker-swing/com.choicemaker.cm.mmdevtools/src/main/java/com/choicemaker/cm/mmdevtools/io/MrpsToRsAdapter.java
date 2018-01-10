/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools.io;

import java.io.IOException;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;


public class MrpsToRsAdapter implements RecordSource {

	public static final int Q_ONLY = -1;
	public static final int M_ONLY = -2;
	public static final int BOTH = 3;

	protected MarkedRecordPairSource mrps;
	protected int which;
	
	protected MutableMarkedRecordPair pair;

	public MrpsToRsAdapter(MarkedRecordPairSource mrps) {
		this(mrps, BOTH);
	}
	
	public MrpsToRsAdapter(MarkedRecordPairSource mrps, int which) {
		this.mrps = mrps;
		this.which = which;
		
		if (this.which != Q_ONLY && this.which != M_ONLY && this.which != BOTH) {
			throw new IllegalArgumentException("Which source has illegal value: " + which);
		}
	}

	public MarkedRecordPairSource getMarkedRecordPairSource() {
		return mrps;
	}
	
	public int getWhich() {
		return which;
	}

	public Record getNext() throws IOException {
		if (which == BOTH) {
			if (pair == null) {
				pair = mrps.getNextMarkedRecordPair();
				return pair.getQueryRecord();
			} else {
				Record ret = pair.getMatchRecord();
				pair = null;
				return ret;
			}
		} else if (which == Q_ONLY) {
			return mrps.getNext().getQueryRecord();
		} else if (which == M_ONLY) {
			return mrps.getNext().getMatchRecord();
		} else {
			throw new IllegalStateException("Unknown which value: " + which);
		}
	}

	public void open() throws IOException {
		mrps.open();
	}

	public void close() throws IOException {
		mrps.close();
	}

	public boolean hasNext() throws IOException {
		if (which == BOTH) {
			return pair != null || mrps.hasNext();
		} else {
			return mrps.hasNext();
		}
	}

	public String getName() {
		return "MRPS to RS Adapter";
	}

	public void setName(String name) {
		// do nothing..
	}

	public ImmutableProbabilityModel getModel() {
		return mrps.getModel();
	}

	public void setModel(ImmutableProbabilityModel m) {
		mrps.setModel(m);
	}

	public String getFileName() {
		return null;
	}

	public boolean hasSink() {
		return false;
	}

	public Sink getSink() {
		return null;
	}
		
}
