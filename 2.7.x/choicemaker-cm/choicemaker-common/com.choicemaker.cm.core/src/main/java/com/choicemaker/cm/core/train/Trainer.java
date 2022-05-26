/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.train;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.BooleanActiveClues;
import com.choicemaker.cm.core.ClueDesc;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.ClueSetType;
import com.choicemaker.cm.core.Evaluator;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.OperationFailedException;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;

/**
 * Train and test models with data from a marked record pair source.
 *
 * @author    Martin Buechi
 * @author    S. Yoakum-Stover
 */

public class Trainer /* implements ITrainer */ {

	private static final Logger logger = Logger.getLogger(Trainer.class.getName());

	private ImmutableProbabilityModel model;
	private Collection src;
	private int noPairs;
	private int[] size;
	private float lowerThreshold;
	private float upperThreshold;

	/**
	 * Number of possible decision values, e.g. 2 for match/differ,
	 * 3 for match/differ/hold.
	 */
	private int decisionDomainSize;

	/**
	 * The number of times the ith clue fired on the training set.
	 */
	private int[] counts;

	/**
	 * The number of times the ith clue fired correctly on the training set.
	 */
	private int[] correctCounts;

	/**
	 * The number of times the ith clue fired incorrectly on the training set.
	 */
	private int[] incorrectCounts;

	/**
	 * The number of times the feature fired correctly divided by the number of pairs.
	 */
	private double[] firingPercentages;

	private int[] numCorrectFirings;

	private int[] numIncorrectFirings;

	// Count cache dirty counter supporting multiple evaluations
	private int evaluationNo;
	private int computeCountsNo;
	private int computeProbabilitiesNo;

