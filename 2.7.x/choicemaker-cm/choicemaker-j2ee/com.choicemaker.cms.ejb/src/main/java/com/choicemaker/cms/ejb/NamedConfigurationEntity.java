package com.choicemaker.cms.ejb;

import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_BKCONF;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_CM_IO_CLASS;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_CONF_NAME;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_CONF_DESCRIPTION;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_DATASOURCE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_FILE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_FORMAT;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_GRAPH;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_HIGH_THRESHOLD;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_ID;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_INTERVAL;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_ABA_MAX_MATCHES;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_LIMIT_BLOCKSET;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_LIMIT_SINGLESET;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_LIMIT_SINGLETABLE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_LOW_THRESHOLD;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAXCHUNKCOUNT;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAXCHUNKSIZE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAXTHREADS;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAX_BLOCKSIZE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAX_CHUNKSIZE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAX_MATCHES;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAX_OVERSIZE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MAX_SINGLE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MIN_FIELDS;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_MODEL;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_QUERY_DBCONF;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_QUERY_IS_DEDUPED;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_QUERY_SQL;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_REF_DBACCESSOR;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_REF_DBCONF;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_REF_DBREADER;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_REF_SQL;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_RS_TYPE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.CN_TASK;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.JPQL_NAMEDCONFIG_FIND_ALL;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.JPQL_NAMEDCONFIG_FIND_BY_NAME;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.QN_NAMEDCONFIG_FIND_ALL;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.QN_NAMEDCONFIG_FIND_BY_NAME;
import static com.choicemaker.cms.ejb.NamedConfigurationJPA.TABLE_NAME;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.batch.ejb.AbstractPersistentObject;
import com.choicemaker.cms.api.NamedConfiguration;

