package com.choicemaker.cms.ejb;

import static com.choicemaker.client.api.Decision.HOLD;
import static com.choicemaker.client.api.WellKnownGraphProperties.GP_SCM;
import static com.choicemaker.cms.ejb.WellKnownInstances.dbRecord01;
import static com.choicemaker.cms.ejb.WellKnownInstances.parameters01;
import static com.choicemaker.cms.ejb.WellKnownInstances.query01;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.util.UniqueSequence;

public class TestData {

	private static final Logger logger =
		Logger.getLogger(TestData.class.getName());

	public static <T extends Comparable<T> & Serializable> Match matchFromEvaluatedPair(
			DataAccessObject<T> query, EvaluatedPair<T> pair,
			TestModel<T> model) {
		Match match;
		final DataAccessObject<T> dao1 = pair.getQueryRecord();
		final DataAccessObject<T> dao2 = pair.getMatchCandidate();
		final Accessor accessor = model.getAccessor();
		final Record<T> qRecord = accessor.toImpl(query);
		final Record<T> mRecord;
		if (!query.equals(dao1) && !query.equals(dao2)) {
			match = null;
		} else if (query.equals(dao1) && query.equals(dao2)) {
			match = null;
		} else {
			if (query.equals(dao1)) {
				mRecord = accessor.toImpl(dao2);
			} else {
				assert query.equals(dao2);
				assert !query.equals(dao1);
				mRecord = accessor.toImpl(dao1);
			}
			T mID = mRecord.getId();
			float probability = pair.getMatchProbability();
			Decision decision = pair.getMatchDecision();
			ClueSet cs = accessor.getClueSet();
			boolean[] enabledFeatures = model.getCluesToEvaluate();
			ActiveClues ac =
				cs.getActiveClues(qRecord, mRecord, enabledFeatures);
			match = new Match(decision, probability, mID, mRecord, ac);
		}
		return match;
	}

	public static <T extends Comparable<T> & Serializable> List<Match> matchListFromEvaluatedPairs(
			DataAccessObject<T> query, List<EvaluatedPair<T>> ePairs,
			TestModel<T> model) {
		List<Match> retVal = new ArrayList<>();
		for (EvaluatedPair<T> pair : ePairs) {
			Match match = matchFromEvaluatedPair(query, pair, model);
			if (match != null) {
				retVal.add(match);
			} else {
				logger.fine("Omitting " + pair + " from Match list");
			}
		}
		return retVal;
	}

	public static <T extends Comparable<T> & Serializable> List<Match> matchListFromTestModel(
			DataAccessObject<T> query, TestModel<T> model) {
		List<EvaluatedPair<T>> ePairs = model.getKnownPairs();
		List<Match> retVal = matchListFromEvaluatedPairs(query, ePairs, model);
		return retVal;
	}

	public static <T extends Comparable<T> & Serializable> MatchRecord2<T> matchrecord2FromEvaluatedPair(
			EvaluatedPair<T> pair) {
		return matchrecord2FromEvaluatedPair(pair, RECORD_SOURCE_ROLE.MASTER);
	}

	public static <T extends Comparable<T> & Serializable> MatchRecord2<T> matchrecord2FromEvaluatedPair(
			EvaluatedPair<T> pair, RECORD_SOURCE_ROLE role) {
		MatchRecord2<T> retVal =
			new MatchRecord2<>(pair.getQueryRecord().getId(),
					pair.getMatchCandidate().getId(), role,
					pair.getMatchProbability(), pair.getMatchDecision(), null);
		return retVal;
	}

	public TestData() {
	}

	/**
	 * Result01
	 * <p>
	 * input fields
	 * <ul>
	 * <li>match list enumerates a hold between query record 01 and database
	 * record 01
	 * </ul>
	 * result01 result fields
	 * <ul>
	 * <li>A composite entity consisting of only single records, namely query01
	 * and dbRecord01
	 * <li>A MatchCandidates instance consisting of only the Evaluated Pair (a
	 * hold) between query01 and dbRecord01
	 * <li>A TransitivityCandidates instance consisting only of the hold between
	 * query01 and dbRecord01 and no merge candidates
	 * </ul>
	 */
	public static ExpectedResult<Integer> createResult01() {
		TestModel<Integer> model = new TestModel<>();
		PMManager.addModel(model);

		Integer ceID = UniqueSequence.getInstance().getNextInteger();
		CompositeEntity expectedCE = new CompositeEntity(ceID);
		
		EvaluatedPair<Integer> ePair =
			new EvaluatedPair<Integer>(query01, dbRecord01, 0.5f, HOLD);
		model.addEvaluatedPair(ePair);
		MatchRecord2<Integer> mr = matchrecord2FromEvaluatedPair(ePair);
		expectedCE.addMatchRecord(mr);

		List<Match> matchList = matchListFromTestModel(query01, model);
		final boolean mustContainQuery = false;
		ExpectedResult<Integer> retVal = new ExpectedResult<Integer>(query01,
				matchList, parameters01, GP_SCM, mustContainQuery, expectedCE);
		return retVal;
	}

}
