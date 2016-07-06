package com.choicemaker.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Gathers information from an exception
 */
public class ExceptionInfo {

	public final Exception x;
	public final String className;
	public final String simpleClassName;
	public final String message;
	public final String causeClassName;
	public final String causeSimpleClassName;
	public final String causeMessage;

	public ExceptionInfo(Exception x) {
		Precondition.assertNonNullArgument("null exception", x);
		this.x = x;
		className = x.getClass().getName();
		simpleClassName = x.getClass().getSimpleName();
		message = x.getMessage() == null ? "" : x.getMessage();
		final Throwable t = x.getCause();
		if (t != null) {
			causeClassName = t.getClass().getName();
			causeSimpleClassName = t.getClass().getSimpleName();
			causeMessage = t.getMessage() == null ? "" : t.getMessage();
		} else {
			causeClassName = "";
			causeSimpleClassName = "";
			causeMessage = "";
		}
	}
	
	public String getStackTrace() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		x.printStackTrace(pw);
		String retVal = sw.toString();
		return retVal;
	}

}