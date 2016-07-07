package com.choicemaker.cm.io.xml.base;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.util.ExceptionInfo;

/**
 * An exception that occurs while building or transforming an XML document,
 * separate from any IO or security related errors. This class is meant to
 * essentially roll up various types of errors related XML processing into one
 * exception condition:
 * <ul>
 * <li>javax.xml.transform.TransformerException</li>
 * <li>org.xml.sax.SAXException</li>
 * <li>javax.xml.parsers.ParserConfigurationException</li>
 * <li>com.choicemaker.cm.core.XmlConfException</li>
 * </ul>
 * This class explicitly does <em><strong>NOT</strong></em> roll up XML
 * security exceptions. See the XmlEncDiagnosticException class for that
 * functionality.
 * <ul>
 * <li><em><strong>NOT</strong></em>&nbsp;org.apache.xml.security.exceptions.XMLSecurityException</li>
 * </ul>
 * 
 * @author rphall
 *
 */
public class XmlDiagnosticException extends Exception {

	private static final long serialVersionUID = 271L;

	protected static String createXmlDiagnostic(String message, Exception cause) {
		ExceptionInfo xinfo = new ExceptionInfo(cause);
		return xinfo.toString(message);
	}

	private final String xmlDiagnostic;

	public XmlDiagnosticException() {
		this((String) null, (String) null);
	}

	public XmlDiagnosticException(String message) {
		this(message, (String) null);
	}

	public XmlDiagnosticException(String message, String xmlDiagnostic) {
		super(message);
		this.xmlDiagnostic = xmlDiagnostic;
	}

	protected XmlDiagnosticException(String message, String xmlDiagnostic, Exception cause) {
		super(message, cause);
		this.xmlDiagnostic = xmlDiagnostic;
	}

	public XmlDiagnosticException(String message, XmlConfException cause) {
		this(message, createXmlDiagnostic(message, cause), cause);
	}

	public XmlDiagnosticException(String message,
			ParserConfigurationException cause) {
		this(message, createXmlDiagnostic(message, cause), cause);
	}

	public XmlDiagnosticException(String message, SAXException cause) {
		this(message, createXmlDiagnostic(message, cause), cause);
	}

	public XmlDiagnosticException(String message, TransformerException cause) {
		this(message, createXmlDiagnostic(message, cause), cause);
	}

	public XmlDiagnosticException(String message, Throwable cause) {
		// Do not create xmlDiagnostic information from a general condition
		// unrelated to XML processing
		super(message, cause);
		this.xmlDiagnostic = null;
	}

	public String getXmlDiagnostic() {
		return xmlDiagnostic;
	}

}
