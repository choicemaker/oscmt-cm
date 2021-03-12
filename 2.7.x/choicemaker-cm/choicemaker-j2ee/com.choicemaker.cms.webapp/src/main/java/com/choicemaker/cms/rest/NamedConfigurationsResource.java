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
