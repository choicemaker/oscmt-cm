package com.choicemaker.util;

public class InputAndExpectedString {
	public final String input;
	public final String expected;

	public InputAndExpectedString(String i, String e) {
		this.input = i;
		this.expected = e;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((expected == null) ? 0 : expected.hashCode());
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InputAndExpectedString other = (InputAndExpectedString) obj;
		if (expected == null) {
			if (other.expected != null) {
				return false;
			}
		} else if (!expected.equals(other.expected)) {
			return false;
		}
		if (input == null) {
			if (other.input != null) {
				return false;
			}
		} else if (!input.equals(other.input)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "InputAndExpectedString [input=" + input + ", expected="
				+ expected + "]";
	}

}