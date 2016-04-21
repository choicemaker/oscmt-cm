/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;


/**
 * This is the generic OABA exception object.

 * @author pcheung
 *
 */
public class BlockingException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public BlockingException () {
		super ();
	}

	public BlockingException (String message) {
		super (message);
	}

	public BlockingException (String message, Throwable t) {
		super (message, t);
	}

}
