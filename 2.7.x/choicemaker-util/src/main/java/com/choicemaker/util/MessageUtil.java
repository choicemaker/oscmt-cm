package com.choicemaker.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * A utility class that formats user-visible text using a specified resource
 * bundle.
 */
public class MessageUtil {

	private static final Logger logger = Logger.getLogger(MessageUtil.class
			.getName());
	private static final Object[] ZERO_LENGTH_ARRAY = new Object[0];
	protected ResourceBundle myResources;

	public MessageUtil(String name) {
		myResources = ResourceBundle.getBundle(name);
	}

	public MessageUtil(ResourceBundle myResources) {
		this.myResources = myResources;
	}

	private String getMessageString(String messageKey) {
		try {
			return myResources.getString(messageKey);
		} catch (MissingResourceException ex) {
			logger.severe("missing resource in locale: " + Locale.getDefault()
					+ ": " + ex);
			return "Missing resource: " + messageKey;
		}
	}

	public String formatMessage(String messageKey) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		return mf.format(ZERO_LENGTH_ARRAY);
	}

	public String formatMessage(String messageKey, Object[] args) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		return mf.format(args);
	}

	public String formatMessage(String messageKey, Object arg0) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[1];
		args[0] = arg0;
		return mf.format(args);
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[2];
		args[0] = arg0;
		args[1] = arg1;
		return mf.format(args);
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Object arg2) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[3];
		args[0] = arg0;
		args[1] = arg1;
		args[2] = arg2;
		return mf.format(args);
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Object arg2, Object arg3) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[4];
		args[0] = arg0;
		args[1] = arg1;
		args[2] = arg2;
		args[3] = arg3;
		return mf.format(args);
	}

	public static final String ELLIPSIS = "...";

	public static final int ELLIPSIS_LENGTH = ELLIPSIS.length();

	/**
	 * Equivalent to:
	 * 
	 * <pre>
	 * elideString(s, startLength, 0, true)
	 * </pre>
	 * 
	 * @param s
	 *            a non-null String
	 * @param startLength
	 *            the initial length of the returned elision
	 * @return an elision of the trimmed string
	 */
	public String elideString(String s, int startLength) {
		return elideString(s, startLength, 0, true);
	}

	/**
	 * Equivalent to:
	 * 
	 * <pre>
	 * elideString(s, startLength, endLength, true)
	 * </pre>
	 * 
	 * @param s
	 *            a non-null String
	 * @param startLength
	 *            the initial length of the returned elision
	 * @param endLength
	 *            the final length of the returned elision
	 * @return an elision of the trimmed string
	 */
	public String elideString(String s, int startLength, int endLength) {
		return elideString(s, startLength, endLength, true);
	}

	/**
	 * Elides a string; that is, replaces a section of a string with an
	 * {@link #ELLIPSIS ellipsis (...)}. The returned elision depends on the
	 * length of the input string:
	 * <ul>
	 * <li>If the length of the input string is less than <code>startLength +
	 * endLength + ELLIPSIS_LENGTH</code>, the input string is returned without
	 * alteration.</li>
	 * <li>Otherwise, the returned elision consists of the first
	 * <code>startLength</code> characters of the input string, plus an
	 * ellipsis, plus the last <code>endLength</code> of the input string.
	 * </ul>
	 * 
	 * @param s
	 *            a non-null String
	 * @param startLength
	 *            the initial length of the returned elision, before the
	 *            ellipsis. The starting section of the elision is the
	 *            <code>startLength</code> first characters of the input string.
	 * @param endLength
	 *            the final length of the returned elision, after the ellipsis.
	 *            The final section of the elision is the <code>endLength</code>
	 *            last characters of the input string.
	 * @param trim
	 *            if true, the input string is first trimmed.
	 * @return an elision of the string. The length of the returned elision is
	 *         <code>min(s_length, startLength + endLength + ELLIPSIS_LENGTH)</code>
	 *         , where s_length is the length of input string after optional
	 *         trimming.
	 */
	public String elideString(String s, int startLength, int endLength,
			boolean trim) {
		Precondition.assertNonNullArgument("null string", s);
		Precondition.assertBoolean("negative start length", startLength > -1);
		Precondition.assertBoolean("negative start length", startLength > -1);
		String retVal;
		if (trim) {
			s = s.trim();
		}
		final int s_length = s.length();
		if (s_length <= startLength + endLength + ELLIPSIS_LENGTH) {
			retVal = s;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(s.substring(0, startLength));
			sb.append(ELLIPSIS);
			if (endLength > 0) {
				int endStart = s_length - endLength;
				String end = s.substring(endStart, s_length);
				sb.append(end);
			}
			retVal = sb.toString();
		}
		return retVal;
	}

	/**
	 * Creates a list of Strings where each element is a labeled component of an
	 * exception.
	 * 
	 * @param x
	 *            may be null (but return value will be empty)
	 * @return a non-null but possibly empty list
	 */
	public List<String> exceptionInfo(Exception x) {
		ExceptionInfo xinfo = x == null ? null : new ExceptionInfo(x);
		return exceptionInfo(xinfo);
	}

	/**
	 * Creates a list of Strings where each element is a labeled component of an
	 * exception.
	 * 
	 * @param xinfo
	 *            may be null (but return value will be empty)
	 * @return a non-null but possibly empty list
	 */
	public List<String> exceptionInfo(ExceptionInfo xinfo) {
		List<String> retVal;
		if (xinfo == null) {
			retVal = Collections.emptyList();
		} else {
			retVal = new ArrayList<>();
			retVal.add("Exception: " + xinfo.simpleClassName);
			if (!xinfo.message.isEmpty()) {
				retVal.add("Message: " + xinfo.message);
			}
			if (!xinfo.causeSimpleClassName.isEmpty()) {
				retVal.add("Cause: " + xinfo.causeSimpleClassName);
				if (!xinfo.causeMessage.isEmpty()) {
					retVal.add("Details: " + xinfo.causeMessage);
				}
			}
		}
		return retVal;
	}

}
