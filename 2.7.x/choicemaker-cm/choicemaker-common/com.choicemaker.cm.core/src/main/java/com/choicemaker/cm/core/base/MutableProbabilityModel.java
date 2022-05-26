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
package com.choicemaker.cm.core.base;

import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;

import javax.swing.event.SwingPropertyChangeSupport;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.ClueDesc;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.Descriptor;
import com.choicemaker.cm.core.Evaluator;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.cm.core.ProbabilityModelSpecification;
import com.choicemaker.cm.core.report.Report;
import com.choicemaker.cm.core.report.Reporter;
import com.choicemaker.cm.core.util.NameUtils;
import com.choicemaker.cm.core.util.Signature;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.choicemaker.util.ArrayHelper;
import com.choicemaker.util.FileUtilities;

/**
 * A probability model consisting of holder classes, translators, a clue set,
 * weights, and a list of clues to be evaluated.
 *
 * Class invariant: clueSet != null <=> cluesToEval != null AND
 * cluesToEval.length == clueSet.size() AND weights != null AND weights.length
 * == clueSet.size
 *
 * @author Martin Buechi
 * @author S. Yoakum-Stover
 * @author rphall (Split ProbabilityModel into separate instance and manager
 *         types)
 * @see PMManager
 */
public class MutableProbabilityModel implements IProbabilityModel {

	// private static final Logger logger = Logger
	// .getLogger(MutableProbabilityModel.class.getName());

	private Accessor acc;
	private String accessorClassName;
	private String clueFilePath;
	private File clueFile;
	private boolean[] cluesToEvaluate;
	private int decisionDomainSize;
	private boolean enableAllCluesBeforeTraining;
	private boolean enableAllRulesBeforeTraining;
	private int firingThreshold = 3;
	private Date lastTrainingDate;
	private MachineLearner ml;
	private String modelName;
	private String modelFilePath;
	private boolean multiPropertyChange;
	private boolean trainedWithHolds;
	private String trainingSource;
	private String userName;

	// listeners
	private SwingPropertyChangeSupport propertyChangeListeners =
		new SwingPropertyChangeSupport(this);

	public MutableProbabilityModel(String modelFilePath, String clueFilePath) {
		setModelFilePath(modelFilePath);
		setClueFilePath(clueFilePath);
		setMachineLearner(new DoNothingMachineLearning());
	}

	public MutableProbabilityModel(ProbabilityModelSpecification spec,
			Accessor acc) throws ModelConfigurationException {
		setModelFilePath(spec.getWeightFilePath());
		setClueFilePath(spec.getClueFilePath());
		setMachineLearner(spec.getMachineLearner());
		this.setAccessor(acc);
	}

	public MutableProbabilityModel(String modelFilePath, String clueFilePath,
			Accessor acc, MachineLearner ml, boolean[] cluesToEvaluate,
			String trainingSource, boolean trainedWithHolds,
			Date lastTrainingDate) throws IllegalArgumentException,
			ModelConfigurationException {
		this(modelFilePath, clueFilePath);
		setAccessorInternal(acc);
		this.trainingSource = trainingSource;
		this.trainedWithHolds = trainedWithHolds;
		this.lastTrainingDate = lastTrainingDate;
		acc.getClueSet(); // Make sure the clue set gets loaded
		setMachineLearner(ml);
		setCluesToEvaluate(cluesToEvaluate);
		computeDecisionDomainSize();
	}

	/**
	 * Returns the number of active clues in this <code>ClueSet</code>.
	 *
	 * @return The number of active clues in this <code>ProbabilityModel</code>.
	 */
	@Override
	public int activeSize() {
		int r = 0;
		for (int i = 0; i < cluesToEvaluate.length; ++i) {
			if (cluesToEvaluate[i]) {
				++r;
			}
		}
		return r;
	}

