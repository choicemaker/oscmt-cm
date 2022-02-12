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
 * Something that implements some of the functionality of Java's
 * string class, but which is a non-final class, and hence can also
 * cache additional information to facilitate later processing.
 */
public interface StringWrapper extends Serializable {
	/** Return the string that is wrapped. */
	public String unwrap();
	/** Return the i-th char of the wrapped string */
	public char charAt(int i);
	/** Return the length of the wrapped string */
	public int length();
}
