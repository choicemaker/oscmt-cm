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
package com.choicemaker.cm.transitivity.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class TransitivityProcessControllerBean implements ProcessController {

	@EJB
	TransitivityJobManager transitivityJobManager;

	@Override
	public void abortBatchJob(BatchJob batchJob) {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertBoolean("not an OABA Job entity",
				batchJob instanceof TransitivityJobEntity);
		batchJob.markAsAbortRequested();
		batchJob.markAsAborted();
		transitivityJobManager.save(batchJob);
	}

	@Override
	public void restartBatchJob(BatchJob batchJob) {
		throw new Error("not yet implemented");
	}

}
