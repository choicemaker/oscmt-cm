/*******************************************************************************
 * Copyright (c) 2014, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.base;

import org.apache.xml.security.exceptions.XMLSecurityException;

import com.choicemaker.cm.io.xml.base.XmlDiagnosticException;

public class XmlEncDiagnosticException extends XmlDiagnosticException {

	private static final long serialVersionUID = 271L;

	public XmlEncDiagnosticException() {
	}

	public XmlEncDiagnosticException(String message) {
		super(message);
	}

	public XmlEncDiagnosticException(String message, XMLSecurityException cause) {
		super(message, createXmlDiagnostic(message,cause), cause);
	}

	public XmlEncDiagnosticException(String message, Throwable cause) {
		super(message, cause);
	}

}
