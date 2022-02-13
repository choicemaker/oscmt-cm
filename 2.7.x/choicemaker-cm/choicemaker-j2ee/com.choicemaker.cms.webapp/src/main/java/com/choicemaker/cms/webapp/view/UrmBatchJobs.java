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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cms.api.UrmBatchController;
import com.choicemaker.cms.webapp.model.UrmBatchModel;

@Named
@RequestScoped
public class UrmBatchJobs {

	private Long activeJobId;
	private List<SelectItem> activeJobs;

	@Inject
	private UrmBatchController urmBatchController;

	protected static String buildActiveJobLabel(UrmBatchModel job, List<BatchJob> subJobs) {
		String retVal = "Job " + job.getId();
		return retVal;
	}

	@PostConstruct
	public void init() {
		this.activeJobs = new ArrayList<>();
		List<UrmBatchModel> jobs = getUrmBatchJobs();
		for (UrmBatchModel job : jobs) {
			BatchJobStatus status = BatchJobStatus.valueOf(job.getStatus());
			boolean isActive;
			switch (status) {
			case NEW: case QUEUED: case PROCESSING:  case ABORT_REQUESTED:
				isActive = true;
				break;
			default:
				isActive = false;
			}
			if (isActive) {
				List<BatchJob> subJobs = new ArrayList<>();
				String label = buildActiveJobLabel(job, subJobs);
				SelectItem si = new SelectItem(job.getId(), label);
				this.activeJobs.add(si);
			}
		}
	}

	public void abort() throws DatabaseException {
		if (activeJobId != null) {
			BatchJob job = urmBatchController.findBatchJob(activeJobId);
			if (job != null) {
				urmBatchController.abortBatchJob(job);
			}
		}
	}

	public List<UrmBatchModel> getUrmBatchJobs() {
		List<UrmBatchModel> retVal = new ArrayList<>();
		List<BatchJob> urmJobs = urmBatchController.findAllUrmJobs();
		for (BatchJob urmJob : urmJobs) {
			UrmBatchModel urmModel = new UrmBatchModel(urmJob);
			retVal.add(urmModel);
		}
		return retVal;
	}

	public Long getActiveJobId() {
		return activeJobId;
	}

	public void setActiveJobId(Long activeJobId) {
		this.activeJobId = activeJobId;
	}

	public List<SelectItem> getActiveJobs() {
		return activeJobs;
	}

	public void setActiveJobs(List<SelectItem> activeJobs) {
		this.activeJobs = activeJobs;
	}

}