	public Trainer(float lowerThreshold, float upperThreshold) {
		setLowerThreshold(lowerThreshold);
		setUpperThreshold(upperThreshold);
	}

	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}

	public void setSource(Collection src) {
		this.src = src;
	}

	/** Set the weights and clues to evaluate by training on the source */
	public Object train() throws OperationFailedException {
		computeFirings();
		computeCounts();
		boolean[] cluesToEvaluate = model.getCluesToEvaluate();
		ClueSet cs = model.getAccessor().getClueSet();
		ClueDesc[] desc = cs.getClueDesc();
		if (cs.getType() == ClueSetType.BOOLEAN) {
			int minClueFirings = model.getFiringThreshold();
			for (int i = 0; i < counts.length; ++i) {
				cluesToEvaluate[i] = cluesToEvaluate[i] && (desc[i].rule || correctCounts[i] >= minClueFirings);
			}
		}
		// model.setCluesToEvaluate(cluesToEvaluate) not needed because we got actual array above
		return model.getMachineLearner().train(src, firingPercentages);
	}

	/**
	 * Tests the model against the data from the source.
	 *
	 * @throws IOException  if there is a problem reading from the source.
	 */
	public void test() throws OperationFailedException {
		computeFirings();
	}

	/**
	 * Evaluates the clues on each of the record pairs in the training set.
	 * 
	 * @exception IOException
	 */
	private void computeFirings() throws OperationFailedException {
		final String METHOD = "computeFirings";
		++evaluationNo;
		decisionDomainSize = model.getDecisionDomainSize();
		boolean[] cluesToEvaluate = model.getCluesToEvaluate();
		int br = 0;
		int idx = -1;
		Iterator i = src.iterator();
		while (i.hasNext()) {
			++idx;

			// Because ClueSet instances are stateful, a new instance should
			// be initialized for each pair. (The current implementation of
			// ImmutableProbabilityModel.getClueSet() calls a generated accessor
			// which in turn instantiates a new ClueSet instance.)
			ClueSet fs = model.getClueSet();

			MutableMarkedRecordPair mp = (MutableMarkedRecordPair) i.next();
			logPair(METHOD,idx,mp);
			mp.setActiveClues(fs.getActiveClues(mp.getQueryRecord(), mp.getMatchRecord(), cluesToEvaluate));
			if ((br = (br + 1) % 100) == 0 && Thread.currentThread().isInterrupted()) {
				break;
			}
		}
		noPairs = src.size();
		if (noPairs == 0) {
			throw new OperationFailedException(ChoiceMakerCoreMessages.m.formatMessage("train.trainer.source.is.empty"));
		}
	}

	/**
	 * Return the decision domain size. This is 3 if there are
	 * clues that predict hold and 2 otherwise.
	 *
	 * @return  the decision domain size.
	 */
	public int getDecisionDomainSize() {
		return decisionDomainSize;
	}

	/**
	 * Returns the number of times each clue fired on the source data.
	 *
	 * @return  the number of times each clue fired on the source data.
	 */
	public int[] getFirings() {
		computeCounts();
		return counts;
	}

	/**
	 * Returns the number of times each clue fired correctly on the source data.
	 *
	 * @return  the number of times each clue fired correctly on the source data.
	 */
	public int[] getCorrectFirings() {
		computeCounts();
		return correctCounts;
	}

	/**
	 * Returns the number of times each clue fired incorrectly on the source data.
	 *
	 * @return  the number of times each clue fired incorrectly on the source data.
	 */
	public int[] getIncorrectFirings() {
		computeCounts();
		return incorrectCounts;
	}

	public int getNumCorrectFirings(Decision d) {
		if (d.toInt() >= decisionDomainSize) {
			return 0; //what a hack!
		}
		return numCorrectFirings[d.toInt()];
	}

	public int getNumIncorrectFirings(Decision d) {
		if (d.toInt() >= decisionDomainSize) {
			return 0; //what a hack!
		}
		return numIncorrectFirings[d.toInt()];
	}

	public double[] getFiringPercentages() {
		computeCounts();
		return firingPercentages;
	}

	/**
	 * Returns the number of pairs used for training/testing.
	 *
	 * @return  The number of pairs used for training/testing.
	 */
	public int size() {
		return noPairs;
	}

	/**
	 * Returns the number of pairs predicting <code>d</code> used for training/testing.
	 *
	 * @param   d  The decision for which the size is requested. 
	 * @return  The number of pairs predicting <code>d</code> used for training/testing.
	 */
	public int size(Decision d) {
		computeCounts();
		return size[d.toInt()];
	}

	public Collection getSource() {
		return src;
	}

	protected static void logPair(String tag, int pairIdx, ImmutableRecordPair<?> p) {
		if (logger.isLoggable(Level.FINER)) {
			Record<?> qRecord = p == null ? null : p.getQueryRecord();
			Object qId = qRecord == null ? null : qRecord.getId();

			Record<?> mRecord = p == null ? null : p.getMatchRecord();
			Object mId = mRecord == null ? null : mRecord.getId();

			String msg0 = "%s: pair %d[queryId = %s, matchId = %s]";
			String msg = String.format(msg0, tag, pairIdx, qId, mId);
			logger.finer(msg);
		}
	}

	protected static void logPairDecisionProbability(String tag, int pairIdx, ImmutableRecordPair<?> pair,
			Decision decision, float probabilty) {
		if (logger.isLoggable(Level.FINE)) {
			Record<?> qRecord = pair == null ? null : pair.getQueryRecord();
			Object qId = qRecord == null ? null : qRecord.getId();

			Record<?> mRecord = pair == null ? null : pair.getMatchRecord();
			Object mId = mRecord == null ? null : mRecord.getId();

			String d = decision == null ? "null" : decision.toSingleCharString();

			String msg0 = "%s: pair %d[queryId = %s, matchId = %s], decision = %s, probability = %f";
			String msg = String.format(msg0, tag, pairIdx, qId, mId, d, probabilty);
			logger.fine(msg);
		}
	}

	public void computeProbabilitiesAndDecisions(float lt, float ut) {
		final String METHOD = "computeProbabilitiesAndDecisions";
		if (evaluationNo != computeProbabilitiesNo) {
			computeProbabilitiesNo = evaluationNo;
			Evaluator e = model.getEvaluator();
			Iterator i = src.iterator();
			int count = -1;
			while (i.hasNext()) {
				++count;
				MutableMarkedRecordPair p = (MutableMarkedRecordPair) i.next();
				logPair(METHOD, count, p);
				p.setProbability(e.getProbability(p.getActiveClues()));
				p.setCmDecision(e.getDecision(p.getActiveClues(), p.getProbability(), lt, ut));
				logPairDecisionProbability(METHOD, count, p, p.getCmDecision(), p.getProbability());
			}
		}
	}

	public void computeDecisions(float lt, float ut) {
		Evaluator e = model.getEvaluator();
		Iterator i = src.iterator();
		while (i.hasNext()) {
			MutableMarkedRecordPair p = (MutableMarkedRecordPair) i.next();
			p.setCmDecision(e.getDecision(p.getActiveClues(), p.getProbability(), lt, ut));
		}
	}

	public void computeProbability(MutableMarkedRecordPair mrp) {
		final String METHOD = "computeProbabilitiesAndDecisions";
		mrp.getQueryRecord().computeValidityAndDerived();
		mrp.getMatchRecord().computeValidityAndDerived();
		mrp.setActiveClues(model.getClueSet().getActiveClues(mrp.getQueryRecord(), mrp.getMatchRecord(), model.getCluesToEvaluate()));
		Evaluator e = model.getEvaluator();
		logPairDecisionProbability(METHOD, -1, mrp, mrp.getCmDecision(), mrp.getProbability());
		mrp.setProbability(e.getProbability(mrp.getActiveClues()));
		mrp.setCmDecision(e.getDecision(mrp.getActiveClues(), mrp.getProbability(), lowerThreshold, upperThreshold));
		logPairDecisionProbability(METHOD, -2, mrp, mrp.getCmDecision(), mrp.getProbability());
	}

	/**
	 * Fills the counts array whose ith element represents the number of times the
	 * ith clue fired when evaluated on the training set. 
	 */
	private void computeCounts() {
		if (evaluationNo != computeCountsNo) {
			computeCountsNo = evaluationNo;
			ClueSet fs = model.getClueSet();
			ClueDesc[] cd = fs.getClueDesc();
			int numClues = fs.size();
			counts = new int[numClues];
			correctCounts = new int[numClues];
			incorrectCounts = new int[numClues];
			firingPercentages = new double[numClues];
			numCorrectFirings = new int[decisionDomainSize];
			numIncorrectFirings = new int[decisionDomainSize];
			size = new int[Decision.NUM_DECISIONS];
			Iterator iPair = src.iterator();
			while (iPair.hasNext()) {
				MutableMarkedRecordPair p = (MutableMarkedRecordPair) iPair.next();
				++size[p.getMarkedDecision().toInt()];
				ActiveClues af = p.getActiveClues();
				if (af instanceof BooleanActiveClues) {
					BooleanActiveClues bac = (BooleanActiveClues) af;
					int len = af.size();
					for (int j = 0; j < len; ++j) {
						int clueNum = bac.get(j);
						++counts[clueNum];
						Decision d = cd[clueNum].decision;
						if (p.getMarkedDecision() == d) {
							++correctCounts[clueNum];
							++numCorrectFirings[d.toInt()];
						} else {
							++incorrectCounts[clueNum];
							++numIncorrectFirings[d.toInt()];
						}
					}
				}
				int[] rules = af.getRules();
				for (int i = 0; i < rules.length; ++i) {
					++counts[rules[i]];
				}
			}
			for (int i = 0; i < firingPercentages.length; ++i) {
				firingPercentages[i] = (double) correctCounts[i] / (double) noPairs;
			}
		}
	}
	/**
	 * Returns the lowerThreshold.
	 * @return float
	 */
	public float getLowerThreshold() {
		return lowerThreshold;
	}

	/**
	 * Returns the upperThreshold.
	 * @return float
	 */
	public float getUpperThreshold() {
		return upperThreshold;
	}

	/**
	 * Sets the lowerThreshold.
	 * @param lowerThreshold The lowerThreshold to set
	 */
	public void setLowerThreshold(float lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}

	/**
	 * Sets the upperThreshold.
	 * @param upperThreshold The upperThreshold to set
	 */
	public void setUpperThreshold(float upperThreshold) {
		this.upperThreshold = upperThreshold;
	}

}
