/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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

	public static final int DEFAULT_MAX_LINE_LENGTH = 100;
	public static final String DEFAULT_REPLACEMENT_TEXT =
		"See the application log";

	public static final String ELLIPSIS = "...";
	public static final int ELLIPSIS_LENGTH = ELLIPSIS.length();

	public static final String EXCEPTION_SEP = ": ";

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
					+ EXCEPTION_SEP + ex);
			return "Missing resource: " + messageKey;
		}
	}

	public String formatMessage(String messageKey) {
		return formatMessage(messageKey, (Throwable) null);
	}

	public String formatMessage(String messageKey, Throwable t) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		return t == null ? mf.format(ZERO_LENGTH_ARRAY) : mf
				.format(ZERO_LENGTH_ARRAY)
				+ EXCEPTION_SEP
				+ new ExceptionInfo(t).toString();
	}

	public String formatMessage(String messageKey, Object[] args) {
		return formatMessage(messageKey, args, (Throwable) null);
	}

	public String formatMessage(String messageKey, Object[] args, Throwable t) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		return t == null ? mf.format(args) : mf.format(args) + EXCEPTION_SEP
				+ new ExceptionInfo(t).toString();
	}

	public String formatMessage(String messageKey, Object arg0) {
		return formatMessage(messageKey, arg0, (Throwable) null);
	}

	public String formatMessage(String messageKey, Object arg0, Throwable t) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[1];
		args[0] = arg0;
		return t == null ? mf.format(args) : mf.format(args) + EXCEPTION_SEP
				+ new ExceptionInfo(t).toString();
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1) {
		return formatMessage(messageKey, arg0, arg1, (Throwable) null);
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Throwable t) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[2];
		args[0] = arg0;
		args[1] = arg1;
		return t == null ? mf.format(args) : mf.format(args) + EXCEPTION_SEP
				+ new ExceptionInfo(t).toString();
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Object arg2) {
		return formatMessage(messageKey, arg0, arg1, arg2, (Throwable) null);
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Object arg2, Throwable t) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[3];
		args[0] = arg0;
		args[1] = arg1;
		args[2] = arg2;
		return t == null ? mf.format(args) : mf.format(args) + EXCEPTION_SEP
				+ new ExceptionInfo(t).toString();
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Object arg2, Object arg3) {
		return formatMessage(messageKey, arg0, arg1, arg2, arg3,
				(Throwable) null);
	}

	public String formatMessage(String messageKey, Object arg0, Object arg1,
			Object arg2, Object arg3, Throwable t) {
		MessageFormat mf = new MessageFormat(getMessageString(messageKey));
		Object[] args = new Object[4];
		args[0] = arg0;
		args[1] = arg1;
		args[2] = arg2;
		args[3] = arg3;
		return t == null ? mf.format(args) : mf.format(args) + EXCEPTION_SEP
				+ new ExceptionInfo(t).toString();
	}

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
	public static String elideString(String s, int startLength) {
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
	 *            any string
	 * @param startLength
	 *            the initial length of the returned elision
	 * @param endLength
	 *            the final length of the returned elision
	 * @return an elision of the trimmed string, or null if the input is null
	 */
	public static String elideString(String s, int startLength, int endLength) {
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
	 *            any string
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
	 * @return null if the input is null, otherwise an elision of the string.
	 *         The length of the returned elision is
	 *         <code>min(s_length, startLength + endLength + ELLIPSIS_LENGTH)</code>
	 *         , where s_length is the length of input string after optional
	 *         trimming.
	 */
	public static String elideString(String s, int startLength, int endLength,
			boolean trim) {
		String retVal = null;
		if (s != null) {
			Precondition.assertBoolean("negative start length",
					startLength > -1);
			Precondition.assertBoolean("negative start length",
					startLength > -1);
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
		}
		return retVal;
	}

	/**
	 * Elide a file name. This method looks for the first file separator in a
	 * file name such that the elided path, starting at the separator, is less
	 * than the specified limit. If no such file separator exists -- in other
	 * words, if the base file name is too long -- then base file name is
	 * returned as the elided value.
	 * 
	 * @param fileName
	 *            any file name
	 * @param endLength
	 *            the maximum length of a file path
	 * @return null if the input is null, otherwise an elided substring of the
	 *         file name broken at some file separator if possible, or the
	 *         entire base file name if base name is too long
	 */
	public static String elideFileName(String fileName, int endLength) {
		String retVal = null;
		if (fileName != null
				&& fileName.length() <= endLength + ELLIPSIS_LENGTH) {
			retVal = fileName;
		} else if (fileName != null) {
			String candidate = elideString(fileName, 0, endLength);
			int firstIdx =
				candidate.indexOf(SystemPropertyUtils.PV_FILE_SEPARATOR);
			if (firstIdx == -1) {
				int lastIdx =
					fileName.lastIndexOf(SystemPropertyUtils.PV_FILE_SEPARATOR);
				if (lastIdx == -1) {
					retVal = fileName;
				} else {
					retVal = ELLIPSIS + fileName.substring(lastIdx);
				}
			} else {
				retVal = ELLIPSIS + candidate.substring(firstIdx);
			}
		}
		return retVal;
	}

	/**
	 * Creates a list of Strings where each element is a labeled component of an
	 * exception. Equivalent to
	 * 
	 * <pre>
	 * exceptionInfo(x, DEFAULT_MAX_LINE_LENGTH)
	 * </pre>
	 * 
	 * @param x
	 *            may be null (but return value will be empty)
	 * @return a non-null but possibly empty list
	 */
	public static List<String> exceptionInfo(Throwable x) {
		ExceptionInfo xinfo = x == null ? null : new ExceptionInfo(x);
		return exceptionInfo(xinfo, DEFAULT_MAX_LINE_LENGTH);
	}

	/**
	 * Creates a list of Strings where each element is a labeled component of an
	 * exception.
	 * 
	 * @param x
	 *            may be null (but return value will be empty)
	 * @param maxLineLen
	 *            the maximum length of a message element before it is replaced
	 *            with {@link #DEFAULT_REPLACEMENT_TEXT default text}
	 * @return a non-null but possibly empty list
	 */
	public static List<String> exceptionInfo(Throwable x, int maxLineLen) {
		ExceptionInfo xinfo = x == null ? null : new ExceptionInfo(x);
		return exceptionInfo(xinfo, maxLineLen);
	}

	/**
	 * Creates a list of Strings where each element is a labeled component of an
	 * exception. Equivalent to
	 * 
	 * <pre>
	 * exceptionInfo(xinfo, DEFAULT_MAX_LINE_LENGTH)
	 * </pre>
	 * 
	 * @param xinfo
	 *            may be null (but return value will be empty)
	 * @return a non-null but possibly empty list
	 */
	public static List<String> exceptionInfo(ExceptionInfo xinfo) {
		return exceptionInfo(xinfo, DEFAULT_MAX_LINE_LENGTH);
	}

	/**
	 * Creates a list of Strings where each element is a labeled component of an
	 * exception.
	 * 
	 * @param xinfo
	 *            may be null (but return value will be empty)
	 * @param maxLineLen
	 *            the maximum length of a message element before it is replaced
	 *            with {@link #DEFAULT_REPLACEMENT_TEXT default text}
	 * @return a non-null but possibly empty list
	 */
	public static List<String> exceptionInfo(ExceptionInfo xinfo, int maxLineLen) {
		List<String> retVal;
		if (xinfo == null) {
			retVal = Collections.emptyList();
		} else {
			retVal = new ArrayList<>();
			retVal.add("Exception: " + xinfo.simpleClassName);
			if (!xinfo.message.isEmpty()) {
				retVal.add("Message: "
						+ conditionalText(xinfo.message, maxLineLen));
			}
			if (!xinfo.causeSimpleClassName.isEmpty()) {
				retVal.add("Cause: " + xinfo.causeSimpleClassName);
				if (!xinfo.causeMessage.isEmpty()) {
					retVal.add("Details: "
							+ conditionalText(xinfo.causeMessage, maxLineLen));
				}
			}
		}
		return retVal;
	}

	protected static String conditionalText(String s, int len) {
		String retVal = s;
		if (s != null && len > 0 && s.length() > len) {
			retVal = DEFAULT_REPLACEMENT_TEXT;
		}
		return retVal;
	}

}