	/**
	 * Returns the number of clues predicting <code>Decision<code>
	 * <code>d</code> in this <code>ProbabilityModel</code>.
	 *
	 * @return The number of clues predicting <code>Decision</code>
	 *         <code>d</code> in this <code>ProbabilityModel</code>.
	 */
	@Override
	public int activeSize(Decision d) {
		ClueDesc[] cd = acc.getClueSet().getClueDesc();
		int r = 0;
		for (int i = 0; i < cluesToEvaluate.length; ++i) {
			if (cluesToEvaluate[i] && cd[i].getDecision() == d) {
				++r;
			}
		}
		return r;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.addPropertyChangeListener(l);
	}

	@Override
	public void beginMultiPropertyChange() {
		multiPropertyChange = true;
	}

	@Override
	public boolean canEvaluate() {
		return ml.canEvaluate();
	}

	@Override
	public void changedCluesToEvaluate() {
		if (!multiPropertyChange) {
			propertyChangeListeners.firePropertyChange(CLUES_TO_EVALUATE, null,
					cluesToEvaluate);
		}
	}

	private void computeDecisionDomainSize() {
		decisionDomainSize = acc.getClueSet().size(Decision.HOLD) == 0 ? 2 : 3;
	}

	@Override
	public void endMultiPropertyChange() {
		multiPropertyChange = false;
		propertyChangeListeners.firePropertyChange(null, new Object(),
				new Object());
	}

	/**
	 * Returns the translator accessors.
	 *
	 * @return The translator accessors.
	 */
	@Override
	public Accessor getAccessor() {
		return acc;
	}

	/**
	 * Returns the name of the Accessor class.
	 *
	 * Note: this is not the same as getAccessor().getClass().getName() because
	 * getAccessor() returns a dynamic proxy, so the class name is something
	 * like $Proxy0.
	 *
	 * @return The name of the accessor class.
	 */
	@Override
	public String getAccessorClassName() {
		return accessorClassName;
	}

	/**
	 * Returns an instance of the clue set.
	 *
	 * @return An instance of the clue set.
	 */
	@Override
	public ClueSet getClueSet() {
		return acc.getClueSet();
	}

	// public String getClueSetPath() {
	// return clueSetPath;
	// }

	/**
	 * Returns the file path of the clue definition file (*.clues) relative to
	 * the clue weights file (*.model).
	 * 
	 * @see #getClueFileAbsolutePath()
	 */
	@Override
	public String getClueFilePath() {
		return clueFilePath;
	}

	/**
	 * Returns an absolute path, if {@link #getClueFile()} is not null,
	 * otherwise returns null.
	 */
	@Override
	public String getClueFileAbsolutePath() {
		String retVal = null;
		File f = getClueFile();
		if (f != null) {
			retVal = f.getAbsolutePath();
		}
		return retVal;
	}

	public File getClueFile() {
		return this.clueFile;
	}

	/**
	 * Returns the list of clues to evaluate.
	 *
	 * @return The list of clues to evaluate.
	 */
	@Override
	public boolean[] getCluesToEvaluate() {
		return cluesToEvaluate;
	}

	@Override
	public String getClueText(int clueNum) throws IOException {
		ClueDesc cd = acc.getClueSet().getClueDesc()[clueNum];
		int start = cd.getStartLineNumber();
		int end = cd.getEndLineNumber();
		int len = end - start;
//		File clueFile = getClueFile();
//		String clueFilePath = clueFile.getAbsolutePath();
		String clueFilePath = getClueFileAbsolutePath();
		BufferedReader in =
			new BufferedReader(new FileReader(clueFilePath));
		for (int i = 1; i < start && in.ready(); ++i) {
			in.readLine();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i <= len && in.ready(); ++i) {
			buf.append(in.readLine()).append(Constants.LINE_SEPARATOR);
		}
		in.close();
		return buf.toString();
	}

	@Override
	public int getDecisionDomainSize() {
		return decisionDomainSize;
	}

	@Override
	public Evaluator getEvaluator() {
		return ml.getEvaluator();
	}

	/**
	 * Returns the file name of the probability model.
	 *
	 * @return The file name of the probability model.
	 */
	@Override
	public String getModelFilePath() {
		return modelFilePath;
	}

	/**
	 * Get the value of firingThreshold.
	 * 
	 * @return value of firingThreshold.
	 */
	@Override
	public int getFiringThreshold() {
		return firingThreshold;
	}

