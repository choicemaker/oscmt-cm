/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;

import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

interface BatchBase {

	public void copyResult(long jobID, RefRecordCollection resRc)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException;

	public boolean abortJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	public boolean cleanJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	public String getVersion(Object context) throws RemoteException;

	public boolean resumeJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

	public boolean suspendJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException;

}
