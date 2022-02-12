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
package com.wcohen.ss.lookup;

/**
 * Shared code for SoftTFIDFDictionary and the rescoring variant of it.
 * 
 */

/*package-visible*/ class LookupResult implements Comparable
{
    private static final java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.000");

    String found; // a string 'looked up' in a dictionary
    Object value; // the value associated with that string
    double score; // the score of the match between the looked-up string and 'found'

    public LookupResult(String found,Object value,double score) 
    {
        this.found=found; this.value=value; this.score=score; 
    }

    public int compareTo(Object obj) {
			if ( !(obj instanceof LookupResult) ) {
				throw new ClassCastException("incompatible type");
			}
    		LookupResult o = (LookupResult) obj;
        double diff = o.score - score;
        return diff<0 ? -1 : (diff>0?+1:0);
    }

    public String toString() { return "["+fmt.format(score)+" "+found+"=>"+value+"]"; }
}
