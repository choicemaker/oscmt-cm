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
 * Interface for SoftTFIDFDictionary and the rescoring variant of it.
 * 
 */

public interface FastLookup
{
    /** Lookup items similar to 'toFind', and return the number of
     * items found.  The found items must have a similarity score
     * greater than minScore to 'toFind'.
     */

    public int lookup(double minScore,String toFind);
    
    /** Get the i'th string found by the last lookup */
    public String getResult(int i);

    /** Get the value of the i'th string found by the last lookup */
    public Object getValue(int i);

    /** Get the score of the i'th string found by the last lookup */
    public double getScore(int i);

}
