/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;
import java.util.Iterator;

import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.batch.api.BatchJobRigor;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

public interface BatchMatchAnalyzer extends BatchBase {

	public Iterator<?> getResultIterator(long jobId, AnalysisResultFormat s)
			throws RecordCollectionException, ArgumentException,
			CmRuntimeException, RemoteException;

	public Iterator<?> getResultIterator(RefRecordCollection rc,
			AnalysisResultFormat s) throws RecordCollectionException,
			ArgumentException, CmRuntimeException, RemoteException;

	@Override
	public String getVersion(Object context) throws RemoteException;

	public long startAnalysis(long jobId, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId)
			throws ModelException, ConfigException, ArgumentException,
			CmRuntimeException, RemoteException;

	long startAnalysis(long jobId, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId,
			BatchJobRigor rigor) throws ModelException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException;

	public long startMatchAndAnalysis(IRecordCollection qRc,
			RefRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxSingle, LinkCriteria c,
			AnalysisResultFormat serializationFormat, String trackingId)
			throws RecordCollectionException, ArgumentException,
			ConfigException, ModelException, CmRuntimeException,
			RemoteException;

}
