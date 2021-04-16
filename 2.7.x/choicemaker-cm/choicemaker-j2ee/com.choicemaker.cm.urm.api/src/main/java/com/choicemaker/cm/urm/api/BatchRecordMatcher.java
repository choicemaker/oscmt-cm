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
import java.util.List;

import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

public interface BatchRecordMatcher extends BatchBase {

	long[] getJobList() throws ArgumentException, ConfigException,
			CmRuntimeException, RemoteException;

	Iterator<?> getResultIter(long jobId) throws RecordCollectionException,
			ArgumentException, CmRuntimeException, RemoteException;

	Iterator<?> getResultIter(RefRecordCollection rc)
			throws RecordCollectionException, ArgumentException,
			CmRuntimeException, RemoteException;

	@Override
	String getVersion(Object context) throws RemoteException;

	long startMatching(IRecordCollection qRc, RefRecordCollection mRc,
			String modelName, float differThreshold, float matchThreshold,
			int maxSingle, String trackingId)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException;

	List<String> getResultFileNames(long jobID) throws CmRuntimeException;

}
