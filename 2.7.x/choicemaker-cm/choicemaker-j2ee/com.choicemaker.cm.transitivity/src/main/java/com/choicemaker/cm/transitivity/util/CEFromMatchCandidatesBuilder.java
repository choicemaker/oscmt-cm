/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.InvalidProfileException;
import com.choicemaker.cm.core.Profile;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.BeanMatchCandidate;
import com.choicemaker.cm.core.base.BeanProfile;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.core.util.MatchUtils;

/**
 * This object builds an Iterator of CompositeEntity from an Array of
 * MatchCandidate.
 *
 * @author pcheung
 *
 *         ChoiceMaker Technologies, Inc.
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class CEFromMatchCandidatesBuilder extends CEFromMatchesBuilder {

	private static Logger logger = Logger
			.getLogger(CEFromMatchCandidatesBuilder.class.getName());

	private BeanMatchCandidate[] candidates;

	/**
	 * @param q
	 *            - the query record
	 * @param candidates
	 *            - an array of BeanMatchCandidate
	 * @param modelName
	 *            - name of the ProbabilityModel
	 * @param differThreshold
	 *            - differ threshold
	 * @param matchThreshold
	 *            - match threshold
	 */
	public CEFromMatchCandidatesBuilder(Record q,
			BeanMatchCandidate[] candidates, String modelName,
			float differThreshold, float matchThreshold) {

		this.q = q;
		this.candidates = candidates;
		this.differThreshold = differThreshold;
		this.matchThreshold = matchThreshold;

		this.model = PMManager.getModelInstance(modelName);
		this.evaluator = model.getEvaluator();
	}

	/**
	 * This version of the constructor takes a Profile containing the record
	 * instead of the record itself.
	 *
	 * @param p
	 *            - profile that contains the record
	 * @param candidates
	 *            - an array of BeanMatchCandidate
	 * @param modelName
	 *            - name of the ProbabilityModel
	 * @param differThreshold
	 *            - differ threshold
	 * @param matchThreshold
	 *            - match threshold
	 */
	public CEFromMatchCandidatesBuilder(Profile p,
			BeanMatchCandidate[] candidates, String modelName,
			float differThreshold, float matchThreshold)
			throws InvalidProfileException {

		this.candidates = candidates;
		this.differThreshold = differThreshold;
		this.matchThreshold = matchThreshold;

		this.model = PMManager.getModelInstance(modelName);
		this.q = p.getRecord(model);
		this.evaluator = model.getEvaluator();
	}

	/**
	 * This method returns an Iterator of CompositeEntities created by the
	 * record and the match candidates.
	 *
	 * @return Iterator
	 */
	@Override
	public Iterator getCompositeEntities() throws TransitivityException {

		List<MatchRecord2> pairs = new ArrayList<>();
		try {

			// First, get the matches between q and all the m's.
			List<Record> records = new ArrayList<>();
			for (int i = 0; i < candidates.length; i++) {
				Object o = candidates[i].getProfile();
				Profile profile = new BeanProfile(o);
				Record m = profile.getRecord(model);
				logger.fine("q " + q.getId().toString() + " m "
						+ m.getId().toString());

				final Decision d =
					Decision.valueOf(candidates[i].getDecision());
				final String[] notes = candidates[i].getNotes();
				final String noteInfo =
					MatchUtils.getNotesAsDelimitedString(notes);
				MatchRecord2 mr =
					new MatchRecord2(q.getId(), m.getId(),
							RECORD_SOURCE_ROLE.STAGING,
							candidates[i].getProbability(), d, noteInfo);

				pairs.add(mr);
				records.add(m);
			}

			// Second, match the m records against each other.
			pairs.addAll(allMatch(records));

		} catch (InvalidProfileException e) {
			logger.severe(e.toString());
		}

		CompositeEntityBuilder ceb = new CompositeEntityBuilder(pairs);
		return ceb.getCompositeEntities();
	}

}
