package com.choicemaker.cms.ejb;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.api.SqlRecordSourceController;
import com.choicemaker.cm.oaba.ejb.OabaParametersEntity;
import com.choicemaker.cm.oaba.ejb.OabaSettingsEntity;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationControllerBean;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationEntity;
import com.choicemaker.cm.oaba.ejb.SqlRecordSourceEntity;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cm.transitivity.ejb.TransitivityParametersEntity;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.beans.AbaParametersBean;
import com.choicemaker.cms.beans.AbaServerConfigurationBean;
import com.choicemaker.cms.beans.AbaSettingsBean;
import com.choicemaker.util.Precondition;

public class NamedConfigConversion {
	
	// Implementation note: Persistent NamedConfiguration entities are
	// currently immutable; the API does not provide a way to change the
	// any field of an NamedConfiguration once it has been persisted to
	// the database. (There are workarounds, but these are corner cases.)
	// Therefore the results of the various "create" operations below could
	// be cached.
	//
	// FIXME: Caching of "create" operations is not implemented for persistent
	// NamedConfiguration entities. (Caches should be keyed by id, for i>0,
	// rather than name?)

	private static final Logger logger =
		Logger.getLogger(NamedConfigConversion.class.getName());

	public static final String JNDI_TRANS_PARAMS_CTL =
		"java:app/com.choicemaker.cm.transitivity.ejb/TransitivityParametersControllerBean";

	public static final String JNDI_OABA_PARAMS_CTL =
		"java:app/com.choicemaker.cm.oaba.ejb/OabaParametersControllerBean";

	public static final String JNDI_SERVER_CTL =
		"java:app/com.choicemaker.cm.oaba.ejb/ServerConfigurationControllerBean";

	public static final String JNDI_SQL_RS_CTL =
		"java:app/com.choicemaker.cm.oaba.ejb/SqlRecordSourceControllerBean";

	public static final String JNDI_SETTINGS_CTL =
		"java:app/com.choicemaker.cm.oaba.ejb/OabaSettingsControllerBean";

	public static AbaParameters createAbaParameters(NamedConfiguration nc) {
		Precondition.assertNonNullArgument("null named configuration", nc);
		AbaParametersBean retVal = new AbaParametersBean();
		retVal.setDatabaseAccessorName(nc.getReferenceDatabaseAccessor());
		retVal.setDatabaseReaderName(nc.getReferenceDatabaseReader());
		retVal.setHighThreshold(nc.getHighThreshold());
		retVal.setLowThreshold(nc.getLowThreshold());
		retVal.setModelConfigurationName(nc.getModelName());
		retVal.setQueryToReferenceBlockingConfiguration(
				nc.getBlockingConfiguration());
		retVal.setReferenceDatabaseConfiguration(
				nc.getReferenceDatabaseConfiguration());
		retVal.setReferenceDatasource(nc.getDataSource());
		retVal.setReferenceSelectionView(nc.getReferenceSelection());
		final String HACK2 = "FIXME: FAKE VIEW NAME";
		retVal.setReferenceSelectionViewAsSQL(HACK2,
				nc.getReferenceSelection());
		return retVal;
	}

	public static AbaServerConfiguration createAbaServerConfiguration(
			NamedConfiguration nc) {
		Precondition.assertNonNullArgument("null named configuration", nc);
		AbaServerConfigurationBean retVal = new AbaServerConfigurationBean();
		retVal.setAbaMaxThreadCount(nc.getServerMaxThreads());
		retVal.setAbaMinThreadCount(1);
		return retVal;
	}

	public static AbaSettings createAbaSettings(NamedConfiguration nc) {
		Precondition.assertNonNullArgument("null named configuration", nc);
		AbaSettingsBean retVal = new AbaSettingsBean();
		retVal.setAbaMaxMatches(nc.getAbaMaxMatches());
		retVal.setLimitPerBlockingSet(nc.getAbaLimitPerBlockingSet());
		retVal.setLimitSingleBlockingSet(nc.getAbaLimitSingleBlockingSet());
		retVal.setSingleTableBlockingSetGraceLimit(
				nc.getAbaSingleTableBlockingSetGraceLimit());
		return retVal;
	}

