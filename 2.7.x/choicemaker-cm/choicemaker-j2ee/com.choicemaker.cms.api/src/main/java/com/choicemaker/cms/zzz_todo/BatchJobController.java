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
package com.choicemaker.cms.zzz_todo;

public interface BatchJobController {

	long INVALID_JOB_ID = Long.MIN_VALUE;

	String getBatchStatus(long jobID);

	DetailedBatchStatus getDetailedStatus(long jobID);

	/**
	 * This method attempts to suspend a job. Suspended jobs are recoverable
	 * 
	 * @param jobID
	 *            This is the unique job id created by the Choicemaker Batch
	 *            system.
	 * @return true means the attempt to abort was successful. false means the
	 *         attempt failed because the job is already done, already aborted,
	 *         or another error.
	 * 
	 */
	boolean suspendJob(long jobID);

	/**
	 * This method tries to resume a suspended job.
	 * 
	 * @param jobID
	 *            job id of the job you want to resume
	 * @return true means the attempt to abort was successful. false means the
	 *         attempt failed.
	 */
	boolean resumeJob(long jobID);

	/**
	 * This method attempts to abort a job.
	 * 
	 * @param jobID
	 *            This is the unique job id created by the Choicemaker Batch
	 *            system.
	 * @return true means the attempt to abort was successful. false means the
	 *         attempt failed.
	 * 
	 */
	boolean abortJob(long jobID);

}
