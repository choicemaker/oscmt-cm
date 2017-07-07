/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base;

import java.io.IOException;
import java.io.Writer;

import com.choicemaker.cm.core.util.DateHelper;

/**
 * Description
 *
 * @author    Martin Buechi
 */

public class FlatFileOutput {
	public static final int MAX_LINE_LENGTH = 16384;
	public static final char[] SPACE_BUF = new char[MAX_LINE_LENGTH];
	static {
		for (int i = 0; i < SPACE_BUF.length; i++) {
			SPACE_BUF[i] = ' ';
		}
	}

	public static void write(
		Writer w,
		String str,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int len)
		throws IOException {

		char [] s;
		if (str == null) {
			s = new char[0];
		} else {
			s = str.toCharArray();

			//remove line characters
			int size = s.length;
			for (int i=0; i<size; i++) {
				if (s[i] == '\n' || s[i] == '\r') s[i] = ' ';
			}
		}

		if (fixedLength) {
			int l = s.length;
			if (l == len) {
				w.write(s);
			} else if (l < len) {
				w.write(s);
				w.write(SPACE_BUF, 0, len - l);
			} else {
				w.write(s, 0, len);
			}
		} else {
			if (!first) {
				w.write(sep);
			}
			if (filter) {
				w.write(remove(s, sep));
			} else {
				w.write(s);
			}
		}
	}

	public static void write(
		Writer w,
		char[] lineBuffer,
		String s,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int start,
		int len)
		throws IOException {
		if (fixedLength) {
			if (s == null) {
				s = "";
			}
			s.getChars(0, Math.min(len, s.length()), lineBuffer, start);
		} else {
			write(w, s, fixedLength, sep, filter, first, len);
		}
	}

	public static void write(
		Writer w,
		java.util.Date val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int len)
		throws IOException {
		write(
			w,
			val == null ? "" : DateHelper.format(val),
			fixedLength,
			sep,
			filter,
			first,
			len);
	}

	public static void write(
		Writer w,
		char[] lineBuffer,
		java.util.Date val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int start,
		int len)
		throws IOException {
		write(
			w,
			lineBuffer,
			val == null ? "" : DateHelper.format(val),
			fixedLength,
			sep,
			filter,
			first,
			start,
			len);
	}

	public static void write(
		Writer w,
		Object val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int len)
		throws IOException {
		write(
			w,
			val == null ? "" : val.toString(),
			fixedLength,
			sep,
			filter,
			first,
			len);
	}

	public static void write(
		Writer w,
		char[] lineBuffer,
		Object val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int start,
		int len)
		throws IOException {
		write(
			w,
			lineBuffer,
			val == null ? "" : val.toString(),
			fixedLength,
			sep,
			filter,
			first,
			start,
			len);
	}

	public static void write(
		Writer w,
		char val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int len,
		char nullRepresentation)
		throws IOException {
		String s;
		if (val == '\0') {
			if (fixedLength) {
				s = String.valueOf(nullRepresentation);
			} else {
				s = "";
			}

		} else {
			s = String.valueOf(val);
		}
		write(w, s, fixedLength, sep, filter, first, len);
	}

	public static void write(
		Writer w,
		char[] lineBuffer,
		char val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int start,
		int len,
		char nullRepresentation)
		throws IOException {
		String s;
		if (val == '\0') {
			if (fixedLength) {
				s = String.valueOf(nullRepresentation);
			} else {
				s = "";
			}

		} else {
			s = String.valueOf(val);
		}
		write(w, lineBuffer, s, fixedLength, sep, filter, first, start, len);
	}

	public static void write(
		Writer w,
		long val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int len)
		throws IOException {
		write(w, String.valueOf(val), fixedLength, sep, filter, first, len);
	}

	public static void write(
		Writer w,
		char[] lineBuffer,
		long val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int start,
		int len)
		throws IOException {
		write(
			w,
			lineBuffer,
			String.valueOf(val),
			fixedLength,
			sep,
			filter,
			first,
			start,
			len);
	}

	public static void write(
		Writer w,
		double val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int len)
		throws IOException {
		write(w, String.valueOf(val), fixedLength, sep, filter, first, len);
	}

	public static void write(
		Writer w,
		char[] lineBuffer,
		double val,
		boolean fixedLength,
		char sep,
		boolean filter,
		boolean first,
		int start,
		int len)
		throws IOException {
		write(
			w,
			lineBuffer,
			String.valueOf(val),
			fixedLength,
			sep,
			filter,
			first,
			start,
			len);
	}

	public static String remove(String s, char sep) {
		String retVal;
		if (s == null) {
			retVal = null;
		} else {
			char[] c = s.toCharArray();
			char[] res = remove(c,sep);
			retVal = new String(res);
		}
		return retVal;
	}


	/** This method removes all instances of character sep from char array s.
	 * 
	 * @param s - array of character to filter
	 * @param sep - the character to remove.
	 * @return a new char array that could be shorter
	 */
	public static char [] remove(char [] s, char sep) {
		final int len = s.length;
		int count = 0;
		for (int i=0 ; i < len; i++) {
			if (s[i] != sep) {
				count ++;
			}
		}
		if (count == len) {
			return s;
		} else {
			char[] res = new char[count];
			count = 0;
			for (int i = 0; i < len; i++) {
				if (s[i] != sep) res[count++] = s[i];
			}
			return res;
		}
	}

}
