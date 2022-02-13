/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.DefaultServerConfigurationJPA.CN_HOSTNAME;
import static com.choicemaker.cm.oaba.ejb.DefaultServerConfigurationJPA.CN_SERVERCONFIG;
import static com.choicemaker.cm.oaba.ejb.DefaultServerConfigurationJPA.JPQL_DSC_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.DefaultServerConfigurationJPA.QN_DSC_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.DefaultServerConfigurationJPA.TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.oaba.api.DefaultServerConfiguration;

@NamedQuery(name = QN_DSC_FIND_ALL, query = JPQL_DSC_FIND_ALL)
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class DefaultServerConfigurationEntity
		implements DefaultServerConfiguration {

	@Id
	@Column(name = CN_HOSTNAME)
	private final String hostName;

	@Column(name = CN_SERVERCONFIG)
	private long serverConfigurationId;

	protected DefaultServerConfigurationEntity() {
		this.hostName = null;
		this.serverConfigurationId = -1;
	}

	public DefaultServerConfigurationEntity(String hostName,
			long serverConfigId) {
		if (hostName == null || hostName.trim().isEmpty()) {
			throw new IllegalArgumentException("null or blank host name");
		}
		if (serverConfigId == ServerConfigurationEntity.NON_PERSISTENT_ID) {
			throw new IllegalArgumentException("non-persistent configuration");
		}
		this.hostName = hostName.trim();
		this.serverConfigurationId = serverConfigId;
	}

	public DefaultServerConfigurationEntity(ServerConfiguration sc) {
		this(sc.getHostName(), sc.getId());
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public long getServerConfigurationId() {
		return serverConfigurationId;
	}

}
