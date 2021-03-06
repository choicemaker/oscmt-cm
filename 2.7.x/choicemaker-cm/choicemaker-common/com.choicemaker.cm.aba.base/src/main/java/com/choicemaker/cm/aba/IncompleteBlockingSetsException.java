/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba;

/**
 * This exception should be thrown if the Blocker can only form an incomplete
 * collection of blocking sets; i.e. a collection for which there exists some
 * BlockingValue (and its bases) that does not belong to any BlockingSet in the
 * collection. This exception extends UnderspecifiedQueryException because this
 * error is caused by an under-specified query.
 */
public class IncompleteBlockingSetsException
		extends UnderspecifiedQueryException {

	private static final long serialVersionUID = 1L;

	public IncompleteBlockingSetsException() {
	}

	public IncompleteBlockingSetsException(String message) {
		super(message);
	}

}
