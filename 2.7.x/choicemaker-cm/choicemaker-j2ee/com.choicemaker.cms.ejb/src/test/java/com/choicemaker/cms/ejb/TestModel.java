/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.QueryCandidatePair;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Evaluator;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.cm.core.report.Report;
import com.choicemaker.util.Precondition;

public class TestModel<T extends Comparable<T> & Serializable>
		implements IProbabilityModel {

	private static final Logger logger =
		Logger.getLogger(TestModel.class.getName());

	public static final String MODEL_NAME = TestModel.class.getName();

	private final List<QueryCandidatePair<T>> knownPairs;
	private AtomicReference<TestEvaluator> evaluator = new AtomicReference<>();
	private AtomicReference<TestClueSet<T>> clueset = new AtomicReference<>();

	public TestModel() {
		this(Collections.emptyList());
	}

	public TestModel(List<QueryCandidatePair<T>> evaluatedPairs) {
		Precondition.assertNonNullArgument(evaluatedPairs);
		this.knownPairs = new ArrayList<>(evaluatedPairs.size());
		this.knownPairs.addAll(evaluatedPairs);
	}

	public void addEvaluatedPair(QueryCandidatePair<T> pair) {
		Precondition.assertNonNullArgument(pair);
		this.knownPairs.add(pair);
	}

	public List<QueryCandidatePair<T>> getKnownPairs() {
		return Collections.unmodifiableList(knownPairs);
	}

	@Override
	public int activeSize() {
		return 0;
	}

	@Override
	public int activeSize(Decision d) {
		return 0;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
	}

	@Override
	public boolean canEvaluate() {
		return true;
	}

	@Override
	public void changedCluesToEvaluate() {
	}

	@Override
	public Accessor getAccessor() {
		return new TestAccessor<T>(this);
	}

	@Override
	public String getAccessorClassName() {
		return MODEL_NAME;
	}

	@Override
	public String getClueFilePath() {
		throw new Error("not implemented");
	}

	@Override
	public String getClueFileAbsolutePath() {
		throw new Error("not implemented");
	}

	@Override
	public ClueSet getClueSet() {
		ClueSet retVal;
		if (clueset.compareAndSet(null, new TestClueSet<T>(this))) {
			logger.info("ClueSet set to " + clueset.get());
		}
		retVal = clueset.get();
		assert retVal != null;
		return retVal;
	}

	@Override
	public String getClueSetName() {
		throw new Error("not implemented");
	}

	@Override
	public String getClueSetSignature() {
		throw new Error("not implemented");
	}

	@Override
	public boolean[] getCluesToEvaluate() {
		boolean[] retVal = TestClueSet.getCluesToEvaluate();
		return retVal;
	}

	@Override
	public String getClueText(int clueNum) throws IOException {
		throw new Error("not implemented");
	}

	@Override
	public int getDecisionDomainSize() {
		return TestClueSet.DECISION_DOMAIN_SIZE;
	}

	@Override
	public Evaluator getEvaluator() {
		Evaluator retVal;
		if (evaluator.compareAndSet(null, new TestEvaluator(this))) {
			logger.info("Evaluator set to " + evaluator.get());
		}
		retVal = evaluator.get();
		assert retVal != null;
		return retVal;
	}

	@Override
	public String getEvaluatorSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFiringThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date getLastTrainingDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MachineLearner getMachineLearner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModelFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModelName() {
		return TestModel.class.getName();
	}

	@Override
	public String getModelSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemaName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemaSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean[] getTrainCluesToEvaluate() {
		boolean[] retVal = TestClueSet.getCluesToEvaluate();
		return retVal;
	}

	@Override
	public String getTrainingSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableAllCluesBeforeTraining() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableAllRulesBeforeTraining() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTrainedWithHolds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void machineLearnerChanged(Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needsRecompilation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int numTrainCluesToEvaluate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void report(Report report) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginMultiPropertyChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endMultiPropertyChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAccessor(Accessor newAcc)
			throws ModelConfigurationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCluesToEvaluate(boolean[] cluesToEvaluate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnableAllCluesBeforeTraining(boolean v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnableAllRulesBeforeTraining(boolean v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setModelFilePath(String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFiringThreshold(int v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastTrainingDate(Date v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMachineLearner(MachineLearner ml) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClueFilePath(String fn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrainedWithHolds(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrainingSource(String v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserName(String v) {
		// TODO Auto-generated method stub

	}

}
