/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;


/**
 * Thrown when a profile is invalid, e.g., wrong XML representation.
 *
 * @author   Martin Buechi
 */
public class InvalidProfileException extends Exception {

	private static final long serialVersionUID = 2L;

	/**
	 * Constructs an <code>InvalidProfileException</code> with no detail message.
	 */
	public InvalidProfileException() {
		super();
	}

	/**
	 * Constructs a <code>InvalidProfileException</code> with the specified detail message.
	 *
	 * @param   message  The detail message.
	 */
	public InvalidProfileException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>InvalidProfileException</code> with the specified detail message and cause.
	 *
	 * @param   message  The detail message.
	 * @param   cause  The cause.
	 */
	public InvalidProfileException(String message, Throwable cause) {
		super(message, cause);
	}
}
