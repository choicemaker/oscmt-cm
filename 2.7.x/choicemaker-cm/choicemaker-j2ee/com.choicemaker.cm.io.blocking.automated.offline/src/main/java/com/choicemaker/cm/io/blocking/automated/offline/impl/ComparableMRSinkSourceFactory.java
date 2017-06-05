/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.io.blocking.automated.offline.core.IComparableSink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IComparableSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IComparableSource;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2Sink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2SinkSourceFactory;

/**
 * This is a wrapper on MatchRecord2SinkSourceFactory to make look like a
 * IComparableSinkSourceFactory.
 * 
 * @author pcheung
 *
 */
// @SuppressWarnings({"rawtypes", "unchecked"})
public class ComparableMRSinkSourceFactory<T extends Comparable<T>> implements
		IComparableSinkSourceFactory<MatchRecord2<T>> {

	private IMatchRecord2SinkSourceFactory<T> factory;

	public ComparableMRSinkSourceFactory(
			IMatchRecord2SinkSourceFactory<T> factory) {
		this.factory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IComparableSinkSourceFactory#getNextSink()
	 */
	@Override
	public IComparableSink<MatchRecord2<T>> getNextSink()
			throws BlockingException {
		return new ComparableMRSink<T>(factory.getNextSink());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IComparableSinkSourceFactory#getNextSource()
	 */
	@Override
	public IComparableSource<MatchRecord2<T>> getNextSource()
			throws BlockingException {
		return new ComparableMRSource<T>(factory.getNextSource());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IComparableSinkSourceFactory
	 * #getSource(com.choicemaker.cm.io.blocking.automated
	 * .offline.core.IComparableSink)
	 */
	@Override
	public IComparableSource<MatchRecord2<T>> getSource(
			IComparableSink<MatchRecord2<T>> sink) throws BlockingException {
		@SuppressWarnings("unchecked")
		IMatchRecord2Sink<T> o = (IMatchRecord2Sink<T>) sink.getBaseObject();

		return new ComparableMRSource<T>(factory.getSource(o));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IComparableSinkSourceFactory#getNumSink()
	 */
	@Override
	public int getNumSink() {
		return factory.getNumSink();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IComparableSinkSourceFactory#getNumSource()
	 */
	@Override
	public int getNumSource() {
		return factory.getNumSource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.
	 * IComparableSinkSourceFactory
	 * #move(com.choicemaker.cm.io.blocking.automated
	 * .offline.core.IComparableSink,
	 * com.choicemaker.cm.io.blocking.automated.offline.core.IComparableSink)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void move(IComparableSink<MatchRecord2<T>> sink1,
			IComparableSink<MatchRecord2<T>> sink2) throws BlockingException {
		factory.move((IMatchRecord2Sink<T>) sink1.getBaseObject(),
				(IMatchRecord2Sink<T>) sink2.getBaseObject());
	}

}
