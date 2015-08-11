/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.plugin;

public class IdVersionType {

	public static enum TYPE { plugin, fragment };

	public final String id;
	public final String version;
	public final TYPE type;

	@Override
	public String toString() {
		return "IdVersionType [id=" + id + ", version=" + version
				+ ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		IdVersionType other = (IdVersionType) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}

	public IdVersionType(String id, String version, TYPE type) {
		if (id == null) {
			throw new IllegalArgumentException("null id");
		}
		id = id.trim();
		if (id.isEmpty()) {
			throw new IllegalArgumentException("blank id");
		}
		if (version == null) {
			throw new IllegalArgumentException("null version");
		}
		version = version.trim();
		if (version.isEmpty()) {
			throw new IllegalArgumentException("blank version");
		}
		if (type == null) {
			throw new IllegalArgumentException("null type");
		}
		this.id = id;
		this.version = version;
		this.type = type;
	}
}