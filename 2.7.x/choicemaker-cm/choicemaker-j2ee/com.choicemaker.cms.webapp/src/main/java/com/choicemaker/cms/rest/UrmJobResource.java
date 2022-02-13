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

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.webapp.model.UrmBatchModel;

@Path("urmjob/{id}")
public class UrmJobResource {

	 @EJB
	 private UrmBatchController ubc;

	@GET
	@Produces("application/json") 
	public UrmBatchModel getUrmJob(@PathParam("id") long id) {
		BatchJob urmJob = ubc.findUrmJob(id);
		UrmBatchModel retVal = new UrmBatchModel(urmJob);
		return retVal;
	}

}

