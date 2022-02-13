/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cms.api.BatchJobQueries;
import com.choicemaker.cms.api.UrmJobManager;
import com.choicemaker.cms.api.remote.BatchJobQueriesRemote;

@Stateless
@Local(BatchJobQueries.class)
@Remote(BatchJobQueriesRemote.class)
public class BatchJobQueriesBean implements BatchJobQueriesRemote {

	// private static final String SOURCE_CLASS =
	// BatchJobQueriesBean.class.getSimpleName();
	//
	// private static final Logger logger =
	// Logger.getLogger(BatchJobQueriesBean.class.getName());

	@EJB
	private UrmJobManager urmJobManager;

	@EJB
	private TransitivityJobManager transitivityJobManager;

	@EJB
	private OabaJobManager oabaJobManager;

	// Top-level URM jobs

	@Override
	public List<BatchJob> findAllUrmJobs() {
		return urmJobManager.findAllUrmJobs();
	}

	@Override
	public BatchJob findUrmJob(long id) {
		return urmJobManager.findUrmJob(id);
	}

	@Override
	public List<BatchJob> findAllBatchJobsLinkedByUrmId(long urmJobId) {
		return urmJobManager.findAllLinkedByUrmId(urmJobId);
	}

	// Transitivity jobs

	@Override
	public List<BatchJob> findAllTransitivityJobs() {
		return transitivityJobManager.findAllTransitivityJobs();
	}

	@Override
	public BatchJob findTransitivityJob(long id) {
		return transitivityJobManager.findTransitivityJob(id);
	}

	@Override
	public List<BatchJob> findAllTransitivityJobsByOabaJobId(long oabaJobId) {
		return transitivityJobManager.findAllByOabaJobId(oabaJobId);
	}

	// OABA jobs

	@Override
	public List<BatchJob> findAllOabaJobs() {
		return oabaJobManager.findAllOabaJobs();
	}

	@Override
	public BatchJob findOabaJob(long id) {
		return oabaJobManager.findOabaJob(id);
	}

	// General batch jobs (URM, Transitivity, OABA, etc)

	@Override
	public List<BatchJob> findAll() {
		return urmJobManager.findAll();
	}

	@Override
	public BatchJob findBatchJob(long id) {
		return urmJobManager.findBatchJob(id);
	}

}
