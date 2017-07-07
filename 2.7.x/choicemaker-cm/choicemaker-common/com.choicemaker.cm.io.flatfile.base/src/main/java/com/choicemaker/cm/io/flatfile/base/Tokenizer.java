/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base;

import static java.util.logging.Level.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.util.Precondition;

public class Tokenizer {

	private static final Logger logger =
		Logger.getLogger(Tokenizer.class.getName());

	private static String tagMsg(String tag, String msg) {
		String s = (tag == null ? "" : tag) + ": " + (msg == null ? "" : msg);
		return s;
	}

	private static void logFinest(String tag, String msg) {
		if (logger.isLoggable(FINEST))
			logger.finest(tagMsg(tag, msg));
	}

	private static void logWarning(String tag, String msg) {
		if (logger.isLoggable(WARNING))
			logger.warning(tagMsg(tag, msg));
	}

	private static void logSevere(String tag, String msg) {
		if (logger.isLoggable(SEVERE))
			logger.severe(tagMsg(tag, msg));
	}

	private boolean fixedWidth;
	private int tagWidth;
	private char separator;
	private boolean tagged;
	private BufferedReader reader;
	private String line;
	private int lineLength;
	public String tag;
	public int pos;
	private char[] buf = new char[8192];
	private boolean wasNull;

	/** Constructs an fixed-width tokenizer */
	public Tokenizer(BufferedReader reader) {
		this(reader, true, (char) 0, false, 0);
	}

	/** Constructs a char-separated tokenizer */
	public Tokenizer(BufferedReader reader, char separator) {
		this(reader, false, separator, false, 0);
	}

	/** Constructs a tagged, char-separated tokenizer */
	public Tokenizer(BufferedReader reader, char separator, boolean tagged,
			int tagWidth) {
		this(reader, false, separator, tagged, tagWidth);
	}

	/**
	 * Fully parameterized constructor. Note that specifying fixed-width and a
	 * non-zero separator will result in the separator being ignored. Also, the
	 * tagWidth is used only with fixed-width tokenization; when a token
	 * separator is specified, the tag is the first token in the line,
	 * regardless of the token length.
	 * 
	 * @param reader
	 *            non-null BufferedReader
	 * @param fixedWidth
	 *            flag indicating whether input consists of fixed-width tokens
	 *            (without char separators)
	 * @param separator
	 *            if not fixed width, the charactor that separates tokens
	 * @param tagged
	 *            indicates whether input lines are prefaced with a tag
	 * @param tagWidth
	 *            positive width for line tags, if tagged
	 */
	public Tokenizer(BufferedReader reader, boolean fixedWidth, char separator,
			boolean tagged, int tagWidth) {

		Precondition.assertNonNullArgument("null reader", reader);
		Precondition.assertBoolean("invalid tag width: " + tagWidth,
				tagWidth >= 0);

		if (separator != (char) 0 && fixedWidth) {
			logWarning("CONSTRUCTOR",
					"separator '" + separator + "' will be ignored");
		}

		this.reader = reader;
		this.fixedWidth = fixedWidth;
		this.separator = separator;
		this.tagged = tagged;
		this.tagWidth = tagWidth;
	}

	/**
	 * Uses an internal reader to read a line. If a line is available, updates
	 * the current line held by this instance. If this instance has been
	 * constructed to handle tagged lines, updates the current tag held by this
	 * instance. Returns a flag indicating whether a line was available for
	 * reading.
	 * 
	 * @return true if a line was available for reading, false otherwise.
	 * @throws IllegalStateException
	 *             If an instance is constructed to read tagged lines, and it
	 *             encounters a line without a tag, then an
	 *             IllegalStateException will be thrown.
	 * @throws IOException
	 *             An IOException may be thrown by the internal reader when it
	 *             tries to read a line from the input.
	 */
	public boolean readLine() throws IOException {
		final String METHOD = "readline()";
		line = reader.readLine();
		logFinest(METHOD, "line = " + (line == null ? null : "'" + line + "'"));
		pos = 0;
		boolean retVal;
		if (line == null) {
			logFinest(METHOD, "null line");
			wasNull = true;
			retVal = false;
			tag = null;
		} else {
			lineLength = line.length();
			logFinest(METHOD, "non-null line, length = " + lineLength);
			wasNull = false;
			retVal = true;
			assert tagged || tag == null ;
			if (tagged) {
				// Line must have a tag
				String s = nextTrimmedString(tagWidth);
				if (s != null) {
					tag = s.intern();
					logFinest(METHOD, "tag = '" + tag + "'");
				} else {
					String msg = "untagged line: '" + line + "'";
					logSevere(METHOD, msg);
					throw new IllegalStateException(msg);
				}
			}
			assert retVal == true;
		}
		assert retVal == (line != null) ;
		assert (line ==null) || (tagged == (tag != null));
		return retVal;
	}

