/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.urm.api.BatchMatchAnalyzer;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.NamedConfigurationController;

/**
 * Delegates to CMS BatchMatching bean.
 */
@Stateless
@Remote(BatchMatchAnalyzer.class )
public class BatchMatchAnalyzerBean implements BatchMatchAnalyzer {

	private static final String VERSION = "2.7.1";

	private static final Logger logger =
		Logger.getLogger(BatchMatchAnalyzerBean.class.getName());

	@EJB(lookup = "java:app/com.choicemaker.cms.ejb/BatchMatchingBean!com.choicemaker.cms.api.BatchMatching")
	private BatchMatching delegate;

	@EJB(lookup = "java:module/UrmConfigurationSingleton")
	private UrmConfigurationAdapter adapter;

	@EJB(lookup = "java:app/com.choicemaker.cms.ejb/NamedConfigurationControllerBean!com.choicemaker.cms.api.NamedConfigurationController")
	private NamedConfigurationController ncController;

	@Override
	public boolean abortJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		boolean delegateRetVal = delegate.abortJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.abortJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public boolean cleanJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		boolean delegateRetVal = delegate.cleanJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.cleanJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public Iterator<?> getResultIterator(long jobId, AnalysisResultFormat s)
			throws RecordCollectionException, ArgumentException,
			CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public Iterator<?> getResultIterator(RefRecordCollection rc,
			AnalysisResultFormat s) throws RecordCollectionException,
			ArgumentException, CmRuntimeException, RemoteException {
		throw new Error("never implemented");
	}

	@Override
	public String getVersion(Object context) throws RemoteException {
		return VERSION;
	}

	@Override
	public boolean resumeJob(long jobID) {
		boolean delegateRetVal = delegate.resumeJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.resumeJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

	@Override
	public long startAnalysis(long jobId, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId)
			throws ModelException, ConfigException, ArgumentException,
			CmRuntimeException, RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long startMatchAndAnalysis(IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxSingle, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId)
			throws RecordCollectionException, ArgumentException,
			ConfigException, ModelException, CmRuntimeException,
			RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean suspendJob(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		boolean delegateRetVal = delegate.suspendJob(jobID);
		if (logger.isLoggable(Level.FINE)) {
			String msg = "BatchMatchingBean.suspendJob: " + delegateRetVal;
			logger.fine(msg);
		}
		return delegateRetVal;
	}

}
