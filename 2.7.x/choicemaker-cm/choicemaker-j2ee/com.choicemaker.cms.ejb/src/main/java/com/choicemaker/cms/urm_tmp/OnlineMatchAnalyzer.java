/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.urm_tmp;

import com.choicemaker.cm.oaba.api.AbaStatisticsController;

/**
 */
public interface OnlineMatchAnalyzer extends AbaStatisticsController {

//	EvaluatedRecord[] getCompositeMatchCandidates(
//			Record patientHolder,
//			DbRecordCollection subsetDbRecordCollection,
//			String probabilityModel, float mediumProbability,
//			float highProbability, int maxNumMatches, LinkCriteria linkCriteria,
//			EvalRecordFormat evalRecordFormat, String trackingId);

//	/**
//	 * @param queryRecord
//	 *            query record
//	 * @param mRc
//	 *            a master record collection.
//	 * @param modelName
//	 *            the name of the probability model.
//	 * @param differThreshold
//	 *            matching probability below this threshold constitutes the
//	 *            differ.
//	 * @param matchThreshold
//	 *            matching probability above this threshold constitutes the
//	 *            match.
//	 * @param maxNumMatches
//	 *            the limit of number of records included into the resulting
//	 *            array. The value <code>-1</code> means bring back all matches
//	 *            and holds.
//	 * @param c
//	 *            link criteria
//	 * @param resultFormat
//	 *            the format of the evaluated records that will be returned as
//	 *            the result.
//	 * @param trackingId
//	 *            an arbitrary string that is stored and may be used for later
//	 *            reporting.
//	 * @return an array of evaluated records
//	 */
//	public EvaluatedRecord[] getCompositeMatchCandidates(
//			ISingleRecord queryRecord, DbRecordCollection mRc,
//			String modelName, float differThreshold, float matchThreshold,
//			int maxNumMatches, LinkCriteria c, EvalRecordFormat resultFormat,
//			String externalId);
//
//	/**
//	 * @param queryRecord
//	 *            the first(query) record.
//	 * @param masterRecord
//	 *            the second(master) record.
//	 * @param modelName
//	 *            the name of the probability model.
//	 * @param differThreshold
//	 *            matching probability below this threshold constitutes the
//	 *            differ.
//	 * @param matchThreshold
//	 *            matching probability above this threshold constitutes the
//	 *            match.
//	 * @param resultFormat
//	 *            the format of the match score that will be returned as the
//	 *            result.
//	 * @param externalId
//	 *            an arbitrary string that is stored and may be used for later
//	 *            reporting.
//	 * 
//	 * @return the match score between the first and the second record.
//	 */
//	
//	public MatchScore evaluatePair(ISingleRecord queryRecord,
//			ISingleRecord masterRecord, String modelName,
//			float differThreshold, float matchThreshold,
//			ScoreType resultFormat, String externalId);
//
//	/**
//	 * @param queryRecord
//	 *            a query record.
//	 * @param mRc
//	 *            a master record collection.
//	 * @param modelName
//	 *            the name of the probability model.
//	 * @param differThreshold
//	 *            matching probability below this threshold constitutes the
//	 *            differ.
//	 * @param matchThreshold
//	 *            matching probability above this threshold constitutes the
//	 *            match.
//	 * @param maxNumMatches
//	 *            the limit of number of records included into the resulting
//	 *            array. The value <code>-1</code> means bring back all matches
//	 *            and holds.
//	 * @param resultFormat
//	 *            the format of the evaluated records that will be returned as
//	 *            the result.
//	 * @param externalId
//	 *            an arbitrary string that is stored and may be used for later
//	 *            reporting.
//	 * 
//	 * @return the array of the evaluated records
//	 */
//	public EvaluatedRecord[] getMatchCandidates(ISingleRecord queryRecord,
//			DbRecordCollection mRc, String modelName, float differThreshold,
//			float matchThreshold, int maxNumMatches,
//			EvalRecordFormat resultFormat, String externalId);

}
