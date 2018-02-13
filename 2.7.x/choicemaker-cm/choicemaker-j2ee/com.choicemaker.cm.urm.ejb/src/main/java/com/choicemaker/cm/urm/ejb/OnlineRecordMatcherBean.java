/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.rmi.RemoteException;

import com.choicemaker.cm.urm.OnlineRecordMatcher;
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

public class OnlineRecordMatcherBean implements OnlineRecordMatcher {

	// private static final Logger log =
	// Logger.getLogger(OnlineRecordMatcherBean.class.getName());

	private static final String VERSION = "2.7.1";

	@Override
	public MatchScore evaluatePair(ISingleRecord<?> queryRecord,
			ISingleRecord<?> masterRecord, String modelName,
			float differThreshold, float matchThreshold, ScoreType resultFormat,
			String externalId)
			throws ModelException, ArgumentException, RecordException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public EvaluatedRecord[] getMatchCandidates(ISingleRecord<?> queryRecord,
			DbRecordCollection masterCollection, String modelName,
			float differThreshold, float matchThreshold, int maxNumMatches,
			EvalRecordFormat resultFormat, String externalId)
			throws ModelException, ArgumentException,
			UrmIncompleteBlockingSetsException, UrmUnderspecifiedQueryException,
			RecordException, RecordCollectionException, ConfigException,
			CmRuntimeException, RemoteException {
		throw new Error("not yet implemented");
	}

	@Override
	public String getVersion(Object context) throws RemoteException {
		return VERSION;
	}

}
