package com.choicemaker.cms.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BATCH_RESULTS_PERSISTENCE_SCHEME;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobInfo;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.batch.api.BatchMonitoring;
import com.choicemaker.cm.batch.api.BatchMonitoringRemote;
import com.choicemaker.cm.batch.api.IndexedProperty;
import com.choicemaker.cm.batch.api.IndexedPropertyController;
import com.choicemaker.cm.batch.api.IndexedPropertyMonitoring;
import com.choicemaker.cm.batch.api.IndexedPropertyMonitoringRemote;
import com.choicemaker.cm.batch.api.OperationalProperty;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.OperationalPropertyMonitoring;
import com.choicemaker.cm.batch.api.OperationalPropertyMonitoringRemote;
import com.choicemaker.cm.batch.ejb.BatchJobInfoBean;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.MatchPairInfoBean;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.ejb.OabaEjbUtils;
import com.choicemaker.cm.oaba.ejb.OabaJobInfoBean;
import com.choicemaker.cm.transitivity.api.TransitiveGroupInfo;
import com.choicemaker.cm.transitivity.api.TransitivityConfigurationController;
import com.choicemaker.cm.transitivity.api.TransitivityJobInfo;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cm.transitivity.api.TransitivitySettingsController;
import com.choicemaker.cm.transitivity.ejb.TransitiveGroupInfoBean;
import com.choicemaker.cm.transitivity.ejb.TransitivityJobInfoBean;
import com.choicemaker.cm.transitivity.ejb.util.TransitivityUtils;
import com.choicemaker.cms.api.UrmJobInfo;
import com.choicemaker.util.Precondition;

//@Singleton
@Stateless
@TransactionAttribute(REQUIRED)
@Local({
		BatchMonitoring.class, IndexedPropertyMonitoring.class,
		OperationalPropertyMonitoring.class })
@Remote({
		BatchMonitoringRemote.class, IndexedPropertyMonitoringRemote.class,
		OperationalPropertyMonitoringRemote.class })
