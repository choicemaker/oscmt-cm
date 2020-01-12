package com.choicemaker.cms.webapp.model;

import java.io.Serializable;

import com.choicemaker.cms.api.NamedConfiguration;

public class NamedConfigurationBean
		implements Serializable, NamedConfiguration {

	private static final long serialVersionUID = 271L;

	protected long configurationId;
	protected String configurationName;
	protected String configurationDescription;
	protected String modelName;
	protected float lowThreshold;
	protected float highThreshold;
	protected String task;
	protected String rigor;
	protected String recordSourceType;
	protected String dataSource;
	protected String jdbcDriverClassName;
	protected String blockingConfiguration;
	protected String querySelection;
	protected String queryDatabaseConfiguration;
	protected boolean queryIsDeduplicated;
	protected String referenceSelection;
	protected String referenceDatabaseConfiguration;
	protected String referenceDatabaseAccessor;
	protected String referenceDatabaseReader;
	protected String transitivityFormat;
	protected String transitivityGraph;
	protected int abaMaxMatches;
	protected int abaLimitPerBlockingSet;
	protected int abaLimitSingleBlockingSet;
	protected int abaSingleTableBlockingSetGraceLimit;
	protected int oabaMaxSingle;
	protected int oabaMaxBlockSize;
	protected int oabaMaxChunkSize;
	protected int oabaMaxOversized;
	protected int oabaMaxMatches;
	protected int oabaMinFields;
	protected int oabaInterval;
	protected int serverMaxThreads;
	protected int serverMaxFileEntries;
	protected int serverMaxFilesCount;
	protected String serverFileURI;

	public NamedConfigurationBean() {
	}

	public NamedConfigurationBean(NamedConfiguration nc) {
		this.setConfigurationName(nc.getConfigurationName());
		this.setConfigurationDescription(nc.getConfigurationDescription());
		this.setModelName(nc.getModelName());
		this.setLowThreshold(nc.getLowThreshold());
		this.setHighThreshold(nc.getHighThreshold());
		this.setTask(nc.getTask());
		this.setRigor(nc.getRigor());
		this.setDataSource(nc.getDataSource());
		this.setJdbcDriverClassName(nc.getJdbcDriverClassName());
		this.setBlockingConfiguration(nc.getBlockingConfiguration());
		this.setQuerySelection(nc.getQuerySelection());
		this.setQueryDatabaseConfiguration(nc.getQueryDatabaseConfiguration());
		this.setQueryDeduplicated(nc.isQueryDeduplicated());
		this.setReferenceSelection(nc.getReferenceSelection());
		this.setReferenceDatabaseConfiguration(
				nc.getReferenceDatabaseConfiguration());
		this.setReferenceDatabaseAccessor(nc.getReferenceDatabaseAccessor());
		this.setTransitivityFormat(nc.getTransitivityFormat());
		this.setTransitivityGraph(nc.getTransitivityGraph());
		this.setAbaMaxMatches(nc.getAbaMaxMatches());
		this.setAbaLimitPerBlockingSet(nc.getAbaLimitPerBlockingSet());
		this.setAbaLimitSingleBlockingSet(nc.getAbaLimitSingleBlockingSet());
		this.setAbaSingleTableBlockingSetGraceLimit(
				nc.getAbaSingleTableBlockingSetGraceLimit());
		this.setOabaMaxSingle(nc.getOabaMaxSingle());
		this.setOabaMaxBlockSize(nc.getOabaMaxBlockSize());
		this.setOabaMaxChunkSize(nc.getOabaMaxChunkSize());
		this.setOabaMaxOversized(nc.getOabaMaxOversized());
		this.setOabaMaxMatches(nc.getOabaMaxMatches());
		this.setOabaMinFields(nc.getOabaMinFields());
		this.setOabaInterval(nc.getOabaInterval());
		this.setServerMaxThreads(nc.getServerMaxThreads());
		this.setServerMaxChunkSize(nc.getServerMaxFileEntries());
		this.setServerMaxChunkCount(nc.getServerMaxFilesCount());
		this.setServerFileURI(nc.getServerFileURI());
		this.setReferenceDatabaseReader(nc.getReferenceDatabaseReader());
	}

	// -- Accessors

	@Override
	public long getId() {
		return configurationId;
	}

	@Override
	public String getConfigurationName() {
		return configurationName;
	}

	@Override
	public String getConfigurationDescription() {
		return configurationDescription;
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public float getLowThreshold() {
		return lowThreshold;
	}

	@Override
	public float getHighThreshold() {
		return highThreshold;
	}

	@Override
	public String getTask() {
		return task;
	}

	@Override
	public String getRigor() {
		return rigor;
	}

	@Override
	public String getRecordSourceType() {
		return recordSourceType;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}

	@Override
	public String getJdbcDriverClassName() {
		return jdbcDriverClassName;
	}

	@Override
	public String getBlockingConfiguration() {
		return blockingConfiguration;
	}

	@Override
	public String getQuerySelection() {
		return querySelection;
	}

	@Override
	public String getQueryDatabaseConfiguration() {
		return queryDatabaseConfiguration;
	}

	@Override
	public boolean isQueryDeduplicated() {
		return queryIsDeduplicated;
	}

	@Override
	public String getReferenceSelection() {
		return referenceSelection;
	}

	@Override
	public String getReferenceDatabaseConfiguration() {
		return referenceDatabaseConfiguration;
	}

	@Override
	public String getReferenceDatabaseAccessor() {
		return referenceDatabaseAccessor;
	}

	@Override
	public String getReferenceDatabaseReader() {
		return referenceDatabaseReader;
	}

	@Override
	public String getTransitivityFormat() {
		return transitivityFormat;
	}

	@Override
	public String getTransitivityGraph() {
		return transitivityGraph;
	}

	@Override
	public int getAbaMaxMatches() {
		return abaMaxMatches;
	}

	@Override
	public int getAbaLimitPerBlockingSet() {
		return abaLimitPerBlockingSet;
	}

	@Override
	public int getAbaLimitSingleBlockingSet() {
		return abaLimitSingleBlockingSet;
	}

	@Override
	public int getAbaSingleTableBlockingSetGraceLimit() {
		return abaSingleTableBlockingSetGraceLimit;
	}

	@Override
	public int getOabaMaxSingle() {
		return oabaMaxSingle;
	}

	@Override
	public int getOabaMaxBlockSize() {
		return oabaMaxBlockSize;
	}

	@Override
	public int getOabaMaxChunkSize() {
		return oabaMaxChunkSize;
	}

	@Override
	public int getOabaMaxOversized() {
		return oabaMaxOversized;
	}

	@Override
	public int getOabaMaxMatches() {
		return oabaMaxMatches;
	}

	@Override
	public int getOabaMinFields() {
		return oabaMinFields;
	}

	@Override
	public int getOabaInterval() {
		return oabaInterval;
	}

	@Override
	public int getServerMaxThreads() {
		return serverMaxThreads;
	}

	@Override
	public int getServerMaxFileEntries() {
		return serverMaxFileEntries;
	}

	@Override
	public int getServerMaxFilesCount() {
		return serverMaxFilesCount;
	}

	@Override
	public String getServerFileURI() {
		return serverFileURI;
	}

	// -- Manipulators

	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}

	public void setConfigurationDescription(String configurationDescription) {
		this.configurationDescription = configurationDescription;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public void setLowThreshold(float lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public void setHighThreshold(float highThreshold) {
		this.highThreshold = highThreshold;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public void setRigor(String rigor) {
		this.rigor = rigor;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public void setJdbcDriverClassName(String jdbcDriverClassName) {
		this.jdbcDriverClassName = jdbcDriverClassName;
	}

	public void setBlockingConfiguration(String blockingConfiguration) {
		this.blockingConfiguration = blockingConfiguration;
	}

	public void setQuerySelection(String querySelection) {
		this.querySelection = querySelection;
	}

	public void setQueryDatabaseConfiguration(
			String queryDatabaseConfiguration) {
		this.queryDatabaseConfiguration = queryDatabaseConfiguration;
	}

	public void setQueryDeduplicated(boolean queryIsDeduplicated) {
		this.queryIsDeduplicated = queryIsDeduplicated;
	}

	public void setReferenceSelection(String referenceSelection) {
		this.referenceSelection = referenceSelection;
	}

	public void setReferenceDatabaseConfiguration(
			String referenceDatabaseConfiguration) {
		this.referenceDatabaseConfiguration = referenceDatabaseConfiguration;
	}

	public void setReferenceDatabaseAccessor(String rdba) {
		this.referenceDatabaseAccessor = rdba;
	}

	public void setReferenceDatabaseReader(String rdbr) {
		this.referenceDatabaseReader = rdbr;
	}

	public void setTransitivityFormat(String transitivityFormat) {
		this.transitivityFormat = transitivityFormat;
	}

	public void setTransitivityGraph(String transitivityGraph) {
		this.transitivityGraph = transitivityGraph;
	}

	public void setAbaMaxMatches(int abaMaxMatches) {
		this.abaMaxMatches = abaMaxMatches;
	}

	public void setAbaLimitPerBlockingSet(int abaLimitPerBlockingSet) {
		this.abaLimitPerBlockingSet = abaLimitPerBlockingSet;
	}

	public void setAbaLimitSingleBlockingSet(int abaLimitSingleBlockingSet) {
		this.abaLimitSingleBlockingSet = abaLimitSingleBlockingSet;
	}

	public void setAbaSingleTableBlockingSetGraceLimit(
			int abaSingleTableBlockingSetGraceLimit) {
		this.abaSingleTableBlockingSetGraceLimit =
			abaSingleTableBlockingSetGraceLimit;
	}

	public void setOabaMaxSingle(int oabaMaxSingle) {
		this.oabaMaxSingle = oabaMaxSingle;
	}

	public void setOabaMaxBlockSize(int oabaMaxBlockSize) {
		this.oabaMaxBlockSize = oabaMaxBlockSize;
	}

	public void setOabaMaxChunkSize(int oabaMaxChunkSize) {
		this.oabaMaxChunkSize = oabaMaxChunkSize;
	}

	public void setOabaMaxOversized(int oabaMaxOversized) {
		this.oabaMaxOversized = oabaMaxOversized;
	}

	public void setOabaMaxMatches(int oabaMaxMatches) {
		this.oabaMaxMatches = oabaMaxMatches;
	}

	public void setOabaMinFields(int oabaMinFields) {
		this.oabaMinFields = oabaMinFields;
	}

	public void setOabaInterval(int oabaInterval) {
		this.oabaInterval = oabaInterval;
	}

	public void setServerMaxThreads(int serverMaxThreads) {
		this.serverMaxThreads = serverMaxThreads;
	}

	public void setServerMaxFileEntries(int serverMaxFileEntries) {
		this.serverMaxFileEntries = serverMaxFileEntries;
	}

	@Deprecated
	public void setServerMaxChunkSize(int serverMaxChunkSize) {
		setServerMaxFileEntries(serverMaxChunkSize);
	}

	public void setServerMaxFilesCount(int serverMaxFilesCount) {
		this.serverMaxFilesCount = serverMaxFilesCount;
	}

	@Deprecated
	public void setServerMaxChunkCount(int serverMaxChunkCount) {
		setServerMaxFilesCount(serverMaxChunkCount);
	}

	public void setServerFileURI(String serverFileURI) {
		this.serverFileURI = serverFileURI;
	}

	// -- Identity

	@Override
	public String toString() {
		return "NamedConfigurationBean [" + getId() + ", "
				+ getConfigurationName() + "]";
	}

}
