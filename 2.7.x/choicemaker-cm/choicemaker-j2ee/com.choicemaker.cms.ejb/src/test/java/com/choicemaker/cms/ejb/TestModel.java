package com.choicemaker.cms.ejb;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Evaluator;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.report.Report;
import com.choicemaker.util.Precondition;

public class TestModel<T extends Comparable<T> & Serializable> implements ImmutableProbabilityModel {
	
	private final List<EvaluatedPair<T>> knownPairs;
	
	public TestModel() {
		this(Collections.emptyList());
	}
	
	public TestModel(List<EvaluatedPair<T>> evaluatedPairs) {
		Precondition.assertNonNullArgument(evaluatedPairs);
		this.knownPairs = new ArrayList<>(evaluatedPairs.size());
		this.knownPairs.addAll(evaluatedPairs);
	}
	
	public void addEvaluatedPair(EvaluatedPair<T> pair) {
		Precondition.assertNonNullArgument(pair);
		this.knownPairs.add(pair);
	}
	
	public List<EvaluatedPair<T>> getKnownPairs() {
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
		return TestAccessor.class.getName();
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
		throw new Error("not implemented");
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
		throw new Error("not implemented");
	}

	@Override
	public String getClueText(int clueNum) throws IOException {
		throw new Error("not implemented");
	}

	@Override
	public int getDecisionDomainSize() {
		throw new Error("not implemented");
	}

	@Override
	public Evaluator getEvaluator() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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

}
