package com.choicemaker.cms.webapp.util;

import java.util.logging.Logger;

public final class PropertyNameType {

	private static final Logger logger =
		Logger.getLogger(PropertyNameType.class.getName());

	private final String name;
	private final Class<?> type;

	public PropertyNameType(String pn, Class<?> pt) {
		if (pn == null || !pn.equals(pn.trim()) || pn.isEmpty()) {
			String msg = "invalid property name '" + pn + "'";
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		if (pt == null) {
			String msg = "null property type";
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		this.name = pn;
		this.type = pt;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
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
		PropertyNameType other = (PropertyNameType) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		if (getType() == null) {
			if (other.getType() != null) {
				return false;
			}
		} else if (!getType().equals(other.getType())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PropertyNameType [" + getName() + ", " + getType() + "]";
	}

}
