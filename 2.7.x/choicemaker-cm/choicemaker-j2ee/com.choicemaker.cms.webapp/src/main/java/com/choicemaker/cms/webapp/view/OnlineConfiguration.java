/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.webapp.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;

@Named
@RequestScoped
public class OnlineConfiguration {

	// private static final Logger logger =
	// Logger.getLogger(OnlineConfiguration.class.getName());

	private String dataSourceUrl;
	private List<SelectItem> dataSources;

	@Inject
	private NamedConfigurationController ncController;

	@Inject
	private AbaStatisticsController statsController;

	@PostConstruct
	public void init() {
		this.dataSources = new ArrayList<>();
		Set<String> urls = new HashSet<>();
		List<NamedConfiguration> ncs =
			ncController.findAllNamedConfigurations();
		for (NamedConfiguration nc : ncs) {
			String url = nc.getDataSource();
			if (url != null) {
				url = url.trim();
				if (url.length() > 0) {
					boolean isNew = urls.add(url);
					if (isNew) {
						SelectItem si = new SelectItem(url, url);
						this.dataSources.add(si);
					}
				}
			}
		}
	}

	public void submit() throws DatabaseException {
		statsController.updateReferenceStatistics(dataSourceUrl);
	}

	public String getDataSourceUrl() {
		return dataSourceUrl;
	}

	public void setDataSourceUrl(String dataSourceUrl) {
		this.dataSourceUrl = dataSourceUrl;
	}

	public List<SelectItem> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<SelectItem> items) {
		this.dataSources = items;
	}

}
