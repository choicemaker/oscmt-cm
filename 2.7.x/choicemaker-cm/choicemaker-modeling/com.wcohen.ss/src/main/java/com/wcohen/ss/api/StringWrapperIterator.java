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
package com.wcohen.ss.api;

import java.io.Serializable;

/**
 * An iterator over StringWrapper objects.
 */
public interface StringWrapperIterator extends java.util.Iterator, Serializable {
	public boolean hasNext();
	public Object next();
	public StringWrapper nextStringWrapper();
}
