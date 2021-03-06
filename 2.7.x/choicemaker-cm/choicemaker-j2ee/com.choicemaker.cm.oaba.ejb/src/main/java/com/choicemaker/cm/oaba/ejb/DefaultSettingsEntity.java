/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.CN_SETTINGS_ID;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.JPQL_DSET_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.JPQL_DSET_FIND_ALL_ABA;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.JPQL_DSET_FIND_ALL_OABA;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.QN_DSET_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.QN_DSET_FIND_ALL_ABA;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.QN_DSET_FIND_ALL_OABA;
import static com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA.TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.choicemaker.cm.oaba.api.DefaultSettings;

@NamedQueries({
		@NamedQuery(name = QN_DSET_FIND_ALL, query = JPQL_DSET_FIND_ALL),
		@NamedQuery(name = QN_DSET_FIND_ALL_ABA,
				query = JPQL_DSET_FIND_ALL_ABA),
		@NamedQuery(name = QN_DSET_FIND_ALL_OABA,
				query = JPQL_DSET_FIND_ALL_OABA) })
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class DefaultSettingsEntity implements DefaultSettings {

	@EmbeddedId
	private DefaultSettingsPKBean key;

	@Column(name = CN_SETTINGS_ID)
	private long settingsId;

	protected DefaultSettingsEntity() {
	}

	public DefaultSettingsEntity(String m, String t, String d, String b,
			long settingsId) {
		this(new DefaultSettingsPKBean(m, t, d, b), settingsId);
	}

	public DefaultSettingsEntity(DefaultSettingsPKBean pk, long settingsId) {
		if (pk == null || settingsId == 0) {
			throw new IllegalArgumentException("invalid argument");
		}
		this.key = pk;
		this.settingsId = settingsId;
	}

	@Override
	public DefaultSettingsPKBean getPrimaryKey() {
		return key;
	}

	@Override
	public String getModel() {
		return key.getModel();
	}

	@Override
	public String getType() {
		return key.getType();
	}

	@Override
	public String getDatabaseConfiguration() {
		return key.getDatabaseConfiguration();
	}

	@Override
	public String getBlockingConfiguration() {
		return key.getBlockingConfiguration();
	}

	@Override
	public long getSettingsId() {
		return settingsId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + (int) (settingsId ^ (settingsId >>> 32));
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
		DefaultSettingsEntity other = (DefaultSettingsEntity) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (settingsId != other.settingsId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DefaultSettings [" + key + ", settingsId=" + settingsId + "]";
	}

}
