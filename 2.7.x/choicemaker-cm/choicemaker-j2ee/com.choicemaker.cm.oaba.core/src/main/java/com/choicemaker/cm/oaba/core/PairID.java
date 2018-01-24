/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.util.LongArrayList;

/**
 * This object stores two long record id's.
 * 
 * @author pcheung
 * 
 *
 */
public class PairID implements Comparable<PairID>, IIDSet {

	private long id1, id2;

	public PairID(long l1, long l2) {
		id1 = l1;
		id2 = l2;
	}

	public long getID1() {
		return id1;
	}

	public long getID2() {
		return id2;
	}

	@Override
	public int compareTo(PairID p) {
		int ret = 0;

		if (id1 < p.id1)
			ret = -1;
		else if (id1 > p.id1)
			ret = 1;
		else if (id1 == p.id1) {
			if (id2 < p.id2)
				ret = -1;
			else if (id2 > p.id2)
				ret = 1;
			else if (id2 == p.id2)
				ret = 0;
		}
		return ret;
	}

	public boolean equals(PairID p) {
		boolean ret = false;

		if (p != null) {
			if ((id1 == p.id1) && (id2 == p.id2))
				ret = true;
		}

		return ret;
	}

	@Override
	public int hashCode() {
		return (int) (id1 ^ (id1 >>> 32) ^ id2 ^ (id2 >>> 32));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IIDSet#getRecordIDs
	 * ()
	 */
	@Override
	public LongArrayList getRecordIDs() {
		LongArrayList list = new LongArrayList(2);
		list.add(id1);
		list.add(id2);
		return list;
	}

}
