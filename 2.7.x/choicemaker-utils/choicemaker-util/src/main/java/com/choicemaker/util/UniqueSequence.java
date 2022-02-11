/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This singleton object generates a unique sequence.
 * 
 * @author pcheung
 *
 */
public class UniqueSequence {

	private AtomicInteger num = new AtomicInteger(1000);
	private static AtomicReference<UniqueSequence> seq =
		new AtomicReference<>();

	private UniqueSequence() {
	}

	public static UniqueSequence getInstance() {
		UniqueSequence retVal = seq.get();
		if (retVal == null) {
			seq.compareAndSet(null, new UniqueSequence());
			retVal = seq.get();
			assert retVal != null;
		}
		return retVal;
	}

	public synchronized int getNext() {
		return num.incrementAndGet();
	}

	public synchronized Integer getNextInteger() {
		return new Integer(num.incrementAndGet());
	}

}
