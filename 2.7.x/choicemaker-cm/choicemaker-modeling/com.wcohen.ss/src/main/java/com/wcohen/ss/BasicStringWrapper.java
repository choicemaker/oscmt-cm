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

import com.wcohen.ss.api.StringWrapper;

/**
 * An extendible (non-final) class that implements some of the
 * functionality of a string.
 */

public class BasicStringWrapper implements StringWrapper
{
	private static final long serialVersionUID = 1L;
	private String s;

	public BasicStringWrapper(String s) { this.s = s; } 
	public char charAt(int i) { return s.charAt(i); }
	public int length() { return s.length(); }
	public String unwrap() { return s; }
	public String toString() { return "[wrap '"+s+"']"; }
	public int hashCode() { return s.hashCode(); }

}
