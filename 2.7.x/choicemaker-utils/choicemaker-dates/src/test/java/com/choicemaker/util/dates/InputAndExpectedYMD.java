package com.choicemaker.util.dates;

public class InputAndExpectedYMD {
	
	public final String input;
	public final YearMonthDay expected;

	public InputAndExpectedYMD(String s, YearMonthDay ymd) {
		this.input = s;
		this.expected = ymd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + ((expected == null) ? 0 : expected.hashCode());
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
		InputAndExpectedYMD other = (InputAndExpectedYMD) obj;
		if (input == null) {
			if (other.input != null) {
				return false;
			}
		} else if (!input.equals(other.input)) {
			return false;
		}
		if (expected == null) {
			if (other.expected != null) {
				return false;
			}
		} else if (!expected.equals(other.expected)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "InputAndExpectedYMD [input=" + input + ", expected=" + expected
				+ "]";
	}

}
