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
package com.wcohen.ss.tokens;

import com.wcohen.ss.api.Token;


/**
 * An interned version of a string.    
 *
 */

public class BasicToken implements Token, Comparable
{
	private static final long serialVersionUID = 1L;
	private final int index;
	private final String value;
	
	BasicToken(int index,String value) {
		this.index = index;
		this.value = value;
	}
	public String getValue() { return value; }
	public int getIndex() { return index; }
	public int compareTo(Object o) {
		if ( !(o instanceof Token) ) {
			throw new ClassCastException("incompatible type");
		}
		Token t = (Token) o;
		return index - t.getIndex();
	} 
	public int hashCode() { return value.hashCode(); }
	public String toString() { return "[tok "+getIndex()+":"+getValue()+"]"; }
}
