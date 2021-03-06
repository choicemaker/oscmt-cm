/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.configure.xml;

/**
 * Indicates a Configurable object can not be recreated
 * from an XML specification.
 * @author rphall
 */
public class XmlSpecificationException extends Exception {

	private static final long serialVersionUID = 1L;

	public XmlSpecificationException() {
		super();
	}

	public XmlSpecificationException(String message) {
		super(message);
	}

	public XmlSpecificationException(Throwable cause) {
		super(cause);
	}

	public XmlSpecificationException(String message, Throwable cause) {
		super(message, cause);
	}

}
