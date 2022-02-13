/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import com.choicemaker.cm.args.IFilterConfiguration;
import com.choicemaker.cm.urm.api.BatchResultProcessor;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

public class BatchResultProcessorBean implements BatchResultProcessor {

	private static final String VERSION = "2.7.1";

	// REMOVEME
	static final int BATCH_MATCH_STEP_INDEX = 0;
	static final int TRANS_OABA_STEP_INDEX = 1;
	static final int TRANS_SERIAL_STEP_INDEX = 2;

	protected static Logger logger =
		Logger.getLogger(BatchResultProcessorBean.class.getName());

	public final static String JMS_MRPS_PROCESSOR_QUEUE =
		"java:comp/env/jms/mrpsProcessorQueue";

	@Override
	public boolean abortJob(long jobId) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public boolean cleanJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public void copyResult(long jobID, RefRecordCollection resRc)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemente");
	}

	@Override
	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getMrpsGenerationJobList() throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public long[] getResultCopyJobList() throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public String getVersion(Object context) throws RemoteException {
		return VERSION;
	}

	@Override
	public boolean resumeJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public long startMrpsGeneration(long processedJobId, String mrpsUrl,
			String filterConfName, IFilterConfiguration overrideFilterConfig,
			String trackingId)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public long startResultCopy(long processedJobId, RefRecordCollection resRc,
			String trackingId)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		throw new Error("not implemented");
	}

	@Override
	public boolean suspendJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

}
