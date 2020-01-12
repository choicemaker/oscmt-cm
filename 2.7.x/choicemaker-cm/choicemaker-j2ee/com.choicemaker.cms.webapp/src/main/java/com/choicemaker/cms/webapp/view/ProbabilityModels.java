package com.choicemaker.cms.webapp.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.choicemaker.cms.api.NamedConfigurationController;

@Named
@RequestScoped
public class ProbabilityModels {

	@Inject
	private NamedConfigurationController ncController;

	public List<String> getModelNames() {
		List<String> retVal = ncController.findAllModelConfigurationNames();
		return retVal;
	}

}