	public static OabaParameters createOabaParameters(NamedConfiguration nc,
			boolean isLinkage) throws NamingException {
		boolean makePersistent = true;
		return createOabaParameters(nc, isLinkage, makePersistent);
	}

	public static OabaParameters createOabaParameters(NamedConfiguration nc,
			boolean isLinkage, boolean makePersistent) throws NamingException {

		final boolean isQueryDeduped = false;
		logger.info("isQueryDeduped: " + isQueryDeduped);
		final String modelConfigurationName = nc.getModelName();
		logger.info("modelName: " + modelConfigurationName);
		final float lowThreshold = nc.getLowThreshold();
		logger.info("lowThreshold: " + lowThreshold);
		final float highThreshold = nc.getHighThreshold();
		logger.info("highThreshold: " + highThreshold);

		// Common record source parameters
		final String dataSource = nc.getDataSource();
		logger.info("dataSource: " + dataSource);
		final String rsClassName = nc.getJdbcDriverClassName();
		logger.info("rsClassName: " + rsClassName);
		final String blockingConfig = nc.getBlockingConfiguration();
		logger.info("blockingConfig: " + blockingConfig);

		// Query record source
		final String queryDbConfig = nc.getQueryDatabaseConfiguration();
		logger.info("queryDbConfiguration: " + queryDbConfig);
		final String querySelection = nc.getQuerySelection();
		logger.info("querySelection: " + querySelection);
		PersistableRecordSource queryRs = new SqlRecordSourceEntity(rsClassName,
				dataSource, modelConfigurationName, querySelection,
				queryDbConfig, null);

		// Linkage task and reference record source
		final OabaLinkageType task;
		final String refDbConfig;
		final String refDatabaseAccessor;
		final String refSelection;
		PersistableRecordSource refRs;
		if (isLinkage) {
			task = OabaLinkageType.STAGING_TO_MASTER_LINKAGE;
			refDbConfig = nc.getReferenceDatabaseConfiguration();
			refDatabaseAccessor = nc.getReferenceDatabaseAccessor();
			refSelection = nc.getReferenceSelection();
			refRs = new SqlRecordSourceEntity(rsClassName, dataSource,
					modelConfigurationName, refSelection, refDbConfig,
					refDatabaseAccessor);
		} else {
			task = OabaLinkageType.STAGING_DEDUPLICATION;
			refDbConfig = null;
			refDatabaseAccessor = null;
			refSelection = null;
			refRs = null;
		}
		logger.info("linkageTask: " + task);
		logger.info("refDbConfig: " + refDbConfig);
		logger.info("refSelection: " + refSelection);

		if (makePersistent) {
			InitialContext initialContext = new InitialContext();

			Object o = initialContext.lookup(JNDI_SQL_RS_CTL);
			final SqlRecordSourceController rsController =
				(SqlRecordSourceController) o;
			logger.fine("opController = '" + rsController + "'");

			queryRs = rsController.save(queryRs);
			if (refRs != null) {
				refRs = rsController.save(refRs);
			}
		}
		logger.info("queryRs: " + queryRs);
		logger.info("refRs: " + refRs);

		OabaParameters retVal = new OabaParametersEntity(modelConfigurationName,
				lowThreshold, highThreshold, blockingConfig, queryRs,
				isQueryDeduped, queryDbConfig, refRs, refDbConfig, task);

		if (makePersistent) {
			InitialContext initialContext = new InitialContext();

			Object o = initialContext.lookup(JNDI_OABA_PARAMS_CTL);
			final OabaParametersController opController =
				(OabaParametersController) o;
			logger.fine("opController = '" + opController + "'");

			retVal = opController.save(retVal);
		}

		return retVal;
	}

	public static OabaSettings createOabaSettings(NamedConfiguration nc)
			throws NamingException {
		boolean makePersistent = true;
		return createOabaSettings(nc, makePersistent);
	}

