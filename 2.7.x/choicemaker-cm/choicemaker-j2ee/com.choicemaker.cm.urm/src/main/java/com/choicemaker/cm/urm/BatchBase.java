/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm;

import java.rmi.RemoteException;

import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;

/**
 * 
 * Common operations of the batch match and batch analysis session beans. Contains
 * functionality related to the job management such as checking status, abort,
 * resume, etc.
 * 
 * @author emoussikaev
 */
interface BatchBase {

	/**
	 * Aborts the job with the given job ID.
	 * 
	 * @param jobID
	 *            Job ID.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public boolean abortJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	/**
	 * Suspends the job with the given job ID.
	 * 
	 * @param jobID
	 *            Job ID.
	 * 
	 * @return true if job is aborted; false if job is already completed,
	 *         aborted or failed.
	 * @throws ArgumentException
	 * @throws ConfigException
	 * @throws CmRuntimeException
	 * @throws RemoteException
	 */
	public boolean suspendJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	/**
	 * Resumes the job with the given job ID.
	 * 
	 * @param jobID
	 *            Job ID.
	 * 
	 * @return
	 * @throws ArgumentException
	 * @throws ConfigException
	 * @throws CmRuntimeException
	 * @throws RemoteException
	 */
	public boolean resumeJob(long jobID)
	// throws ArgumentException,
	// ConfigException,
	// CmRuntimeException,
	// RemoteException
	;

	/**
	 * Cleans serialized data related to the give job ID (including the file
	 * with the matching results).
	 * 
	 * @param jobID
	 *            Job ID.
	 * @return
	 * @throws ArgumentException
	 * @throws ConfigException
	 * @throws CmRuntimeException
	 * @throws RemoteException
	 */
	public boolean cleanJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	/**
	 * Retrieves the status of the job with the given job ID.
	 * 
	 * @param jobID
	 *            Job ID.
	 * @return Job status.
	 * @throws ArgumentException
	 * @throws ConfigException
	 * @throws CmRuntimeException
	 * @throws RemoteException
	 */
	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	/**
	 * Returns the version of the interface implementation.
	 * <p>
	 * 
	 * @param context
	 *            reserved
	 * @return version
	 * @throws RemoteException
	 */

	public String getVersion(Object context) throws RemoteException;

}
