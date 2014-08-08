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
package com.choicemaker.cm.io.blocking.automated.offline.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecord;

/**
 * This is a sink that stores MatchRecord.
 * 
 * @author pcheung
 *
 */
public interface IMatchRecordSink<T extends Comparable<? super T>> extends ISink {

	/** Writes out an ArrayList of MatchRecord. */
	public void writeMatches (List<T> matches) throws BlockingException;
	
	/** Writes out a Collection of MatchRecord. */
	public void writeMatches (Collection<T> c) throws BlockingException;
	
	/** Writes out an Iterator containing MatchRecord. */
	public void writeMatches (Iterator<T> it) throws BlockingException;
	
	/** Writes out a single MatchRecord. */
	public void writeMatch (MatchRecord match) throws BlockingException;
	
}
