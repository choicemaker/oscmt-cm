package com.choicemaker.util;

public class EnumUtils {

	/**
	 * Finds the value of the given enumeration by name, case-insensitive.
	 * Throws an IllegalArgumentException if no match is found.
	 * <p>
	 * Based on contribution by Patrick Arnesen, 2016-12-01, StackOverflow,
	 * "Look up enum by string value", http://links.rph.cx/2uyTSpM
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
