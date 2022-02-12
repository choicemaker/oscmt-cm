/*******************************************************************************
 * Copyright (c) 2007, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.wcohen.ss.data;

import java.util.Collections;

import com.wcohen.ss.BasicDistanceInstanceIterator;
import com.wcohen.ss.api.DistanceInstance;
import com.wcohen.ss.api.DistanceInstanceIterator;
import com.wcohen.ss.api.StringDistanceTeacher;
import com.wcohen.ss.api.StringWrapperIterator;

/**
 * Train a StringDistanceLearner.
 */
public class BasicTeacher extends StringDistanceTeacher
{
	private static final long serialVersionUID = 1L;
	private DistanceInstanceIterator distanceExamplePool;
	private DistanceInstanceIterator distanceInstancePool;
	private StringWrapperIterator wrapperIterator;

	/** Create a teacher from a blocker and a dataset.
	 * Will train from all blocked pairs.
	 */
	public BasicTeacher(final Blocker blocker,final MatchData data)
	{
		blocker.block(data);
		wrapperIterator = data.getIterator();
		distanceInstancePool = new BasicDistanceInstanceIterator(Collections.EMPTY_SET.iterator());
		distanceExamplePool = 
			new DistanceInstanceIterator() {
				private static final long serialVersionUID = 1L;
				private int cursor=0;
				public boolean hasNext() { return cursor<blocker.size(); }
				public Object next() { return blocker.getPair( cursor++ ); }
				public void remove() { throw new UnsupportedOperationException(); }
				public DistanceInstance nextDistanceInstance() { return (DistanceInstance)next();}
			};
	}

	/**
	 * Create a teacher using specific values for the various iterators. 
	 */
	public BasicTeacher(
		StringWrapperIterator wrapperIterator,
		DistanceInstanceIterator distanceInstancePool,
		DistanceInstanceIterator distanceExamplePool
		)
	{
		this.wrapperIterator = wrapperIterator;
		this.distanceInstancePool = distanceInstancePool;
		this.distanceExamplePool = distanceExamplePool;
	}

	public StringWrapperIterator stringWrapperIterator() 
	{
		return wrapperIterator;
	}

	public DistanceInstanceIterator distanceInstancePool()
	{
		return distanceInstancePool;
	}

	public DistanceInstanceIterator distanceExamplePool() 
	{
		return distanceExamplePool;
	}

	public DistanceInstance labelInstance(DistanceInstance distanceInstance) 
	{	
		return distanceInstance;
	}

	public boolean hasAnswers() 
	{ 
		return true; 
	}
}
