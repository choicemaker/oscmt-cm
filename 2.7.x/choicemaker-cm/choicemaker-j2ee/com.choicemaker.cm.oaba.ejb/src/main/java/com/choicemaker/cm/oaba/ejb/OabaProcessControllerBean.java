/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.oaba.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class OabaProcessControllerBean implements ProcessController {

	@EJB
	OabaJobManager oabaJobManager;

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertBoolean("not an OABA Job entity",
				batchJob instanceof OabaJobEntity);
		batchJob.markAsAbortRequested();
		batchJob.markAsAborted();
		oabaJobManager.save(batchJob);
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		throw new Error("not yet implemented");
	}

}
