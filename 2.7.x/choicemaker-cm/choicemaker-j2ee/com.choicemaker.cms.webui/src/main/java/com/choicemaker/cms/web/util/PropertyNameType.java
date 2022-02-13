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
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.web.util;

import java.util.logging.Logger;

/**
 * @deprecated see com.choicemaker.cms.webapp.util.PropertyNameType
 */
@Deprecated
public final class PropertyNameType {

	private static final Logger logger =
		Logger.getLogger(PropertyNameType.class.getName());

	public final String name;
	public Class<?> type;

	public PropertyNameType(String pn, Class<?> pt) {
		if (pn == null || !pn.equals(pn.trim()) || pn.isEmpty()) {
			String msg = "invalid property name '" + pn + "'";
			NamedConfigPresentation.logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		if (pt == null) {
			String msg = "null property type";
			NamedConfigPresentation.logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		this.name = pn;
		this.type = pt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PropertyNameType [" + name + ", " + type + "]";
	}

}
