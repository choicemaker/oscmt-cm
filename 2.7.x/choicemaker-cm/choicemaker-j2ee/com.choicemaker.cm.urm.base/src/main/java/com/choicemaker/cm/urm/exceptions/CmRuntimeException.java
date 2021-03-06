/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.exceptions;

/**
 * Signals that a ChoiceMaker server runtime problem has occurred.
 * 
 * @author emoussikaev
 * @see
 */
public class CmRuntimeException extends Exception {

	static final long serialVersionUID = 6967781293484629967L;

	/**
	 * Constructs a <code>CmRuntimeException</code>
	 * <p> 
	 * 
	 */
	public CmRuntimeException() {
		super();
	}

	/**
	 * Constructs a <code>CmRuntimeException</code> with the specified detail message.
	 * <p> 
	 * @param message
	 */
	public CmRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>CmRuntimeException</code> with the specified cause.
	 * <p> 
	 * @param cause
	 */
	public CmRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a <code>CmRuntimeException</code> with the specified detail message and cause.
	 * <p> 
	 * @param message
	 * @param cause
	 */
	public CmRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
