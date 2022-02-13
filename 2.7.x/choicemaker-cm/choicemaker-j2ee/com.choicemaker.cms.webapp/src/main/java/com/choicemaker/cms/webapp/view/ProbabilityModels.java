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

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.choicemaker.cms.api.NamedConfigurationController;

@Named
@RequestScoped
public class ProbabilityModels {

	private static final Logger logger =
		Logger.getLogger(ProbabilityModels.class.getName());

	private String modelName;
	private Long comparisonModelName;
//	private List<SelectItem> models;

	@Inject
	private NamedConfigurationController ncController;

	public List<String> getModelNames() {
		List<String> retVal = ncController.findAllModelConfigurationNames();
		return retVal;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Long getComparisonModelName() {
		return comparisonModelName;
	}

	public void setComparisonModelName(Long comparisonModelName) {
		this.comparisonModelName = comparisonModelName;
	}

}
