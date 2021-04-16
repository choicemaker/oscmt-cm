package com.choicemaker.cms.rest;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.webapp.model.NamedConfigurationBean;

@Path("configuration/{id}")
public class NamedConfigurationResource {

	@EJB
	private NamedConfigurationController ncController;

	@GET
	@Produces("application/json") 
	public NamedConfigurationBean getNamedConfiguration(@PathParam("id") long id) {
		NamedConfiguration nc = ncController.findNamedConfiguration(id);
		NamedConfigurationBean retVal = new NamedConfigurationBean(nc);
		return retVal;
	}

}

