package com.choicemaker.cms.webapp.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;

@Named
@RequestScoped
public class BatchConfigurations {
	
	private Long namedConfigurationId;

	@Inject
	private NamedConfigurationController ncController;

	public List<NamedConfiguration> getBatchConfigurations() {
		List<NamedConfiguration> retVal = ncController.findAllNamedConfigurations();
		return retVal;
	}

	public Long getNamedConfigurationId() {
		return namedConfigurationId;
	}

	public void setNamedConfigurationId(Long namedConfigurationId) {
		this.namedConfigurationId = namedConfigurationId;
	}

}
