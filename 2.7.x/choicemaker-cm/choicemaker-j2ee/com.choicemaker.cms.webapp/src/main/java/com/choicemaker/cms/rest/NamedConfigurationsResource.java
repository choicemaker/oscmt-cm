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
package com.choicemaker.cms.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.webapp.model.NamedConfigurationBean;

@Path("configurations")
public class NamedConfigurationsResource {

	@EJB
	private NamedConfigurationController ncController;

	@GET
	@Produces("application/json")
	public List<NamedConfigurationBean> getNamedConfigurations() {
		List<NamedConfigurationBean> retVal = new ArrayList<>();
		List<NamedConfiguration> ncs = ncController.findAllNamedConfigurations();
		for (NamedConfiguration nc : ncs) {
			NamedConfigurationBean ncb = new NamedConfigurationBean(nc);
			retVal.add(ncb);
		}
		return retVal;
	}

}
