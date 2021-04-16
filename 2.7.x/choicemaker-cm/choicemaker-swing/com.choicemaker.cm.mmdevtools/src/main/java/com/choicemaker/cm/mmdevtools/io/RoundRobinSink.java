/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools.io;

import java.io.IOException;

import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSink;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.SinkFactory;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConf;
import com.choicemaker.cm.io.xml.base.XmlRecordSinkFactory;

public class RoundRobinSink implements RecordSink, MarkedRecordPairSink {

	protected SinkFactory factory;
	protected int distrib;
	protected int sinkSize;

	protected int[] sizes;
	protected Sink[] sinks;
	protected int curSink;

	public RoundRobinSink(SinkFactory factory, int distrib, int sinkSize) {
		this.factory = factory;
		this.distrib = distrib;
		this.sinkSize = sinkSize;
	}

	@Override
	public void put(Record r) throws Exception {
		((RecordSink)sinks[curSink]).put(r);
		sizes[curSink]++;
		if (sizes[curSink] == sinkSize) {
			sinks[curSink].close();
			sinks[curSink] = factory.getSink();
			sinks[curSink].open();
			sizes[curSink] = 0;
		}
		curSink = (curSink + 1) % distrib;
	}

	@Override
	public void putMarkedRecordPair(ImmutableMarkedRecordPair mrp) throws Exception {
		((MarkedRecordPairSink)sinks[curSink]).put(mrp);
		sizes[curSink]++;
		if (sizes[curSink] == sinkSize) {
			sinks[curSink].close();
			sinks[curSink] = factory.getSink();
			sinks[curSink].open();
			sizes[curSink] = 0;
		}
		curSink = (curSink + 1) % distrib;
	}

	@Override
	public void put(ImmutableRecordPair rp) throws Exception {
		putMarkedRecordPair((ImmutableMarkedRecordPair)rp);
	}

	public void saveSourceDescriptors() throws XmlConfException {
		if (factory instanceof XmlRecordSinkFactory) {
			Source[] sources = ((XmlRecordSinkFactory)factory).getSources();
			for (int i = 0; i < sources.length; i++) {
				RecordSourceXmlConf.add((RecordSource)sources[i]);
			}
		}
	}

	@Override
	public void open() throws IOException {
		sizes = new int[distrib];
		sinks = new Sink[distrib];
		for (int i = 0; i < distrib; i++) {
			sinks[i] = factory.getSink();
			sinks[i].open();
		}
		curSink = 0;
	}

	@Override
	public void close() throws Exception {
		for (int i = 0; i < distrib; i++) {
			sinks[i].close();
		}
		sizes = null;
		sinks = null;
		curSink = -1;
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		throw new UnsupportedOperationException();
	}

	/**
	 * NOP for now
	 * @see com.choicemaker.cm.core.base.Sink#flush()
	 */
	@Override
	public void flush() {
	}

}
