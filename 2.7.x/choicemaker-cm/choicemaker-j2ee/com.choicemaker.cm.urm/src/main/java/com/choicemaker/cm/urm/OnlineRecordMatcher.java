/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.EvalRecordFormat;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.MatchScore;
import com.choicemaker.cm.urm.base.ScoreType;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cm.urm.exceptions.RecordException;
import com.choicemaker.cm.urm.exceptions.UrmIncompleteBlockingSetsException;
import com.choicemaker.cm.urm.exceptions.UrmUnderspecifiedQueryException;

public interface OnlineRecordMatcher<T extends Comparable<T> & Serializable> {

	MatchScore evaluatePair(ISingleRecord<T> queryRecord,
			ISingleRecord<T> masterRecord, String modelName,
			float differThreshold, float matchThreshold, ScoreType resultFormat,
			String externalId)
			throws ModelException, ArgumentException, RecordException,
			ConfigException, CmRuntimeException, RemoteException;

	EvaluatedRecord[] getMatchCandidates(ISingleRecord<T> queryRecord,
			DbRecordCollection mRc, String modelName, float differThreshold,
			float matchThreshold, int maxNumMatches,
			EvalRecordFormat resultFormat, String externalId)
			throws ModelException, ArgumentException,
			UrmIncompleteBlockingSetsException, UrmUnderspecifiedQueryException,
			RecordException, RecordCollectionException, ConfigException,
			CmRuntimeException, RemoteException;

	String getVersion(Object context) throws RemoteException;

}
