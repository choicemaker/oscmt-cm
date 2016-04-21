/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

/**
 * Thrown by a constructor or modifier if an object would be incompletely
 * specified after construction or modification.
 * @author rphall
 * @since 2.5.206
 */
public class IncompleteSpecificationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncompleteSpecificationException() {
		super();
	}

	public IncompleteSpecificationException(String message) {
		super(message);
	}

	public IncompleteSpecificationException(Throwable cause) {
		super(cause);
	}

	public IncompleteSpecificationException(String message, Throwable cause) {
		super(message, cause);
	}

}
