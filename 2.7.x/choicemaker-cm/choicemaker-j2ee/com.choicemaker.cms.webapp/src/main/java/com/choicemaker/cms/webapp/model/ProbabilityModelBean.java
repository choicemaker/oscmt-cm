package com.choicemaker.cms.webapp.model;

import java.util.Date;

/**
 * Serializable representation of an immutable probability model. Only a subset
 * of the methods defined by
 * {@link com.choicemaker.cm.core.ImmutableProbabilityModel
 * ImmutableProbabilityModel} are implemented.
 * 
 * @author rphall
 *
 */
public class ProbabilityModelBean {

	 private String _accessorClassName;
	 private String _clueSetName;
	 private String _clueSetSignature;
	 private String _evaluatorSignature;
	 private int _firingThreshold;
	 private Date _lastTrainingDate;
	 private String _modelName;
	 private String _modelSignature;
	 private String _schemaName;
	 private String _schemaSignature;
	 private String _trainingSource;
	 private String _userName;

	public String getAccessorClassName() {
		return _accessorClassName;
	}

	protected void setAccessorClassName(String accessorClassName) {
		_accessorClassName = accessorClassName;
	}

	public String getClueSetName() {
		return _clueSetName;
	}

	protected void setClueSetName(String clueSetName) {
		_clueSetName = clueSetName;
	}

	public String getClueSetSignature() {
		return _clueSetSignature;
	}

	protected void setClueSetSignature(String clueSetSignature) {
		_clueSetSignature = clueSetSignature;
	}

	public String getEvaluatorSignature() {
		return _evaluatorSignature;
	}

	protected void setEvaluatorSignature(String evaluatorSignature) {
		_evaluatorSignature = evaluatorSignature;
	}

	public int getFiringThreshold() {
		return _firingThreshold;
	}

	protected void setFiringThreshold(int firingThreshold) {
		_firingThreshold = firingThreshold;
	}

	public Date getLastTrainingDate() {
		return _lastTrainingDate;
	}

	protected void setLastTrainingDate(Date lastTrainingDate) {
		_lastTrainingDate = lastTrainingDate;
	}

	public String getModelName() {
		return _modelName;
	}

	protected void setModelName(String modelName) {
		_modelName = modelName;
	}

	public String getModelSignature() {
		return _modelSignature;
	}

	protected void setModelSignature(String modelSignature) {
		_modelSignature = modelSignature;
	}

	public String getSchemaName() {
		return _schemaName;
	}

	protected void setSchemaName(String schemaName) {
		_schemaName = schemaName;
	}

	public String getSchemaSignature() {
		return _schemaSignature;
	}

	protected void setSchemaSignature(String schemaSignature) {
		_schemaSignature = schemaSignature;
	}

	public String getTrainingSource() {
		return _trainingSource;
	}

	protected void setTrainingSource(String trainingSource) {
		_trainingSource = trainingSource;
	}

	public String getUserName() {
		return _userName;
	}

	protected void setUserName(String userName) {
		_userName = userName;
	}

}
