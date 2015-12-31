/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.exceptions;

/**
 * Thrown by a finder method if some specified object can not
 * be found. This exception class serves an identical purpose to
 * {@link  com.choicemaker.cm.core.configure.NotFoundException NotFoundException}.
 * It is defined separately from <code>NotFoundException</code>
 * in order to keep the URM interface independent of the core package.
 * @author rphall
 * @since 2.5.206
 * @see com.choicemaker.cm.core.configure.NotFoundException
 */
public class UrmNotFoundException extends Exception {

	static final long serialVersionUID = -5345486590788459519L;

	public UrmNotFoundException() {
		super();
	}

	public UrmNotFoundException(String message) {
		super(message);
	}

	public UrmNotFoundException(Throwable cause) {
		super(cause);
	}

	public UrmNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