public class BatchMonitoringBean implements BatchMonitoring,
		IndexedPropertyMonitoring, OperationalPropertyMonitoring {

	private static final Logger logger =
		Logger.getLogger(BatchMonitoringBean.class.getName());

	public static List<String> mapToValueSortedList(Map<Integer, String> map) {
		Precondition.assertNonNullArgument(map);
		Set<String> uniques = new HashSet<>();
		uniques.addAll(map.values());
		List<String> retVal = new ArrayList<>();
		retVal.addAll(uniques);
		Collections.sort(retVal);
		return retVal;
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private IndexedPropertyController idxPropController;

	@EJB
	private OperationalPropertyController opPropController;

	@EJB
	private OabaJobManager jobManager;

	@EJB
	private OabaParametersController oabaParamsController;

	@EJB
	private OabaJobManager oabaJobController;

	@EJB
	private TransitivitySettingsController transSettingsController;

	@EJB
	private TransitivityParametersController transParamsController;

	@EJB
	private TransitivityConfigurationController serverController;

	// -- Accessors

	protected final IndexedPropertyController getIndexedPropertyController() {
		return idxPropController;
	}

	protected final OabaParametersController getOabaParamsController() {
		return oabaParamsController;
	}

	protected final TransitivityConfigurationController getServerController() {
		return serverController;
	}

	protected final BatchJobManager getOabaJobController() {
		return oabaJobController;
	}

	protected final TransitivitySettingsController getSettingsController() {
		return transSettingsController;
	}

	protected final TransitivityParametersController getParametersController() {
		return transParamsController;
	}

	// -- BatchJobMonitor

	@TransactionAttribute(SUPPORTS)
	@Override
	public BatchJobInfo getBatchJobInfo(BatchJob batchJob) {
		BatchJobInfo retVal = null;
		if (batchJob != null) {
			if (OabaEjbUtils.isOabaJob(batchJob)) {
				retVal = computeOabaJobInfo(batchJob);
			} else if (TransitivityUtils.isTransitivityJob(batchJob)) {
				retVal = computeTransitivityJobInfo(batchJob);
			} else if (UrmUtils.isUrmJob(batchJob)) {
				retVal = computeUrmJobInfo(batchJob);
			} else {
				retVal = computeBatchJobInfo(batchJob);
			}
		}
		return retVal;
	}

	public BatchJobInfo computeBatchJobInfo(BatchJob batchJob) {
		BatchJobInfo retVal = null;
		if (batchJob != null) {
			retVal = new BatchJobInfoBean(batchJob);
		}
		return retVal;
	}

	public OabaJobInfo computeOabaJobInfo(final BatchJob oabaJob) {
		OabaJobInfo retVal = null;
		if (oabaJob != null && OabaEjbUtils.isOabaJob(oabaJob)) {
			final long jobId = oabaJob.getId();
			logger.finest(String.format("OabaJob id: %d", jobId));
			OabaParameters oabaParams =
				getOabaParamsController().findOabaParametersByBatchJobId(jobId);
			logger.finest(
					String.format("OabaParameter id: %d", oabaParams.getId()));
			OabaSettings oabaSettings =
				getSettingsController().findOabaSettingsByJobId(jobId);
			logger.finest(
					String.format("OabaSettings id: %d", oabaSettings.getId()));
			ServerConfiguration serverConfig =
				getServerController().findServerConfigurationByJobId(jobId);
			logger.finest(
					String.format("ServerConfig: %d", serverConfig.getId()));

			MatchPairInfo matchPairInfo = computeOabaMatchPairInfo(oabaJob);

			retVal = new OabaJobInfoBean(oabaJob, oabaParams, oabaSettings,
					serverConfig, matchPairInfo);

		}
		return retVal;
	}

	public TransitivityJobInfo computeTransitivityJobInfo(
			BatchJob transitivityJob) {
		TransitivityJobInfo retVal = null;
		if (transitivityJob != null
				&& TransitivityUtils.isTransitivityJob(transitivityJob)) {
			final BatchJob oabaParentJob = getParentOabaJob(transitivityJob);
			logger.finest(String.format("OabaJob id: %d", oabaParentJob));
			OabaJobInfo oabaJobInfo = computeOabaJobInfo(oabaParentJob);
			final long transJobId = transitivityJob.getId();
			logger.finest(String.format("TransitivityJob id: %d", transJobId));
			TransitivityParameters transitivityParams =
				getParametersController()
						.findTransitivityParametersByBatchJobId(transJobId);
			logger.finest(String.format("TransivityParams id: %d",
					transitivityParams.getId()));
			OabaSettings oabaSettings = getSettingsController()
					.findSettingsByTransitivityJobId(transJobId);
			logger.finest(
					String.format("OabaSettings: %d", oabaSettings.getId()));
			ServerConfiguration serverConfig = getServerController()
					.findConfigurationByTransitivityJobId(transJobId);
			logger.finest(
					String.format("ServerConfig: %d", serverConfig.getId()));

			MatchPairInfo matchPairInfo =
				computeTransitivityPairInfo(transitivityJob);
			TransitiveGroupInfo transitiveGroupInfo =
				computeTransitivityGroupInfo(transitivityJob);

			retVal = new TransitivityJobInfoBean(transitivityJob, oabaJobInfo,
					transitivityParams, oabaSettings, serverConfig,
					matchPairInfo, transitiveGroupInfo);

		}
		return retVal;
	}

	protected MatchPairInfo computeOabaMatchPairInfo(BatchJob oabaJob) {
		// FIXME
		final int differCount = -1;
		final int holdCount = -1;
		final int matchCount = -1;
		final int pairCount = -1;
		final Map<Integer, String> mapPairFileURIs =
			getIndexedPropertyController().findIndexedProperties(oabaJob,
					MatchPairInfoBean.PN_OABA_MATCH_RESULT_FILE);
		final List<String> pairFileURIs = mapToValueSortedList(mapPairFileURIs);
		BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme = null;
		RECORD_ID_TYPE recordIdType = null;
		MatchPairInfo retVal =
			new MatchPairInfoBean(differCount, holdCount, matchCount, pairCount,
					pairFileURIs, persistenceScheme, recordIdType);
		return retVal;
	}

	protected MatchPairInfo computeTransitivityPairInfo(
			BatchJob transitivityJob) {
		// FIXME
		final int differCount = -1;
		final int holdCount = -1;
		final int matchCount = -1;
		final int pairCount = -1;
		final Map<Integer, String> mapPairFileURIs =
			getIndexedPropertyController().findIndexedProperties(
					transitivityJob,
					TransitiveGroupInfoBean.PN_TRANSMATCH_PAIR_FILE);
		final List<String> pairFileURIs = mapToValueSortedList(mapPairFileURIs);
		BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme = null;
		RECORD_ID_TYPE recordIdType = null;
		MatchPairInfo retVal =
			new MatchPairInfoBean(differCount, holdCount, matchCount, pairCount,
					pairFileURIs, persistenceScheme, recordIdType);
		return retVal;
	}

	protected TransitiveGroupInfo computeTransitivityGroupInfo(
			BatchJob transitivityJob) {
		// FIXME
		final Map<Integer, String> mapGroupFileURIs =
			getIndexedPropertyController().findIndexedProperties(
					transitivityJob,
					TransitiveGroupInfoBean.PN_TRANSMATCH_GROUP_FILE);
		final List<String> groupFileURIs =
			mapToValueSortedList(mapGroupFileURIs);
		final int holdGroupCount = -1;
		final int mergeGroupCount = -1;
		BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme = null;
		RECORD_ID_TYPE recordIdType = null;
		TransitiveGroupInfo retVal =
			new TransitiveGroupInfoBean(groupFileURIs, holdGroupCount,
					mergeGroupCount, persistenceScheme, recordIdType);
		return retVal;
	}

	/** Returns the parent OABA job of a transitivity analysis job */
	@TransactionAttribute(SUPPORTS)
	public BatchJob getParentOabaJob(BatchJob transitivityJob) {
		Precondition.assertNonNullArgument(transitivityJob);
		final long oabaJobId = transitivityJob.getBatchParentId();
		BatchJob retVal = getOabaJobController().findBatchJob(oabaJobId);
		return retVal;
	}

	public UrmJobInfo computeUrmJobInfo(BatchJob urmJob) {
		throw new Error("not yet implemented");
	}

	@Override
	public String getOperationalPropertyValue(BatchJob job, String pn) {
		return opPropController.getOperationalPropertyValue(job, pn);
	}

	@Override
	public OperationalProperty findOperationalProperty(long propertyId) {
		return opPropController.findOperationalProperty(propertyId);
	}

	@Override
	public OperationalProperty findOperationalProperty(BatchJob job,
			String name) {
		return opPropController.findOperationalProperty(job, name);
	}

	@Override
	public List<OperationalProperty> findOperationalProperties(BatchJob job) {
		return opPropController.findOperationalProperties(job);
	}

	@Deprecated
	@Override
	public List<OperationalProperty> findAllOperationalProperties() {
		return opPropController.findAllOperationalProperties();
	}

	@Override
	public String getIndexedPropertyValue(BatchJob job, String pn, int index) {
		return idxPropController.getIndexedPropertyValue(job, pn, index);
	}

	@Override
	public IndexedProperty findIndexedProperty(long propertyId) {
		return idxPropController.findIndexedProperty(propertyId);
	}

	@Override
	public IndexedProperty findIndexedProperty(BatchJob job, String name,
			int index) {
		return idxPropController.findIndexedProperty(job, name, index);
	}

	@Override
	public Map<Integer, String> findIndexedProperties(BatchJob job,
			String name) {
		return idxPropController.findIndexedProperties(job, name);
	}

	@Override
	public List<String> findIndexedPropertyNames(BatchJob job) {
		return idxPropController.findIndexedPropertyNames(job);
	}

}
