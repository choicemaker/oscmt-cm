/*******************************************************************************
 * Copyright (c) 2003 Carnegie Mellon University
 *
 * This program and the accompanying materials are made available under the
 * terms of an instance of the University of Illinois/NCSA Open Source
 * license which accompanies this distribution.
 *
 * Authors: William W. Cohen, Pradeep Ravikumar, Stephen E. Fienberg, and others
 * https://sourceforge.net/projects/secondstring
 *******************************************************************************/
package com.wcohen.ss;

import java.util.Iterator;

import com.wcohen.ss.api.DistanceInstance;
import com.wcohen.ss.api.DistanceInstanceIterator;

/** A simple DistanceInstanceIterator implementation. 
 */

public class BasicDistanceInstanceIterator implements DistanceInstanceIterator {
	private static final long serialVersionUID = 1L;
	private Iterator myIterator;
	public BasicDistanceInstanceIterator(Iterator i) { myIterator=i; }
	public boolean hasNext() { return myIterator.hasNext(); }
	public Object next() { return myIterator.next(); }
	public DistanceInstance nextDistanceInstance() { return (DistanceInstance)next(); }
	public void remove() { myIterator.remove(); }
}
