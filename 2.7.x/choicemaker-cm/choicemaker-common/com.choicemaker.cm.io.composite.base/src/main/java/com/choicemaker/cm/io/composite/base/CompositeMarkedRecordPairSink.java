/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.composite.base;

import java.io.IOException;

import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSink;
import com.choicemaker.cm.core.MarkedRecordPairSource;

/**
 * @author    Martin Buechi
 */
public class CompositeMarkedRecordPairSink implements MarkedRecordPairSink {
	private String name;
	private ImmutableProbabilityModel model;
	private MarkedRecordPairSource[] constituents;
	private int[] constituentSizes;
	private MarkedRecordPairSink curSink;
	private int curSource;
	private int curIdx;

	public CompositeMarkedRecordPairSink(CompositeMarkedRecordPairSource src) {
		name = src.getName();
		model = src.getModel();
		constituents = src.getConstituents();
		constituentSizes = src.getSizes();
	}

	@Override
	public void open() throws IOException {
		curSource = 0;
		curIdx = 0;
		curSink = (MarkedRecordPairSink) constituents[curSource].getSink();
		curSink.open();
	}

	@Override
	public void close() throws Exception {
		curSink.close();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		this.model = m;
		for (int i = 0; i < constituents.length; ++i) {
			constituents[i].setModel(m);
		}
	}

	@Override
	public void put(ImmutableRecordPair r) throws Exception {
		putMarkedRecordPair((ImmutableMarkedRecordPair) r);
	}

	@Override
	public void putMarkedRecordPair(ImmutableMarkedRecordPair r) throws Exception {
		while (curIdx == constituentSizes[curSource]) {
			curSink.close();
			++curSource;
			curIdx = 0;
			curSink = (MarkedRecordPairSink) constituents[curSource].getSink();
			curSink.open();
		}
		curSink.putMarkedRecordPair(r);
		++curIdx;
	}

	/** NOP for now */
	@Override
	public void flush() throws IOException {
	}
		
}
