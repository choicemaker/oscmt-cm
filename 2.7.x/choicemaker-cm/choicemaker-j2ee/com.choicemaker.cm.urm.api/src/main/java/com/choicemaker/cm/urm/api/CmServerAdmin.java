/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;

import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

public interface CmServerAdmin {

	String getVersion(Object context) throws RemoteException;

	void updateAllDerivedFields(String probabilityModel, DbRecordCollection rc)
			throws ArgumentException, RecordCollectionException,
			ConfigException, ModelException, CmRuntimeException,
			RemoteException;

	void updateCounts(String probabilityMode, String urlString)
			throws ArgumentException, RecordCollectionException,
			ConfigException, ModelException, CmRuntimeException,
			RemoteException;

	void updateDerivedFields(String probabilityModel, DbRecordCollection rc)
			throws ArgumentException, RecordCollectionException,
			ConfigException, ModelException, CmRuntimeException,
			RemoteException;

}