	/**
	 * Get the value of lastTrainingDate.
	 * 
	 * @return value of lastTrainingDate.
	 */
	@Override
	public Date getLastTrainingDate() {
		return lastTrainingDate;
	}

	@Override
	public MachineLearner getMachineLearner() {
		return ml;
	}

	/**
	 * Returns the model name of the probability model.
	 *
	 * @return The model name of the probability model.
	 */
	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public boolean[] getTrainCluesToEvaluate() {
		boolean[] res = new boolean[cluesToEvaluate.length];
		ClueDesc[] desc = getClueSet().getClueDesc();
		for (int i = 0; i < cluesToEvaluate.length; ++i) {
			res[i] = cluesToEvaluate[i] && !desc[i].rule;
		}
		return res;
	}

	/**
	 * Get the value of trainingSource.
	 * 
	 * @return value of trainingSource.
	 */
	@Override
	public String getTrainingSource() {
		return trainingSource;
	}

	/**
	 * Get the value of userName.
	 * 
	 * @return value of userName.
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	/**
	 * Get the value of enableAllCluesBeforeTraining.
	 * 
	 * @return value of enableAllCluesBeforeTraining.
	 */
	@Override
	public boolean isEnableAllCluesBeforeTraining() {
		return enableAllCluesBeforeTraining;
	}

	/**
	 * Get the value of enableAllRulesBeforeTraining.
	 * 
	 * @return value of enableAllRulesBeforeTraining.
	 */
	@Override
	public boolean isEnableAllRulesBeforeTraining() {
		return enableAllRulesBeforeTraining;
	}

	@Override
	public boolean isTrainedWithHolds() {
		return trainedWithHolds;
	}

	@Override
	public void machineLearnerChanged(Object oldValue, Object newValue) {
		propertyChangeListeners.firePropertyChange(MACHINE_LEARNER_PROPERTY,
				oldValue, newValue);
	}

	@Override
	public boolean needsRecompilation() {
		if (acc == null) {
			return true;
		} else {
			long cd = acc.getCreationDate();
			return cd < getClueFile().lastModified()
					|| cd < new File(acc.getSchemaFileName()).getAbsoluteFile()
							.lastModified();
		}
	}

	@Override
	public int numTrainCluesToEvaluate() {
		int res = 0;
		ClueDesc[] desc = getClueSet().getClueDesc();
		for (int i = 0; i < cluesToEvaluate.length; ++i) {
			if (cluesToEvaluate[i] && !desc[i].rule) {
				++res;
			}
		}
		return res;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.removePropertyChangeListener(l);
	}

