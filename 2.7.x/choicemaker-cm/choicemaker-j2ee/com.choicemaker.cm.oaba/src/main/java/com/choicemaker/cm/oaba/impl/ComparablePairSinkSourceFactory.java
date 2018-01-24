/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IComparableSink;
import com.choicemaker.cm.oaba.core.IComparableSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IComparableSource;
import com.choicemaker.cm.oaba.core.IPairIDSink;
import com.choicemaker.cm.oaba.core.IPairIDSinkSourceFactory;

/**
 * @author pcheung
 *
 */
@SuppressWarnings({ "rawtypes" })
public class ComparablePairSinkSourceFactory implements
		IComparableSinkSourceFactory {

	private IPairIDSinkSourceFactory factory;

	public ComparablePairSinkSourceFactory(IPairIDSinkSourceFactory factory) {
		this.factory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparableSinkSourceFactory#getNextSink()
	 */
	@Override
	public IComparableSink getNextSink() throws BlockingException {
		return new ComparablePairSink(factory.getNextSink());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparableSinkSourceFactory#getNextSource()
	 */
	@Override
	public IComparableSource getNextSource() throws BlockingException {
		return new ComparablePairSource(factory.getNextSource());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparableSinkSourceFactory
	 * #getSource(com.choicemaker.cm.aba
	 * .offline.core.IComparableSink)
	 */
	@Override
	public IComparableSource getSource(IComparableSink sink)
			throws BlockingException {
		IPairIDSink o = (IPairIDSink) sink.getBaseObject();
		return new ComparablePairSource(factory.getSource(o));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparableSinkSourceFactory#getNumSink()
	 */
	@Override
	public int getNumSink() {
		return factory.getNumSink();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparableSinkSourceFactory#getNumSource()
	 */
	@Override
	public int getNumSource() {
		return factory.getNumSource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.
	 * IComparableSinkSourceFactory
	 * #move(com.choicemaker.cm.aba
	 * .offline.core.IComparableSink,
	 * com.choicemaker.cm.oaba.core.IComparableSink)
	 */
	@Override
	public void move(IComparableSink sink1, IComparableSink sink2)
			throws BlockingException {
		factory.move((IPairIDSink) sink1.getBaseObject(),
				(IPairIDSink) sink2.getBaseObject());
	}

}
