/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

/**
 * Thrown when the profile is not specific enough to perform first-pass matching (blocking). E.g., when
 * specifying only the gender in querying a large database.
 *
 * @author   Martin Buechi
 */
public class UnderspecifiedProfileException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>UnderspecifiedProfileException</code> with no detail message.
	 */
	public UnderspecifiedProfileException() { }

	/**
	 * Constructs a <code>UnderspecifiedProfileException</code> with the specified detail message.
	 * 
	 * @param   message  The detail message.
	 */
	public UnderspecifiedProfileException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>UnderspecifiedProfileException</code> with the specified detail message and cause.
	 * 
	 * @param   message  The detail message.
	 * @param   cause  The cause.
	 */
	public UnderspecifiedProfileException(String message, Throwable cause) {
		super(message, cause);
	}

}