	@Override
	public void report(Report report) throws IOException {
		IOException ex = null;
		Reporter[] reporters = PMManager.getGlobalReporters();
		for (int i = 0; i < reporters.length; i++) {
			try {
				reporters[i].append(report);
			} catch (IOException e) {
				if (ex == null) {
					ex = e;
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	/**
	 * Sets the translator accessors.
	 *
	 * @param newAcc
	 *            The translator accessors.
	 * @throws ModelConfigurationException
	 */
	@Override
	public void setAccessor(Accessor newAcc) throws ModelConfigurationException {
		Accessor oldAccessor = acc;
		ClueSet newClueSet = newAcc.getClueSet();
		int newSize = newClueSet.size();
		boolean[] newCluesToEvaluate = ArrayHelper.getTrueArray(newSize);
		int[] oldClueNums = new int[newSize];
		if (acc != null) {
			ClueDesc[] oldDesc = acc.getClueSet().getClueDesc();
			HashMap<String, ClueDesc> m = new HashMap<>();
			for (int i = 0; i < oldDesc.length; ++i) {
				m.put(oldDesc[i].getName(), oldDesc[i]);
			}
			ClueDesc[] newDesc = newClueSet.getClueDesc();
			for (int i = 0; i < newDesc.length; ++i) {
				ClueDesc k = newDesc[i];
				ClueDesc o = m.get(k.getName());
				if (o != null) {
					int number = o.getNumber();
					newCluesToEvaluate[i] = cluesToEvaluate[number];
					oldClueNums[i] = number;
				} else {
					oldClueNums[i] = -1;
				}
			}
		} else {
			for (int i = 0; i < oldClueNums.length; ++i) {
				oldClueNums[i] = -1;
			}
		}
		cluesToEvaluate = newCluesToEvaluate;
		if (ml.canUse(newAcc.getClueSet())) {
			ml.changedAccessor(acc, newAcc, oldClueNums);
			setAccessorInternal(newAcc);
			computeDecisionDomainSize();
		} else {
			setAccessorInternal(newAcc);
			computeDecisionDomainSize();
			setMachineLearner(new DoNothingMachineLearning());
		}
		if (!multiPropertyChange) {
			propertyChangeListeners.firePropertyChange(null, oldAccessor, acc);
		}
	}

	private void setAccessorInternal(Accessor accessor)
			throws ModelConfigurationException {
		CMExtension[] exts =
			CMPlatformUtils
					.getExtensions(ChoiceMakerExtensionPoint.CM_CORE_ACCESSOR);
		Class<?>[] interfaces = new Class[exts.length];
		for (int i = 0; i < interfaces.length; i++) {
			CMExtension ext = exts[i];
			try {
				CMConfigurationElement[] configs =
					ext.getConfigurationElements();
				assert configs != null;
				if (configs.length < 1) {
					String msg =
						"No accessors configured for " + this.getModelName();
					throw new ModelConfigurationException(msg);
				}
				if (configs.length > 1) {
					String msg =
						"Multiple accessors configured for "
								+ this.getModelName();
					throw new ModelConfigurationException(msg);
				}
				CMConfigurationElement ce = configs[0];
				String clsName =
					ce.getAttribute(ChoiceMakerExtensionPoint.CM_CORE_ACCESSOR_ATTR_CLASS);
				CMPluginDescriptor descriptor =
					ext.getDeclaringPluginDescriptor();
				ClassLoader cl1 = descriptor.getPluginClassLoader();
				interfaces[i] = Class.forName(clsName, false, cl1);
			} catch (ClassNotFoundException e) {
				throw new ModelConfigurationException(e.toString(), e);
			}
		}
		Class<?> accessorClass = accessor.getClass();
		this.accessorClassName = accessor.getClass().getName();

		ClassLoader cl2 = accessorClass.getClassLoader();
		this.acc =
			(Accessor) Proxy.newProxyInstance(cl2, interfaces,
					new AccessorInvocationHandler(accessor));
	}

	/**
	 * Sets the clues to evaluate.
	 *
	 * @param cluesToEvaluate
	 *            The clues to evaluate.
	 */
	@Override
	public void setCluesToEvaluate(boolean[] cluesToEvaluate)
			throws IllegalArgumentException {
		ClueSet clueSet = getClueSet();
		if (clueSet != null) {
			if (cluesToEvaluate == null
					|| cluesToEvaluate.length != clueSet.size()) {
				throw new IllegalArgumentException("Illegal cluesToEvaluate.");
			}
		} else if (cluesToEvaluate != null) {
			throw new IllegalArgumentException("Illegal cluesToEvaluate.");
		}
		this.cluesToEvaluate = cluesToEvaluate;
		if (!multiPropertyChange) {
			propertyChangeListeners.firePropertyChange(CLUES_TO_EVALUATE, null,
					cluesToEvaluate);
		}
	}

	/**
	 * Set the value of enableAllCluesBeforeTraining.
	 * 
	 * @param v
	 *            Value to assign to enableAllCluesBeforeTraining.
	 */
	@Override
	public void setEnableAllCluesBeforeTraining(boolean v) {
		this.enableAllCluesBeforeTraining = v;
	}

	/**
	 * Set the value of enableAllRulesBeforeTraining.
	 * 
	 * @param v
	 *            Value to assign to enableAllRulesBeforeTraining.
	 */
	@Override
	public void setEnableAllRulesBeforeTraining(boolean v) {
		this.enableAllRulesBeforeTraining = v;
	}

	/**
	 * Sets the path to the probability model weights file (*.model)
	 * 
	 * If this model is in the collection of probability models, the
	 * {@link #getModelName() model configuration name} that is associated with
	 * in the collection is not changed.
	 * 
	 * @param path
	 *            The new file path.
	 */
	@Override
	public void setModelFilePath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("null model file path");
		} else {
			path = path.trim();
			if (path.isEmpty()) {
				throw new IllegalArgumentException("empty model file path");
			}
		}
		this.modelFilePath = path;
		String name = NameUtils.getNameFromFilePath(path);
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Null or empty model name from file path '" + path + "'");
		}
		setModelName(name);

		assert getModelFilePath() != null;
		assert getModelFilePath().equals(getModelFilePath().trim());
		assert !getModelFilePath().isEmpty();

		assert getModelName() != null;
		assert getModelName().equals(getModelName().trim());
		assert !getModelName().isEmpty();
	}

	/**
	 * Set the value of firingThreshold.
	 * 
	 * @param v
	 *            Value to assign to firingThreshold.
	 */
	@Override
	public void setFiringThreshold(int v) {
		this.firingThreshold = v;
	}

	/**
	 * Set the value of lastTrainingDate.
	 * 
	 * @param v
	 *            Value to assign to lastTrainingDate.
	 */
	@Override
	public void setLastTrainingDate(Date v) {
		this.lastTrainingDate = v;
	}

	@Override
	public void setMachineLearner(MachineLearner ml) {
		MachineLearner old = this.ml;
		this.ml = ml;
		ml.setProbabilityModel(this);
		if (!multiPropertyChange) {
			propertyChangeListeners
					.firePropertyChange(MACHINE_LEARNER, old, ml);
		}
	}

	public void setModelName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("null model name");
		} else {
			name = name.trim();
			if (name.isEmpty()) {
				throw new IllegalArgumentException("blank model name");
			}
		}
 		String oldName = this.modelName;
		this.modelName = name;
		if (!multiPropertyChange) {
			propertyChangeListeners.firePropertyChange(NAME, oldName, name);
		}
	}

	@Override
	public void setClueFilePath(String fn) {
		this.clueFilePath = fn;
		if (fn != null && modelFilePath != null) {
			this.clueFile =
				FileUtilities.getAbsoluteFile(
						new File(modelFilePath).getParentFile(), fn);
		} else if (fn != null) {
			this.clueFile = new File(fn).getAbsoluteFile();
		} else {
			this.clueFile = null;
		}
	}

	@Override
	public void setTrainedWithHolds(boolean b) {
		trainedWithHolds = b;
	}

	/**
	 * Set the value of trainingSource.
	 * 
	 * @param v
	 *            Value to assign to trainingSource.
	 */
	@Override
	public void setTrainingSource(String v) {
		this.trainingSource = v;
	}

	/**
	 * Set the value of userName.
	 * 
	 * @param v
	 *            Value to assign to userName.
	 */
	@Override
	public void setUserName(String v) {
		this.userName = v;
	}

	@Override
	public String getClueSetName() {
		return this.getAccessor().getClueSetName();
	}

	// NOT YET IMPLEMENTED (as of version 2.7.1)
	// public String getEntityInterfaceName() {
	// return null;
	// }

	@Override
	public String getSchemaName() {
		return this.getAccessor().getSchemaName();
	}

	@Override
	public String getClueSetSignature() {
		return Signature.calculateClueSetSignature(this.getClueSet());
	}

	@Override
	public String getModelSignature() {
		return Signature.calculateModelSignature(this);
	}

	@Override
	public String getSchemaSignature() {
		Descriptor d = this.getAccessor().getDescriptor();
		return Signature.calculateRecordLayoutSignature(d);
	}

	@Override
	public String getEvaluatorSignature() {
		Evaluator e = getEvaluator();
		return e == null ? "" : e.getSignature();
	}

	// @Override
	@Override
	public String toString() {
		return "ProbabilityModel [modelName=" + modelName + ", clueFile="
				+ clueFilePath + "]";
	}

}
