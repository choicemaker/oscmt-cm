/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.EvalRecordFormat;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.LinkCriteria;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cm.urm.exceptions.RecordException;
import com.choicemaker.cm.urm.exceptions.UrmIncompleteBlockingSetsException;
import com.choicemaker.cm.urm.exceptions.UrmUnderspecifiedQueryException;

public interface OnlineMatchAnalyzer<T extends Comparable<T> & Serializable> {

	EvaluatedRecord[] getCompositeMatchCandidates(ISingleRecord<T> queryRecord,
			DbRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxNumMatches, LinkCriteria c,
			EvalRecordFormat resultFormat, String externalId)
			throws ArgumentException, ModelException,
			UrmIncompleteBlockingSetsException, UrmUnderspecifiedQueryException,
			RecordException, RecordCollectionException, ConfigException,
			CmRuntimeException, RemoteException;

	String getVersion(Object context) throws RemoteException;

}
