/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Remote;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.urm.OnlineMatchAnalyzer;
import com.choicemaker.cm.urm.OnlineRecordMatcher;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.EvalRecordFormat;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.LinkCriteria;
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
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.OnlineMatching;
import com.choicemaker.cms.ejb.NamedConfigConversion;
import com.choicemaker.util.Precondition;

@Remote
public class OnlineUrmBean<T extends Comparable<T> & Serializable>
		implements OnlineRecordMatcher<T>, OnlineMatchAnalyzer<T> {

	private static final String VERSION = "2.7.1";

	private static final Logger logger =
		Logger.getLogger(OnlineUrmBean.class.getName());

	@EJB(lookup = "java:app/OnlineMatchingBean/com.choicemaker.cms.api.OnlineMatching")
	private OnlineMatching<T> delegate;

	private UrmEjbAssist<T> assist = new UrmEjbAssist<>();

	@Override
	public MatchScore evaluatePair(ISingleRecord<T> queryRecord,
			ISingleRecord<T> masterRecord, String modelName,
			float differThreshold, float matchThreshold, ScoreType resultFormat,
			String externalId)
			throws ModelException, ArgumentException, RecordException,
			ConfigException, CmRuntimeException, RemoteException {
		throw new Error("not implemented");
	}

	@Override
	public EvaluatedRecord[] getMatchCandidates(ISingleRecord<T> queryRecord,
			DbRecordCollection masterCollection, String modelName,
			float differThreshold, float matchThreshold, int maxNumMatches,
			EvalRecordFormat resultFormat, String externalId)
			throws ModelException, ArgumentException,
			UrmIncompleteBlockingSetsException, UrmUnderspecifiedQueryException,
			RecordException, RecordCollectionException, ConfigException,
			CmRuntimeException, RemoteException {

		Precondition.assertNonNullArgument("null query", queryRecord);
		Precondition.assertNonNullArgument("null references", masterCollection);
		Precondition.assertNonEmptyString("null or empty model", modelName);
		Precondition.assertBoolean("invalid differ threshold",
				differThreshold >= 0f && differThreshold <= 1f);
		Precondition.assertBoolean("invalid match threshold",
				matchThreshold >= 0f && matchThreshold <= 1f);
		Precondition.assertBoolean("invalid thresholds (differ > match)",
				differThreshold <= matchThreshold);

		NamedConfiguration cmConf =
			assist.createCustomizedConfiguration(masterCollection, modelName,
					differThreshold, matchThreshold, maxNumMatches);

		MatchCandidates<T> matchCandidates = null;
		try {
			AbaParameters abaParams = null;
			abaParams = NamedConfigConversion.createAbaParameters(cmConf);
			assert abaParams != null;

			AbaSettings oabaSettings = null;
			oabaSettings = NamedConfigConversion.createAbaSettings(cmConf);
			assert oabaSettings != null;

			AbaServerConfiguration serverConfig = null;
			serverConfig =
				NamedConfigConversion.createAbaServerConfiguration(cmConf);
			assert serverConfig != null;

			matchCandidates = delegate.getMatchCandidates(queryRecord, abaParams,
					oabaSettings, serverConfig);
		} catch (BlockingException | IOException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		assert matchCandidates != null;

		EvaluatedRecord[] retVal =
			assist.computeEvaluatedRecords(matchCandidates);
		assert retVal != null;

		return retVal;
	}

	@Override
	public EvaluatedRecord[] getCompositeMatchCandidates(
			ISingleRecord<T> queryRecord, DbRecordCollection masterCollection,
			String modelName, float differThreshold, float matchThreshold,
			int maxNumMatches, LinkCriteria linkCriteria,
			EvalRecordFormat resultFormat, String externalId)
			throws ModelException, ArgumentException,
			UrmIncompleteBlockingSetsException, UrmUnderspecifiedQueryException,
			RecordException, RecordCollectionException, ConfigException,
			CmRuntimeException, RemoteException {

		Precondition.assertNonNullArgument("null query", queryRecord);
		Precondition.assertNonNullArgument("null references", masterCollection);
		Precondition.assertNonEmptyString("null or empty model", modelName);
		Precondition.assertBoolean("invalid differ threshold",
				differThreshold >= 0f && differThreshold <= 1f);
		Precondition.assertBoolean("invalid match threshold",
				matchThreshold >= 0f && matchThreshold <= 1f);
		Precondition.assertBoolean("invalid thresholds (differ > match)",
				differThreshold <= matchThreshold);

		NamedConfiguration cmConf =
			assist.createCustomizedConfiguration(masterCollection, modelName,
					differThreshold, matchThreshold, maxNumMatches);

		TransitiveCandidates<T> transitiveCandidates = null;
		try {
			AbaParameters abaParams = null;
			abaParams = NamedConfigConversion.createAbaParameters(cmConf);
			assert abaParams != null;

			AbaSettings oabaSettings = null;
			oabaSettings = NamedConfigConversion.createAbaSettings(cmConf);
			assert oabaSettings != null;

			AbaServerConfiguration serverConfig = null;
			serverConfig =
				NamedConfigConversion.createAbaServerConfiguration(cmConf);
			assert serverConfig != null;
			
			IGraphProperty mergeConnectivity = assist.computeGraphProperty(linkCriteria);

			boolean mustIncludeQuery = assist.computeQueryInclusion(linkCriteria);

			transitiveCandidates =
				delegate.getTransitiveCandidates(queryRecord, abaParams, oabaSettings,
						serverConfig, mergeConnectivity, mustIncludeQuery);
		} catch (BlockingException | IOException | TransitivityException e) {
			String msg = e.toString();
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		assert transitiveCandidates != null;

		EvaluatedRecord[] retVal =
			assist.computeEvaluatedRecords(transitiveCandidates);
		assert retVal != null;

		return retVal;
	}

	@Override
	public String getVersion(Object context) throws RemoteException {
		return VERSION;
	}

}
