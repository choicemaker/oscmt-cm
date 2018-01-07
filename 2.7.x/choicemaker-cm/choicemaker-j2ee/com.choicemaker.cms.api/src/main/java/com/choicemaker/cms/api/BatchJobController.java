/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.api;

/**
 * Allows a client application to match record collections and perform
 * transitivity analysis in a batch (asynchronous) mode.
 * 
 * @author emoussikaev (original URM)
 * @author rphall (URM2)
 */
public interface BatchJobController {

	long INVALID_JOB_ID = Long.MIN_VALUE;

	public String getBatchStatus(long jobID);

	public DetailedBatchStatus getDetailedBatchStatus(long jobID);

	/**
	 * This method attempts to suspend a job. Suspended jobs are recoverable
	 * 
	 * @param jobID
	 *            - This is the unique job id created by the Choicemaker Batch
	 *            system.
	 * @return 0 means attempt to abort was successful. -1 means cannot abort
	 *         either because the job is already done, already aborted, or
	 *         another error.
	 * 
	 */
	public int suspendJob(long jobID);

	/**
	 * This method tries to resume a suspended job.
	 * 
	 * @param jobID
	 *            - job id of the job you want to resume
	 * @return int = 1 if OK, or -1 if failed
	 */
	public int resumeJob(long jobID);

	/**
	 * This method attempts to abort a job.
	 * 
	 * @param jobID
	 *            - This is the unique job id created by the Choicemaker Batch
	 *            system.
	 * @return 0 means attempt to abort was successful. -1 means cannot abort
	 *         either because the job is already done, already aborted, or
	 *         another error.
	 * 
	 */
	public int abortJob(long jobID);

}
