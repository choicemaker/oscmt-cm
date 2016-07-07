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
