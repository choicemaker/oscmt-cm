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
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.util;

public class EnumUtils {

	/**
	 * Finds the value of the given enumeration by name, case-insensitive.
	 * Throws an IllegalArgumentException if no match is found.
	 * <p>
	 * Based on contribution by Patrick Arnesen, 2016-12-01, StackOverflow,
	 * "Look up enum by string value", http://links.rph.cx/2uyTSpM
	 * @param <T> the enum type
	 * @param enumeration the enum class
	 * @param name a String representation of an enum member
	 * @return an enum member
	 */
	public static <T extends Enum<T>> T valueOfIgnoreCase(Class<T> enumeration,
			String name) {

		for (T enumValue : enumeration.getEnumConstants()) {
			if (enumValue.name().equalsIgnoreCase(name)) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException(
				String.format("There is no value with name '%s' in Enum %s",
						name, enumeration.getName()));
	}

}
