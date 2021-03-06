/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;


/**
 * Thrown if a service that uses IControl can not be stopped cleanly.
 * @see IControl
 * @author rphall
 */
public class ControlException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ControlException () {
		super ();
	}

	public ControlException (String message) {
		super (message);
	}

}
