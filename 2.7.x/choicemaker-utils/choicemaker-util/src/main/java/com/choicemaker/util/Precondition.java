/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.util;

import java.util.logging.Logger;

/**
 * @author rphall
 */
public class Precondition {

	private static final Logger logger = Logger.getLogger(Precondition.class
			.getName());

	/**
	 * Default message about a false boolean argument. This message should move
	 * to a resource bundle.
	 */
	public static final String MSG_FALSE_BOOLEAN = "precondition violated";

	/**
	 * Default message about a null or blank string. This message should move to
	 * a resource bundle.
	 */
	public static final String MSG_NULL_OR_BLANK_STRING =
		"null or blank String value";

	/**
	 * Default message about invalid null method argument. This message should
	 * move to a resource bundle.
	 */
	public static final String MSG_NULL_OBJECT = "null argument";

	public static void assertBoolean(boolean b) {
		assertBoolean(MSG_FALSE_BOOLEAN, b);
	}

	public static void assertBoolean(String msg, boolean b) {
		if (!b) {
			msg = msg == null ? Precondition.MSG_FALSE_BOOLEAN : msg;
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * @param sut
	 *            String under test
	 */
	public static void assertNonEmptyString(String sut)
			throws IllegalArgumentException {
		assertNonEmptyString(Precondition.MSG_NULL_OR_BLANK_STRING, sut);
	}

	/**
	 * Confusing signature! Message is first, string under test is second.
	 * 
	 * @param msg
	 *            Message that will be logged if sut is null or blank
	 * @param sut
	 *            String under test
	 */
	public static void assertNonEmptyString(String msg, String sut)
			throws IllegalArgumentException {
		if (!StringUtils.nonEmptyString(sut)) {
			msg = msg == null ? Precondition.MSG_NULL_OR_BLANK_STRING : msg;
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	public static void assertNonNullArgument(Object o) {
		assertNonNullArgument(Precondition.MSG_NULL_OBJECT, o);
	}

	public static void assertNonNullArgument(String msg, Object o) {
		if (o == null) {
			msg = msg == null ? Precondition.MSG_NULL_OBJECT : msg;
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	private Precondition() {
	}

}
