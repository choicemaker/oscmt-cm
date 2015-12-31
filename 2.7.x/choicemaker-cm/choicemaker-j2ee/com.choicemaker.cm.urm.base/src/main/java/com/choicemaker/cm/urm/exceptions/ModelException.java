/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.exceptions;

/**
 * * Signals that a problem related to a matching probability model has occurred.
 * 
 * @author emoussikaev
 * @see
 */
public class ModelException extends Exception {

	static final long serialVersionUID = 754593021341037243L;

	/**
	 * Constructs a <code>ModelException</code>
	 * <p> 
	 * 
	 */
	public ModelException() {
		super();
	}

	/**
	 * Constructs a <code>ModelException</code>with the specified detail message.
	 * <p> 
	 * @param message
	 */
	public ModelException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>ModelException</code> with the specified cause.
	 * <p> 
	 * @param cause
	 */
	public ModelException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a <code>ModelException</code> with the specified detail message and cause.
	 * <p> 
	 * @param message
	 * @param cause
	 */
	public ModelException(String message, Throwable cause) {
		super(message, cause);
	}

}
