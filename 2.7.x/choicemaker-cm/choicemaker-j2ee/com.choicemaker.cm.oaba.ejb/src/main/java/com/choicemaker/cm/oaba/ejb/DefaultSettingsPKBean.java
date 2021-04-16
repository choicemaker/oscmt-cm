/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.CN_BLOCKING_CONFIGURATION;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.CN_DATABASE_CONFIGURATION;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.CN_MODEL;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.CN_TYPE;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.choicemaker.cm.oaba.api.DefaultSettingsPK;

@Embeddable
public class DefaultSettingsPKBean implements DefaultSettingsPK {
	@Column(name = CN_MODEL, nullable = false)
	private String model;
	@Column(name = CN_TYPE, nullable = false)
	private String type;
	@Column(name = CN_DATABASE_CONFIGURATION, nullable = false)
	private String databaseConfiguration;
	@Column(name = CN_BLOCKING_CONFIGURATION, nullable = false)
	private String blockingConfiguration;

	protected DefaultSettingsPKBean() {
	}

	/**
	 * Constructs a primary key for an entry in the defaults table
	 * 
	 * @param m
	 *            a non-null modelId
	 * @param t
	 *            a valid discriminator value; see for example
	 *            {@link AbaSettingsJPA#DISCRIMINATOR_VALUE} or
	 *            {@link OabaSettingsJPA#DISCRIMINATOR_VALUE}
	 * @param d
	 *            a valid database configuration name, as specified by the
	 *            modelId schema
	 * @param b
	 *            a valid blocking configuration name, as specified by the
	 *            modelId schema
	 */
	public DefaultSettingsPKBean(String m, String t, String d, String b) {
		if (m == null || t == null || d == null || b == null) {
			throw new IllegalArgumentException("null argument");
		}
		m = m.trim().toUpperCase();
		t = t.trim().toUpperCase();
		d = d.trim().toUpperCase();
		b = b.trim().toUpperCase();
		if (m.isEmpty() || t.isEmpty() || d.isEmpty() || b.isEmpty()) {
			throw new IllegalArgumentException("blank argument");
		}
		this.model = m;
		this.type = t;
		this.databaseConfiguration = d;
		this.blockingConfiguration = b;
	}

	@Override
	public String getModel() {
		return model;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getDatabaseConfiguration() {
		return databaseConfiguration;
	}

	@Override
	public String getBlockingConfiguration() {
		return blockingConfiguration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockingConfiguration == null) ? 0
				: blockingConfiguration.hashCode());
		result = prime * result + ((databaseConfiguration == null) ? 0
				: databaseConfiguration.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
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
		DefaultSettingsPKBean other = (DefaultSettingsPKBean) obj;
		if (blockingConfiguration == null) {
			if (other.blockingConfiguration != null) {
				return false;
			}
		} else if (!blockingConfiguration.equals(other.blockingConfiguration)) {
			return false;
		}
		if (databaseConfiguration == null) {
			if (other.databaseConfiguration != null) {
				return false;
			}
		} else if (!databaseConfiguration.equals(other.databaseConfiguration)) {
			return false;
		}
		if (model == null) {
			if (other.model != null) {
				return false;
			}
		} else if (!model.equals(other.model)) {
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
		return "DefaultSettingsPK [modelId=" + model + ", type=" + type
				+ ", dbConfig=" + databaseConfiguration + ", blkConfig="
				+ blockingConfiguration + "]";
	}

}