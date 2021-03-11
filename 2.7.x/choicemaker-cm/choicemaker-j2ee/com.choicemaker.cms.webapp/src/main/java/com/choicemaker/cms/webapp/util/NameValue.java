package com.choicemaker.cms.webapp.util;

import com.choicemaker.util.Precondition;

public final class NameValue {

	private final String name;
	private final String value;

	public NameValue(String pn, String pv) {
		Precondition.assertNonEmptyString("invalid name", pn);
		this.name = pn;
		this.value = pv;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result =
			prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
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
		NameValue other = (NameValue) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		if (getValue() == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (!getValue().equals(other.getValue())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NameValue [" + getName() + ", " + getValue() + "]";
	}

}