	@Deprecated
	public boolean lineRead() {
		return isLineAvailable();
	}

	public boolean isLineAvailable() {
		return line != null;
	}

	public boolean ready() throws IOException {
		return reader.ready();
	}

	public boolean wasNull() {
		return wasNull;
	}

	/**
	 * Skip characters in the current line until the {@code n}th separator has
	 * been found or the line is exhausted. This method does nothing for
	 * fixed-width input, or if the current line is null.
	 * 
	 * @param n
	 *            number of separators to find
	 */
	public void skip(int n) {
		if (!fixedWidth && line != null) {
			while (pos < lineLength) {
				char c = line.charAt(pos);
				++pos;
				if (c == separator && --n == 0) {
					break;
				}
			}
		}
	}

	public String nextInternedString(int width) {
		String s = nextString(width);
		return s != null ? s.intern() : null;
	}

	public String getInernedString(int start, int width) {
		String s = getString(start, width);
		return s != null ? s.intern() : null;
	}

	public String nextInternedTrimedString(int width) {
		String s = nextString(width);
		return s != null ? s.trim().intern() : null;
	}

	public String getInernedTrimedString(int start, int width) {
		String s = getString(start, width);
		return s != null ? s.trim().intern() : null;
	}

	@Deprecated
	public String nextTrimedString(int width) {
		String s = nextString(width);
		return s != null ? s.trim() : null;
	}

	public String nextTrimmedString(int width) {
		String s = nextString(width);
		return s != null ? s.trim() : null;
	}

	@Deprecated
	public String getTrimedString(int start, int width) {
		return getTrimmedString(start, width);
	}

	public String getTrimmedString(int start, int width) {
		String s = getString(start, width);
		return s != null ? s.trim() : null;
	}

	public String nextString(int width) {
		if (line == null)
			return null;
		if (fixedWidth) {
			if (pos >= lineLength) {
				wasNull = true;
				return null;
			} else {
				pos += width;
				wasNull = false;
				return line.substring(pos - width, pos);
			}
		} else {
			if (pos >= lineLength) {
				wasNull = true;
				return null;
			} else {
				int bpos = 0;
				char c;
				while (pos < lineLength
						&& (c = line.charAt(pos)) != separator) {
					buf[bpos++] = c;
					++pos;
				}
				++pos;
				wasNull = false;
				return new String(buf, 0, bpos);
			}
		}
	}

	public String getString(int start, int width) {
		if (fixedWidth) {
			if (start < lineLength) {
				wasNull = false;
				if (start + width <= lineLength) {
					return line.substring(start, start + width);
				} else {
					return line.substring(start);
				}
			} else {
				wasNull = true;
				return null;
			}
		} else {
			return nextString(width);
		}
	}

	public char nextChar(int width, char nullRepresentation) {
		String s = nextString(width);
		if (s != null && s.length() > 0) {
			char c = s.charAt(0);
			if (c == nullRepresentation) {
				return '\0';
			} else {
				return c;
			}
		} else {
			return '\0';
		}
	}

	public char getChar(int start, int width, char nullRepresentation) {
		if (start < lineLength) {
			char c = line.charAt(start);
			if (c == nullRepresentation) {
				return '\0';
			} else {
				return c;
			}
		} else {
			return '\0';
		}
	}

	public int nextInt(int width) {
		String s = nextTrimmedString(width);
		return s != null && s.length() > 0 ? Integer.parseInt(s) : 0;
	}

	public int getInt(int start, int width) {
		String s = getTrimmedString(start, width);
		return s != null && s.length() > 0 ? Integer.parseInt(s) : 0;
	}

	public long nextLong(int width) {
		String s = nextTrimmedString(width);
		return s != null && s.length() > 0 ? Long.parseLong(s) : 0L;
	}

	public long getLong(int start, int width) {
		String s = getTrimmedString(start, width);
		return s != null && s.length() > 0 ? Long.parseLong(s) : 0L;
	}

	public float nextFloat(int width) {
		String s = nextTrimmedString(width);
		return s != null && s.length() > 0 ? Float.parseFloat(s) : 0L;
	}

	public float getFloat(int start, int width) {
		String s = getTrimmedString(start, width);
		return s != null && s.length() > 0 ? Float.parseFloat(s) : 0L;
	}

	public Date nextDate(int width) {
		String s = nextTrimmedString(width);
		return s != null && s.length() > 0 ? DateHelper.parse(s) : null;
	}

	public Date getDate(int start, int width) {
		String s = getTrimmedString(start, width);
		return s != null && s.length() > 0 ? DateHelper.parse(s) : null;
	}
}
