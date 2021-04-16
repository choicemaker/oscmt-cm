package com.choicemaker.cm.validation.eclipse.impl;

import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

public class LabelQualifierKey {

	public static final String EMPTY_VALUE_REPLACEMENT = null;

	public final String label;
	public final String qualifier;
	public final String key;

	public static String computeKey(String label, String qualifier) {
		return "[" + label + "/" + qualifier + "]";
	}

	public LabelQualifierKey(String l, String q) {
		Precondition.assertBoolean(
				"The label must be non-null or "
						+ "the label and quqlifier must both be null",
				(l != null) || (l == null && q == null));

		if (StringUtils.nonEmptyString(l)) {
			this.label = l;
		} else {
			this.label = EMPTY_VALUE_REPLACEMENT;
		}

		if (StringUtils.nonEmptyString(q)) {
			this.qualifier = q;
		} else {
			this.qualifier = EMPTY_VALUE_REPLACEMENT;
		}

		this.key = computeKey(this.label, this.qualifier);
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