@NamedQueries({
		@NamedQuery(name = QN_NAMEDCONFIG_FIND_ALL,
				query = JPQL_NAMEDCONFIG_FIND_ALL),
		@NamedQuery(name = QN_NAMEDCONFIG_FIND_BY_NAME,
				query = JPQL_NAMEDCONFIG_FIND_BY_NAME) })
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class NamedConfigurationEntity extends AbstractPersistentObject
		implements Serializable, NamedConfiguration {

	private static final long serialVersionUID = 271L;

	// -- Basic persistence

	@Id
	@Column(name = CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	protected long configurationId = DEFAULT_CONFIGURATIONID;

	@Column(name = CN_CONF_NAME, unique = true, nullable = false)
	protected String configurationName = DEFAULT_CONFIGURATIONNAME;

	@Column(name = CN_CONF_DESCRIPTION, unique = false, nullable = true)
	protected String configurationDescription = DEFAULT_CONFIGURATIONDESC;


	// -- Basic matching

	@Column(name = CN_MODEL)
	protected String modelName = DEFAULT_MODELNAME;

	@Column(name = CN_LOW_THRESHOLD)
	protected float lowThreshold = DEFAULT_LOWTHRESHOLD;

	@Column(name = CN_HIGH_THRESHOLD)
	protected float highThreshold = DEFAULT_HIGHTHRESHOLD;

	@Column(name = CN_TASK)
	protected String task = DEFAULT_TASK;

//	@Column(name = CN_RIGOR)
//	protected String rigor = DEFAULT_RIGOR;

	// -- Common record source parameters

	@Column(name = CN_RS_TYPE)
	protected final String recordSourceType = DEFAULT_RS_TYPE;

	@Column(name = CN_DATASOURCE)
	protected String dataSource = DEFAULT_DATASOURCE;

	@Column(name = CN_CM_IO_CLASS)
	protected String jdbcDriverClassName = DEFAULT_JDBCDRIVERCLASSNAME;

	@Column(name = CN_BKCONF)
	protected String blockingConfiguration = DEFAULT_BLOCKINGCONFIGURATION;

	// -- Query record source parameters

	@Column(name = CN_QUERY_SQL)
	protected String querySelection = DEFAULT_QUERYSELECTION;

	@Column(name = CN_QUERY_DBCONF)
	protected String queryDatabaseConfiguration =
		DEFAULT_QUERYDATABASECONFIGURATION;

	@Column(name = CN_QUERY_IS_DEDUPED)
	protected boolean queryIsDeduplicated = DEFAULT_QUERYISDEDUPLICATED;

	// -- Reference record source parameters

	@Column(name = CN_REF_SQL)
	protected String referenceSelection = DEFAULT_REFERENCESELECTION;

	@Column(name = CN_REF_DBCONF)
	protected String referenceDatabaseConfiguration =
		DEFAULT_REFERENCEDATABASECONFIGURATION;

	@Column(name = CN_REF_DBACCESSOR)
	protected String referenceDatabaseAccessor =
		DEFAULT_REFERENCEDATABASEACCESSOR;

	@Column(name = CN_REF_DBREADER)
	protected String referenceDatabaseReader =
		DEFAULT_REFERENCEDATABASEREADER;

	// -- Transitivity analysis parameters

	@Column(name = CN_FORMAT)
	protected String transitivityFormat = DEFAULT_TRANSITIVITYFORMAT;

	@Column(name = CN_GRAPH)
	protected String transitivityGraph = DEFAULT_TRANSITIVITYGRAPH;

	// -- ABA settings
	
	@Column(name = CN_ABA_MAX_MATCHES)
	protected int abaMaxMatches = DEFAULT_ABAMAXMATCHES;

	@Column(name = CN_LIMIT_BLOCKSET)
	protected int abaLimitPerBlockingSet = DEFAULT_ABALIMITPERBLOCKINGSET;

	@Column(name = CN_LIMIT_SINGLESET)
	protected int abaLimitSingleBlockingSet = DEFAULT_ABALIMITSINGLEBLOCKINGSET;

	@Column(name = CN_LIMIT_SINGLETABLE)
	protected int abaSingleTableBlockingSetGraceLimit =
		DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT;

	// -- OABA settings

	@Column(name = CN_MAX_SINGLE)
	protected int oabaMaxSingle = DEFAULT_OABAMAXSINGLE;

	@Column(name = CN_MAX_BLOCKSIZE)
	protected int oabaMaxBlockSize = DEFAULT_OABAMAXBLOCKSIZE;

	@Column(name = CN_MAX_CHUNKSIZE)
	protected int oabaMaxChunkSize = DEFAULT_OABAMAXCHUNKSIZE;

	@Column(name = CN_MAX_OVERSIZE)
	protected int oabaMaxOversized = DEFAULT_OABAMAXOVERSIZED;

	@Column(name = CN_MAX_MATCHES)
	protected int oabaMaxMatches = DEFAULT_OABAMAXMATCHES;

	@Column(name = CN_MIN_FIELDS)
	protected int oabaMinFields = DEFAULT_OABAMINFIELDS;

	@Column(name = CN_INTERVAL)
	protected int oabaInterval = DEFAULT_OABAINTERVAL;

	// -- Server settings

	@Column(name = CN_MAXTHREADS)
	protected int serverMaxThreads = DEFAULT_SERVERMAXTHREADS;

	@Column(name = CN_MAXCHUNKSIZE)
	protected int serverMaxChunkSize = DEFAULT_SERVERMAXCHUNKSIZE;

	@Column(name = CN_MAXCHUNKCOUNT)
	protected int serverMaxChunkCount = DEFAULT_SERVERMAXCHUNKCOUNT;

	@Column(name = CN_FILE)
	protected String serverFileURI = DEFAULT_SERVERFILEURI;

	public NamedConfigurationEntity() {
	}

	public NamedConfigurationEntity(NamedConfiguration nc) {
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
		this.setReferenceDatabaseConfiguration(nc
				.getReferenceDatabaseConfiguration());
		this.setReferenceDatabaseAccessor(nc
				.getReferenceDatabaseAccessor());
		this.setTransitivityFormat(nc.getTransitivityFormat());
		this.setTransitivityGraph(nc.getTransitivityGraph());
		this.setAbaMaxMatches(nc.getAbaMaxMatches());
		this.setAbaLimitPerBlockingSet(nc.getAbaLimitPerBlockingSet());
		this.setAbaLimitSingleBlockingSet(nc.getAbaLimitSingleBlockingSet());
		this.setAbaSingleTableBlockingSetGraceLimit(nc
				.getAbaSingleTableBlockingSetGraceLimit());
		this.setOabaMaxSingle(nc.getOabaMaxSingle());
		this.setOabaMaxBlockSize(nc.getOabaMaxBlockSize());
		this.setOabaMaxChunkSize(nc.getOabaMaxChunkSize());
		this.setOabaMaxOversized(nc.getOabaMaxOversized());
		this.setOabaMaxMatches(nc.getOabaMaxMatches());
		this.setOabaMinFields(nc.getOabaMinFields());
		this.setOabaInterval(nc.getOabaInterval());
		this.setServerMaxThreads(nc.getServerMaxThreads());
		this.setServerMaxChunkSize(nc.getServerMaxChunkSize());
		this.setServerMaxChunkCount(nc.getServerMaxChunkCount());
		this.setServerFileURI(nc.getServerFileURI());
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
//		return rigor;
		return null;
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
	public int getServerMaxChunkSize() {
		return serverMaxChunkSize;
	}

	@Override
	public int getServerMaxChunkCount() {
		return serverMaxChunkCount;
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
//		this.rigor = rigor;
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

	public void setQueryDatabaseConfiguration(String queryDatabaseConfiguration) {
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

	public void setReferenceDatabaseAccessor(
			String rdba) {
		this.referenceDatabaseAccessor = rdba;
	}

	public void setReferenceDatabaseReader(
			String rdbr) {
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

	public void setServerMaxChunkSize(int serverMaxChunkSize) {
		this.serverMaxChunkSize = serverMaxChunkSize;
	}

	public void setServerMaxChunkCount(int serverMaxChunkCount) {
		this.serverMaxChunkCount = serverMaxChunkCount;
	}

	public void setServerFileURI(String serverFileURI) {
		this.serverFileURI = serverFileURI;
	}

	// -- Identity

	public boolean equalsIgnoreIdentityFields(NamedConfiguration nc) {
		if (this == nc) {
			return true;
		}
		if (nc == null) {
			return false;
		}
		if (abaLimitPerBlockingSet != nc.getAbaLimitPerBlockingSet()) {
			return false;
		}
		if (abaLimitSingleBlockingSet != nc.getAbaLimitSingleBlockingSet()) {
			return false;
		}
		if (abaSingleTableBlockingSetGraceLimit != nc
				.getAbaSingleTableBlockingSetGraceLimit()) {
			return false;
		}
		if (blockingConfiguration == null) {
			if (nc.getBlockingConfiguration() != null) {
				return false;
			}
		} else if (!blockingConfiguration.equals(nc.getBlockingConfiguration())) {
			return false;
		}
		// if (configurationId != nc.getId()) {
		// return false;
		// }
		if (configurationName == null) {
			if (nc.getConfigurationName() != null) {
				return false;
			}
		} else if (!configurationName.equals(nc.getConfigurationName())) {
			return false;
		}
		if (dataSource == null) {
			if (nc.getDataSource() != null) {
				return false;
			}
		} else if (!dataSource.equals(nc.getDataSource())) {
			return false;
		}
		if (Float.floatToIntBits(highThreshold) != Float.floatToIntBits(nc
				.getHighThreshold())) {
			return false;
		}
		if (jdbcDriverClassName == null) {
			if (nc.getJdbcDriverClassName() != null) {
				return false;
			}
		} else if (!jdbcDriverClassName.equals(nc.getJdbcDriverClassName())) {
			return false;
		}
		if (Float.floatToIntBits(lowThreshold) != Float.floatToIntBits(nc
				.getLowThreshold())) {
			return false;
		}
		if (modelName == null) {
			if (nc.getModelName() != null) {
				return false;
			}
		} else if (!modelName.equals(nc.getModelName())) {
			return false;
		}
		if (oabaInterval != nc.getOabaInterval()) {
			return false;
		}
		if (oabaMaxBlockSize != nc.getOabaMaxBlockSize()) {
			return false;
		}
		if (oabaMaxChunkSize != nc.getOabaMaxChunkSize()) {
			return false;
		}
		if (oabaMaxMatches != nc.getOabaMaxMatches()) {
			return false;
		}
		if (oabaMaxOversized != nc.getOabaMaxOversized()) {
			return false;
		}
		if (oabaMaxSingle != nc.getOabaMaxSingle()) {
			return false;
		}
		if (oabaMinFields != nc.getOabaMinFields()) {
			return false;
		}
		if (queryDatabaseConfiguration == null) {
			if (nc.getQueryDatabaseConfiguration() != null) {
				return false;
			}
		} else if (!queryDatabaseConfiguration.equals(nc
				.getQueryDatabaseConfiguration())) {
			return false;
		}
		if (queryIsDeduplicated != nc.isQueryDeduplicated()) {
			return false;
		}
		if (querySelection == null) {
			if (nc.getQuerySelection() != null) {
				return false;
			}
		} else if (!querySelection.equals(nc.getQuerySelection())) {
			return false;
		}
		if (recordSourceType == null) {
			if (nc.getRecordSourceType() != null) {
				return false;
			}
		} else if (!recordSourceType.equals(nc.getRecordSourceType())) {
			return false;
		}
		if (referenceDatabaseConfiguration == null) {
			if (nc.getReferenceDatabaseConfiguration() != null) {
				return false;
			}
		} else if (!referenceDatabaseConfiguration.equals(nc
				.getReferenceDatabaseConfiguration())) {
			return false;
		}
		if (referenceDatabaseAccessor == null) {
			if (nc.getReferenceDatabaseAccessor() != null) {
				return false;
			}
		} else if (!referenceDatabaseAccessor.equals(nc
				.getReferenceDatabaseAccessor())) {
			return false;
		}
		if (referenceSelection == null) {
			if (nc.getReferenceSelection() != null) {
				return false;
			}
		} else if (!referenceSelection.equals(nc.getReferenceSelection())) {
			return false;
		}
//		if (rigor == null) {
//			if (nc.getRigor() != null) {
//				return false;
//			}
//		} else if (!rigor.equals(nc.getRigor())) {
//			return false;
//		}
		if (serverFileURI == null) {
			if (nc.getServerFileURI() != null) {
				return false;
			}
		} else if (!serverFileURI.equals(nc.getServerFileURI())) {
			return false;
		}
		if (serverMaxChunkCount != nc.getServerMaxChunkCount()) {
			return false;
		}
		if (serverMaxChunkSize != nc.getServerMaxChunkSize()) {
			return false;
		}
		if (serverMaxThreads != nc.getServerMaxThreads()) {
			return false;
		}
		if (task == null) {
			if (nc.getTask() != null) {
				return false;
			}
		} else if (!task.equals(nc.getTask())) {
			return false;
		}
		if (transitivityFormat == null) {
			if (nc.getTransitivityFormat() != null) {
				return false;
			}
		} else if (!transitivityFormat.equals(nc.getTransitivityFormat())) {
			return false;
		}
		if (transitivityGraph == null) {
			if (nc.getTransitivityGraph() != null) {
				return false;
			}
		} else if (!transitivityGraph.equals(nc.getTransitivityGraph())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NamedConfigurationEntity [getUUID()=" + getUUID()
				+ getConfigurationName() + ", getId()=" + getId() + "]";
	}

}
