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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSink;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.Repository;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.SinkFactory;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConf;

/**
 * Provides conversions between a <code>MarkedRecordPairSource</code>,
 * a <code>Collection</code>, and a <code>MarkedRecordPairSink</code>.
 * 
 * <ol>
 *   <li>The conversion from a <code>MarkedRecordPairSource</code> to a
 *     <code>List</code> is a single static method.</li>
 *   <li>The "conversion" from a <code>Collection</code> to a
 *     <code>MarkedRecordPairSource</code> adds a facade/iterator in form
 *     of an instance of this class to a <code>Collection</code>.</li>
 *   <li>The elements of a <code>Collection</code> can be stored in a
 *     <code>MarkedRecordPairSink</code>.</li>
 * </ol>
 *
 * @author    Martin Buechi
 */
public class MarkedRecordPairBinder implements MarkedRecordPairSource {
	private Collection c;
	private Iterator i;
	private String name = "Anonymous Marked Record Pair Binder";
	private ImmutableProbabilityModel model;

	/**
	 * Creates a <code>List</code> containing all the data from the
	 * <code>MarkedRecordPairSource</code>.
	 *
	 * @param   src  The <code>MarkedRecordPairSource</code> to read the data from.
	 * @return  The <code>List</code> with the data.
	 * @throws  IOException  If there is a problem reading data from <code>src</code>.
	 */
	public static List getList(MarkedRecordPairSource src, boolean includeHolds, Repository repository) throws IOException {
		List l = new LinkedList();
		src.open();
		int br = 0;
		while (src.hasNext()) {
			MutableMarkedRecordPair p = src.getNextMarkedRecordPair();
			if (includeHolds || p.getMarkedDecision() != Decision.HOLD) {
				p.setRepository(repository);
				l.add(p);
			}
			if ((br = (br + 1) % 100) == 0 && Thread.currentThread().isInterrupted()) {
				break;
			}
		}
		src.close();
		return l;
	}

	/**
	 * Stores the elements of a <code>Collection</code> in a
	 * <code>MarkedRecordPairSink</code>.
	 *
	 * @param   l  The <code>Collection</code> containing the elements to be stored.
	 * @param   snk  The <code>MarkedRecordPairSink</code> in which the elements are to
	 *            be stored.
	 * @throws  IOException  If an exception occurs adding elements to the
	 *            <code>MarkedRecordPairSink</code>.
	 */
	public static void store(Collection l, MarkedRecordPairSink snk) throws Exception {
		snk.open();
		Iterator i = l.iterator();
		while (i.hasNext()) {
			snk.putMarkedRecordPair((ImmutableMarkedRecordPair) i.next());
		}
		snk.close();
	}

	public static void store(List l, int[] selection, MarkedRecordPairSink snk) throws Exception {
		snk.open();
		if (selection != null) {
			for (int i = 0; i < selection.length; ++i) {
				snk.putMarkedRecordPair((MutableMarkedRecordPair) l.get(selection[i]));
			}
		}
		snk.close();
	}

	public static void store(MarkedRecordPairSource[] sources, MarkedRecordPairSink sink) throws Exception {
		sink.open();
		for (int i = 0; i < sources.length; ++i) {
			MarkedRecordPairSource src = sources[i];
			src.open();
			while (src.hasNext()) {
				sink.putMarkedRecordPair(src.getNextMarkedRecordPair());
			}
			src.close();
		}
		sink.close();
	}

	public static void store(String[] sourceNames, ImmutableProbabilityModel model, MarkedRecordPairSink sink)
		throws Exception {
		MarkedRecordPairSource[] sources = new MarkedRecordPairSource[sourceNames.length];
		for (int i = 0; i < sourceNames.length; ++i) {
			sources[i] = MarkedRecordPairSourceXmlConf.getMarkedRecordPairSource(sourceNames[i]);
			sources[i].setModel(model);
		}
		store(sources, sink);
	}

	public static void store(MarkedRecordPairSource[] sources, SinkFactory f, int distrib, int sinkSize)
		throws Exception {
		MarkedRecordPairSink[] sink = new MarkedRecordPairSink[distrib];
		int[] sizes = new int[distrib];
		for (int d = 0; d < distrib; ++d) {
			sink[d] = (MarkedRecordPairSink) f.getSink();
			sink[d].open();
		}
		int curSink = 0;
		for (int i = 0; i < sources.length; ++i) {
			MarkedRecordPairSource src = sources[i];
			src.open();
			while (src.hasNext()) {
				sink[curSink].putMarkedRecordPair(src.getNextMarkedRecordPair());
				++sizes[curSink];
				if (sizes[curSink] == sinkSize) {
					sink[curSink].close();
					sink[curSink] = (MarkedRecordPairSink) f.getSink();
					sink[curSink].open();
					sizes[curSink] = 0;
				}
				curSink = (curSink + 1) % distrib;
			}
			src.close();
		}
		for (int d = 0; d < distrib; ++d) {
			sink[d].close();
		}
	}

	public static void store(String[] sourceNames, ImmutableProbabilityModel model, SinkFactory f, int distrib, int sinkSize)
		throws Exception {
		MarkedRecordPairSource[] sources = new MarkedRecordPairSource[sourceNames.length];
		for (int i = 0; i < sourceNames.length; ++i) {
			sources[i] = MarkedRecordPairSourceXmlConf.getMarkedRecordPairSource(sourceNames[i]);
			sources[i].setModel(model);
		}
		store(sources, f, distrib, sinkSize);
	}

	/**
	 * Returns a <code>MarkedRecordPairSource</code> representing the elements
	 * code of the <code>Collection</code>.
	 *
	 * The source iterates directly over the <code>Collection</code>, without copying it.
	 * Hence, changes to elements are reflected and addition and removal of elements
	 * may cause the <code>MarkedRecordPairsSource</code>'s operation to throw
	 * exceptions.
	 *
	 *
	 * @param   c  The <code>Collection</code> containg the elements to be represented.
	 * @return  The <code>MarkedRecordPairSource</code> with the specified <code>Collection</code>.
	 */
	public static MarkedRecordPairSource getMarkedRecordPairSource(Collection c) {
		return new MarkedRecordPairBinder(c);
	}

	public MarkedRecordPairBinder(Collection c) {
		this.c = c;
	}

	@Override
	public void open() {
		i = c.iterator();
	}

	@Override
	public void close() {
		i = null;
	}

	@Override
	public boolean hasNext() {
		return i.hasNext();
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
	public String getFileName() {
		return null;
	}

	@Override
	public ImmutableRecordPair getNext() {
		return (ImmutableRecordPair) i.next();
	}

	@Override
	public MutableMarkedRecordPair getNextMarkedRecordPair() {
		return (MutableMarkedRecordPair) i.next();
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		model = m;
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		return null;
	}
}
