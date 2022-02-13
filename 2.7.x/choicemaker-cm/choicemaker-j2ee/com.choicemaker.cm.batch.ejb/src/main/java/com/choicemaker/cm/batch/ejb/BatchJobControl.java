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
package com.choicemaker.cm.batch.ejb;

import java.util.logging.Logger;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.core.IControl;
import com.choicemaker.util.Precondition;

public class BatchJobControl implements IControl {

	private static final Logger logger =
		Logger.getLogger(BatchJobControl.class.getName());

	private static final boolean DEFAULT_STOP = true;

	private final BatchJobManager batchJobManager;
	private final long batchJobId;
	private final boolean defaultStop;

	public BatchJobControl(BatchJobManager batchJobManager, BatchJob batchJob) {
		this(batchJobManager, batchJob.getId(), DEFAULT_STOP);
	}

	public BatchJobControl(BatchJobManager batchJobManager, long batchJobId) {
		this(batchJobManager, batchJobId, DEFAULT_STOP);
	}

	public BatchJobControl(BatchJobManager batchJobManager, long batchJobId,
			boolean defaultStop) {
		Precondition.assertNonNullArgument("null batch job manager",
				batchJobManager);
		this.batchJobManager = batchJobManager;
		this.batchJobId = batchJobId;
		this.defaultStop = defaultStop;
	}

	@Override
	public boolean shouldStop() {
		boolean retVal = defaultStop;
		try {
			BatchJob batchJob = batchJobManager.findBatchJob(batchJobId);
			retVal = batchJob.stopProcessing();
		} catch (Exception x) {
			String msg = "Unable to check batch job: " + batchJobId;
			logger.severe(msg);
		}
		logger.fine("BatchJobControl.shouldStop(): " + retVal);
		return retVal;
	}

}
