/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Date;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.report.Report;

/**
 * @author rphall
 */
public interface ImmutableProbabilityModel {
	
	String CLUES_TO_EVALUATE = "cluesToEvaluate";

	String MACHINE_LEARNER = "machineLearner";

	String MACHINE_LEARNER_PROPERTY = "machineLearnerProperty";

	String NAME = "name";

	/**
	 * Returns the number of active clues in this <code>ClueSet</code>.
	 *
	 * @return The number of active clues in this <code>ProbabilityModel</code>.
	 */
	int activeSize();

	/**
	 * Returns the number of clues predicting <code>Decision<code>
	 * <code>d</code> in this <code>ProbabilityModel</code>.
	 *
	 * @return The number of clues predicting <code>Decision</code>
	 *         <code>d</code> in this <code>ProbabilityModel</code>.
	 */
	int activeSize(Decision d);

	void addPropertyChangeListener(PropertyChangeListener l);

	boolean canEvaluate();

	void changedCluesToEvaluate();

	/**
	 * Returns the translator accessors.
	 *
	 * @return The translator accessors.
	 */
	Accessor getAccessor();

	/**
	 * Returns the name of the Accessor class.
	 * 
	 * Note: this is not the same as getAccessor().getClass().getName() because
	 * getAccessor() returns a dynamic proxy, so the class name is something
	 * like $Proxy0.
	 * 
	 * @return The name of the accessor class.
	 */
	String getAccessorClassName();

	/**
	 * Returns the file path of the clue definition file (*.clues) relative to
	 * the clue weights file (*.model). (Caveat: In future versions of
	 * ChoiceMaker, the extensions for clue and weight files may change, or
	 * models may be persisted as database entries rather than files.)<br/>
	 * <br/>
	 * Note: The relative path is often problematic (although it is required in
	 * some contexts). In many contexts, the absolute file path may be
	 * preferred; see {@link #getClueFileAbsolutePath()}.
	 */
	String getClueFilePath();

	/**
	 * Returns an absolute path (possibly null) to the clue definition file.
	 */
	String getClueFileAbsolutePath();

	/** Returns the clue set instance used by this model */
	ClueSet getClueSet();

	/**
	 * Returns the simple name of the ClueSet, as defined in the ClueMaker file.
	 * <p>
	 * Currently, the clue set name is the same as the base file name obtained
	 * from the {@link #getClueFilePath() clue-file path}, minus any extension.
	 * So, for example, the file <code>MyModels/MyClueSet.clues</code> currently
	 * corresponds to the clue set named <code>MyClueSet</code>. However, this
	 * correspondence is a convention enforced by the ClueMaker language, and it
	 * may not be enforced in future versions of ChoiceMaker, if and when other
	 * languages besides ClueMaker are used to define ClueSets.
	 */
	String getClueSetName();

	/**
	 * Returns the signature of the clue set. The signature should be a SHA1 or
	 * similar hash calculated from the clues and rules used by the model,
	 * including their order and logic, but excluding their weights. If two
	 * models have the same clue-set signature, then they use the same clues to
	 * compare entities, although the clue weights may be different.
	 * 
	 * @see #getSchemaSignature()
	 * @see #getEvaluatorSignature()
	 */
	String getClueSetSignature();

	/** Returns a list of indices of the clues to evaluate */
	boolean[] getCluesToEvaluate();

	String getClueText(int clueNum) throws IOException;

	int getDecisionDomainSize();

	// NOT YET DEFINED (as of version 2.7.1)
	// /**
	// * Returns the name of the interface representing the entities matched by
	// * this model.
	// */
	// String getEntityInterfaceName();

	Evaluator getEvaluator();

	/**
	 * Returns the signature of the evaluator used by this model.
	 * 
	 * @see #getSchemaSignature()
	 * @see #getClueSetSignature()
	 * @see Evaluator#getSignature()
	 */
	String getEvaluatorSignature();

	/**
	 * Returns the value of firingThreshold. The firing threshold is the number
	 * of times a clue must fire <em>correctly</em> during a training session in
	 * order for the clue to be enabled.
	 * 
	 * @return a positive value
	 */
	int getFiringThreshold();

	/** Get the last training date */
	Date getLastTrainingDate();

	MachineLearner getMachineLearner();

	/** Returns the path to the probability model weights file (*.model) */
	String getModelFilePath();

//	/**
//	 * A path to the model weights file (*.model) relative to the application
//	 * working directory.
//	 */
//	String getWeightFilePath();
//
//	/** An absolute path to the model weights file (*.model) */
//	String getWeightFileAbsolutePath();

	/**
	 * Returns the configuration name of a probability model, as registered with
	 * a {@link IProbabilityModelManager}. If a model configuration hasn't been
	 * registered, then the model name is simply the base name of the
	 * {@link #getModelFilePath() model file path} without any extension; for
	 * example, the name of an unregistered model loaded from the path
	 * <code>myModels/MyModel.model</code> would be <code>MyModel</code>.
	 *
	 * @return The name of the probability model.
	 */
	String getModelName();

	/**
	 * Returns the signature for this model. The signature should be a SHA1 or
	 * similar hash calculated the signatures of the schema, clue set and
	 * machine learning used by this model.
	 * 
	 * @see #getSchemaSignature()
	 * @see #getClueSetSignature()
	 * @see #getEvaluatorSignature()
	 * @see com.choicemaker.cm.core.util.Signature#calculateSignature(String,
	 *      String, String)
	 */
	String getModelSignature();

	/**
	 * Returns the name of the schema that defines the layout of entity records.
	 * Schema names are an artifact required by the ClueMaker language. In
	 * future versions of ChoiceMaker, models may not be associated with schema
	 * names, if and when other languages besides ClueMaker are used to define
	 * ClueSets.
	 */
	String getSchemaName();

	/**
	 * Returns the signature of the record schema used by the model. The
	 * signature should be a SHA1 or similar hash that reflects the tree of
	 * interfaces that define the type entity matched by the model. If two
	 * models have the same schema signature, then they apply to the same type
	 * of entity, although they may use different clues and weights to compute
	 * match probabilities.
	 */
	String getSchemaSignature();

	boolean[] getTrainCluesToEvaluate();

	/** Get the name of training source */
	String getTrainingSource();

	/** Get the name of the account used to train the model */
	String getUserName();

	/** Check whether to enable all clues before training. */
	boolean isEnableAllCluesBeforeTraining();

	/** Check whether to enable all rules before training. */
	boolean isEnableAllRulesBeforeTraining();

	boolean isTrainedWithHolds();

	void machineLearnerChanged(Object oldValue, Object newValue);

	boolean needsRecompilation();

	int numTrainCluesToEvaluate();

	void removePropertyChangeListener(PropertyChangeListener l);

	void report(Report report) throws IOException;

}