	public static OabaSettings createOabaSettings(NamedConfiguration nc,
			boolean makePersistent) throws NamingException {

		final int limPerBlockingSet = nc.getAbaLimitPerBlockingSet();
		final int limSingleBlockingSet = nc.getAbaLimitSingleBlockingSet();
		final int singleTableGraceLimit =
			nc.getAbaSingleTableBlockingSetGraceLimit();
		final int maxSingle = nc.getOabaMaxSingle();
		final int maxBlockSize = nc.getOabaMaxBlockSize();
		final int maxChunkSize = nc.getOabaMaxChunkSize();
		final int maxMatches = nc.getOabaMaxMatches();
		final int maxOversized = nc.getOabaMaxOversized();
		final int minFields = nc.getOabaMinFields();
		final int interval = nc.getOabaInterval();

		OabaSettings retVal = new OabaSettingsEntity(limPerBlockingSet,
				limSingleBlockingSet, singleTableGraceLimit, maxSingle,
				maxBlockSize, maxChunkSize, maxMatches, maxOversized, minFields,
				interval);

		if (makePersistent) {
			InitialContext initialContext = new InitialContext();

			Object o = initialContext.lookup(JNDI_SETTINGS_CTL);
			final OabaSettingsController osController =
				(OabaSettingsController) o;
			logger.fine("osController = '" + osController + "'");

			retVal = osController.save(retVal);
		}

		return retVal;
	}

	public static ServerConfiguration createServerConfiguration(
			NamedConfiguration nc) throws NamingException,
			ServerConfigurationException, URISyntaxException {
		boolean makePersistent = true;
		return createServerConfiguration(nc, makePersistent);
	}

	public static ServerConfiguration createServerConfiguration(
			NamedConfiguration nc, boolean makePersistent)
			throws NamingException, ServerConfigurationException,
			URISyntaxException {

		final String scName = UUID.randomUUID().toString();
		final String hostName =
			ServerConfigurationControllerBean.computeHostName();
		final int maxThreads = nc.getServerMaxThreads();
		final int maxChunkFileRecords = nc.getServerMaxChunkSize();
		final int maxChunkFileCount = nc.getServerMaxChunkCount();
		final String location = nc.getServerFileURI();
		final URI uri = new URI(location);
		final File workingDir = new File(uri);

		ServerConfigurationEntity sc = new ServerConfigurationEntity();

		sc.setConfigurationName(scName);
		sc.setHostName(hostName);
		sc.setMaxChoiceMakerThreads(maxThreads);
		sc.setMaxOabaChunkFileRecords(maxChunkFileRecords);
		sc.setMaxOabaChunkFileCount(maxChunkFileCount);
		sc.setWorkingDirectoryLocation(workingDir);

		ServerConfiguration retVal = sc;
		if (makePersistent) {
			InitialContext initialContext = new InitialContext();

			Object o = initialContext.lookup(JNDI_SERVER_CTL);
			final ServerConfigurationController scController =
				(ServerConfigurationController) o;
			logger.fine("scController = '" + scController + "'");

			retVal = scController.save(retVal);
		}

		return retVal;
	}

	public static TransitivityParameters createTransitivityParameters(
			NamedConfiguration nc, boolean isLinkage) throws NamingException {
		boolean makePersistent = true;
		return createTransitivityParameters(nc, isLinkage, makePersistent);
	}

	public static TransitivityParameters createTransitivityParameters(
			NamedConfiguration nc, boolean isLinkage, boolean makePersistent)
			throws NamingException {

		// HACK Create a temporary, persistent instance of OabaParameters
		final boolean makePersistent0 = true;
		OabaParameters op =
			createOabaParameters(nc, isLinkage, makePersistent0);

		// Determine the transitivity parameters
		final String name = nc.getTransitivityFormat();
		final AnalysisResultFormat format = AnalysisResultFormat.valueOf(name);
		final String graph = nc.getTransitivityGraph();

		// Create the transtivity parameters and conditionally save them
		TransitivityParameters retVal =
			new TransitivityParametersEntity(op, format, graph);

		if (makePersistent) {
			InitialContext initialContext = new InitialContext();

			Object o = initialContext.lookup(JNDI_TRANS_PARAMS_CTL);
			final TransitivityParametersController tpController =
				(TransitivityParametersController) o;
			logger.fine("tpController = '" + tpController + "'");

			retVal = tpController.save(retVal);
		}

		// HACK Remove the temporary OABA parameters
		{
			InitialContext initialContext = new InitialContext();

			Object o = initialContext.lookup(JNDI_OABA_PARAMS_CTL);
			final OabaParametersController opController =
				(OabaParametersController) o;
			logger.fine("opController = '" + opController + "'");

			opController.delete(op);
		}

		return retVal;
	}

	private NamedConfigConversion() {
	}

}
