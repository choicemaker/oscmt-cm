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
