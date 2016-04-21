/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

/**
 * Thrown when a database access problem prohibits ChoiceMaker from fulfilling a request. 
 *
 * @author   Martin Buechi
 */
public class DatabaseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a <code>DatabaseException</code> with no detail message.
	 */
	public DatabaseException() { }

	/**
	 * Constructs a <code>DatabaseException</code> with the specified detail message.
	 * 
	 * @param   message  The detail message.
	 */
	public DatabaseException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>DatabaseException</code> with the specified detail message and cause.
	 * 
	 * @param   message  The detail message.
	 * @param   cause  The cause.
	 */
	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
