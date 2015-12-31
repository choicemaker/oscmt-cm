/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.composite.base;

import java.io.IOException;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.base.MutableMarkedRecordPair;

/**
 * Composite source of marked record pairs.
 * A composite source is a collection of other sources.
 *
 * @author    Martin Buechi
 */
public class CompositeMarkedRecordPairSource extends CompositeRecordPairSource implements MarkedRecordPairSource {
	/**
	 * Returns the next marked record pair.
	 *
	 * @throws  IOException if the current source throws an <code>IOException</code>.
	 * @throws  NullPointerException if there are no more marked record pairs.
	 * @return  The next marked record pair. 
	 */
	public MutableMarkedRecordPair getNextMarkedRecordPair() throws java.io.IOException {
		MutableMarkedRecordPair r = ((MarkedRecordPairSource) getCurSource()).getNextMarkedRecordPair();
		nextValid();
		return r;
	}

	/**
	 * Adds a marked record pair source at the end of the collection.
	 */
	public void add(MarkedRecordPairSource s) {
		super.add(s);
	}

	public void add(MarkedRecordPairSource s, boolean saveAsRel) {
		super.add(s, saveAsRel);	
	}

	public MarkedRecordPairSource getMarkedSource(int index) {
		return (MarkedRecordPairSource) getSourceAtIndex(index);
	}

	MarkedRecordPairSource[] getConstituents() {
		return (MarkedRecordPairSource[]) sources.toArray(new MarkedRecordPairSource[sources.size()]);
	}
}
