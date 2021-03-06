/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;



/**
 * XML configuration exception.
 *
 * @author    Martin Buechi
 */
public class XmlConfException extends Exception {
	private static final long serialVersionUID = 2L;

	/**
	 * Constructs a <code>XmlConfException</code> with <code>s</code> as reason.
	 *
	 * @param   s  The reason the exception is thrown.
	 */
	public XmlConfException(String s) {
		super(s);
	}

	/**
	 * Constructor from another exception.
	 *
	 * @param   ex  The exception to be nested.
	 */
	public XmlConfException(String s, Throwable ex) {
		super(s, ex);
	}

}
