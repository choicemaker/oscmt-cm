package com.choicemaker.cm.validation.eclipse.impl;

import com.choicemaker.util.Precondition;

public class LabelQualifierKey {

	public final String label;
	public final String qualifier;
	public final String key;

	public static String computeKey(String label, String qualifier) {
		return "[" + label + "/" + qualifier + "]";
	}

	public LabelQualifierKey(String label, String qualifier) {
		Precondition.assertBoolean(
				"The label must be non-null or "
						+ "the label and quqlifier must both be null",
				(label != null) || (label == null && qualifier == null));

		this.label = label == null ? null : label.trim();
		this.qualifier = qualifier == null ? null : qualifier.trim();
		this.key = computeKey(label, qualifier);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelQualifierKey other = (LabelQualifierKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabelQualifierKey " + key;
	}

}
