package com.choicemaker.util;


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

	public String toString(String context) {
		StringBuilder sb = new StringBuilder();
		if (context != null) {
			sb.append(context.trim());
		}
		if (sb.length() > 0) {
			sb.append(": ");
		}
		sb.append(this.toString());
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Exception " + simpleClassName);
		if (!message.isEmpty()) {
			sb.append(": " + message);
		}
		if (!causeSimpleClassName.isEmpty()) {
			sb.append(" (" + causeSimpleClassName);
			if (!causeMessage.isEmpty()) {
				sb.append(": " + causeMessage);
			}
			sb.append(")");
		}
		String retVal = sb.toString();
		return retVal;
	}

}