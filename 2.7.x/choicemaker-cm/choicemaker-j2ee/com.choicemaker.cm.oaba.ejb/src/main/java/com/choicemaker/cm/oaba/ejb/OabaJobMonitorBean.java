package com.choicemaker.cm.oaba.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.MatchPairInfo;
import com.choicemaker.cm.oaba.api.OabaJobInfo;
import com.choicemaker.cm.oaba.api.OabaJobMonitor;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;

@Stateless
@TransactionAttribute(REQUIRED)
public class OabaJobMonitorBean implements OabaJobMonitor {

	// private static final Logger logger =
	// Logger.getLogger(OabaJobMonitorBean.class.getName());

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private ServerConfigurationController serverManager;

	@TransactionAttribute(SUPPORTS)
	@Override
	public OabaJobInfo getOabaJobInfo(BatchJob batchJob) {
		OabaJobInfo retVal = null;
		if (batchJob != null && OabaEjbUtils.isOabaJob(batchJob)) {
			OabaParameters oabaParameters = getOabaParameters(batchJob);
			OabaSettings oabaSettings = getOabaSettings(batchJob);
			ServerConfiguration serverConfiguration =
				getServerConfiguration(batchJob);
			MatchPairInfo matchPairInfo = getMatchPairInfo(batchJob);
			retVal = new OabaJobInfoBean(batchJob, oabaParameters, oabaSettings,
					serverConfiguration, matchPairInfo);
		}
		return retVal;
	}

	protected OabaParameters getOabaParameters(BatchJob batchJob) {
		assert batchJob != null;
		assert OabaEjbUtils.isOabaJob(batchJob);
		long jobId = batchJob.getId();
		OabaParameters retVal =
			paramsController.findOabaParametersByBatchJobId(jobId);
		return retVal;
	}

	protected OabaSettings getOabaSettings(BatchJob batchJob) {
		assert batchJob != null;
		assert OabaEjbUtils.isOabaJob(batchJob);
		long jobId = batchJob.getId();
		OabaSettings retVal =
			oabaSettingsController.findOabaSettingsByJobId(jobId);
		return retVal;
	}

	protected ServerConfiguration getServerConfiguration(BatchJob batchJob) {
		assert batchJob != null;
		assert OabaEjbUtils.isOabaJob(batchJob);
		long jobId = batchJob.getId();
		ServerConfiguration retVal =
			serverManager.findServerConfigurationByJobId(jobId);
		return retVal;
	}

	protected MatchPairInfo getMatchPairInfo(BatchJob batchJob) {
		// FIXME
		return null;
	}

}
