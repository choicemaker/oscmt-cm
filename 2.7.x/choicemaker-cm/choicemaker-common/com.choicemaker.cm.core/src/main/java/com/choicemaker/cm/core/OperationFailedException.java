/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;


/**
 *
 * @author    S. Yoakum-Stover
 */
public class OperationFailedException extends Exception {
	private static final long serialVersionUID = 2L;

	/**
	 * Constructs a <code>OperationFailedException</code> with <code>s</code> as reason.
	 *
	 * @param   s  The reason the exception is thrown.
	 */
	public OperationFailedException(String s) {
		super(s);
	}

	/**
	 * Constructor from another exception.
	 *
	 * @param   ex  The exception ot be nested.
	 */
	public OperationFailedException(String s, Throwable ex) {
		super(s, ex);
	}
}
