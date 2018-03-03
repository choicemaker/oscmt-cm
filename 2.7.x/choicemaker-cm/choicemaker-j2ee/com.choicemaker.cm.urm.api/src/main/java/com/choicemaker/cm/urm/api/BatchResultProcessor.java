/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;

import com.choicemaker.cm.args.IFilterConfiguration;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

public interface BatchResultProcessor extends BatchBase {

	long[] getMrpsGenerationJobList() throws ArgumentException, ConfigException,
			CmRuntimeException, RemoteException;

	long[] getResultCopyJobList() throws ArgumentException, ConfigException,
			CmRuntimeException, RemoteException;

	long startMrpsGeneration(long processedJobId, String mrpsUrl,
			String filterConfName, IFilterConfiguration overrideFilterConfig,
			String trackingId)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException;

	long startResultCopy(long processedJobId, RefRecordCollection resRc,
			String trackingId)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException;

}
