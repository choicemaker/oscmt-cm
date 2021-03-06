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

import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.StringWrapperIterator;

/** A simple StringWrapperIterator implementation. 
 */

public class BasicStringWrapperIterator implements StringWrapperIterator {
	private static final long serialVersionUID = 1L;
	private Iterator myIterator;
	public BasicStringWrapperIterator(Iterator i) { myIterator=i; }
	public boolean hasNext() { return myIterator.hasNext(); }
	public Object next() { return myIterator.next(); }
	public StringWrapper nextStringWrapper() { return (StringWrapper)next(); }
	public void remove() { myIterator.remove(); }
}
