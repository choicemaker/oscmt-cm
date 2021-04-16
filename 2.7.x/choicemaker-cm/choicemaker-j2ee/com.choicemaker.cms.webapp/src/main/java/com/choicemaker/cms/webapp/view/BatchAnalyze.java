package com.choicemaker.cms.webapp.view;

import static com.choicemaker.cm.args.PersistentObject.NONPERSISTENT_ID;
import static com.choicemaker.cm.core.ImmutableThresholds.DEFAULT_DIFFER_THRESHOLD;
import static com.choicemaker.cm.core.ImmutableThresholds.DEFAULT_MATCH_THRESHOLD;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.WellKnownGraphProperties;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.core.Thresholds;
import com.choicemaker.cm.oaba.api.OabaBatchController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.transitivity.pojo.TransitivityParametersBean;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.ConfigurationQueries;
import com.choicemaker.util.Precondition;

@Named
@RequestScoped
public class BatchAnalyze {

	private static final Logger logger =
		Logger.getLogger(BatchAnalyze.class.getName());

	public static final Long INVALID_OABA_JOB_ID = NONPERSISTENT_ID;

	// protected static final Map<Method, Method> settableNCGetters =
	// Collections.unmodifiableMap(ReflectionUtils.settableGetters(
	// NamedConfigurationBean.class, NamedConfiguration.class));

	private String extId = null;
	private Long oabaJobId;
	private Thresholds thresholds;
	private String graphPropertyName;
	private Long jobId = null;
	private List<SelectItem> oabaJobs;
	private List<SelectItem> graphProperties;

	@Inject
	private OabaBatchController oababBatchController;

	@Inject
	private ConfigurationQueries configQueries;

	@Inject
	private BatchMatching bma;

	@PostConstruct
	public void init() {
		final String label0 = "URM job %d / OABA job %d";
		this.oabaJobs = new ArrayList<>();
		List<BatchJob> oabajs = oababBatchController.findAllOabaJobs();
		for (BatchJob oabaj : oabajs) {
			if (oabaj.getStatus() == BatchJobStatus.COMPLETED) {
				String label =
					String.format(label0, oabaj.getUrmId(), oabaj.getId());
				SelectItem si = new SelectItem(oabaj.getId(), label);
				this.oabaJobs.add(si);
			}
		}

		this.graphProperties = new ArrayList<>();
		for (IGraphProperty gp : WellKnownGraphProperties.GPN_INSTANCES) {
			SelectItem si = new SelectItem(gp.getName(), gp.getName());
			this.graphProperties.add(si);
		}

		this.extId = (new Date()).toString();
		this.thresholds =
			new Thresholds(DEFAULT_DIFFER_THRESHOLD, DEFAULT_MATCH_THRESHOLD);
		this.graphPropertyName = WellKnownGraphProperties.GPN_SCM;
	}

	public void onOabaJobChange() {
		BatchJob oabaJob = lookupOabaJob(oabaJobId);
		TransitivityParameters tp = lookupTransitivityParameters(oabaJob);
		if (tp != null) {
			thresholds =
				new Thresholds(tp.getLowThreshold(), tp.getHighThreshold());
			graphPropertyName = tp.getGraphProperty().getName();
		} else {
			thresholds = new Thresholds(DEFAULT_DIFFER_THRESHOLD,
					DEFAULT_MATCH_THRESHOLD);
			graphPropertyName = WellKnownGraphProperties.GPN_SCM;
		}
	}

	public BatchJob lookupOabaJob(Long id) {
		BatchJob retVal = null;
		if (oabaJobId != null) {
			retVal = oababBatchController.findOabaJob(oabaJobId);
		}
		return retVal;
	}

	public OabaParameters lookupOabaParameters(BatchJob oabaJob) {
		OabaParameters retVal = null;
		if (oabaJob != null) {
			retVal =
				configQueries.findOabaParametersByBatchJobId(oabaJob.getId());
		}
		return retVal;
	}

	public TransitivityParameters lookupTransitivityParameters(
			OabaParameters baseOP) {
		TransitivityParameters retVal = null;
		if (baseOP instanceof TransitivityParameters) {
			retVal = (TransitivityParameters) baseOP;
		} else if (baseOP != null) {
			retVal = new TransitivityParametersBean(baseOP);
		}
		return retVal;
	}

	public TransitivityParameters lookupTransitivityParameters(
			BatchJob oabaJob) {
		TransitivityParameters retVal = null;
		OabaParameters baseOP = lookupOabaParameters(oabaJob);
		if (baseOP instanceof TransitivityParameters) {
			retVal = (TransitivityParameters) baseOP;
		} else if (baseOP != null) {
			retVal = new TransitivityParametersBean(baseOP);
		}
		return retVal;
	}

	public TransitivityParameters computeTransitivityParameters(
			BatchJob oabaJob, Thresholds thresholds, List<String> errors) {
		Precondition.assertNonNullArgument("OABA job must be non-null",
				oabaJob);
		Precondition.assertNonNullArgument("thresholds must be non-null",
				thresholds);
		Precondition.assertNonNullArgument("error list must be non-null",
				errors);
		Precondition.assertBoolean("error list must be empty",
				errors.size() == 0);

		TransitivityParametersBean retVal;

		TransitivityParameters baseTP = lookupTransitivityParameters(oabaJob);
		if (baseTP == null) {
			retVal = null;
			String msg = "Missing parameters for OABA job " + oabaJobId;
			logger.severe(msg);
			errors.add(msg);

		} else {
			retVal = new TransitivityParametersBean(baseTP);

			assert oabaJob != null;
			assert oabaJob.getId() == oabaJobId;

			final float overrideDiffer = thresholds.getDifferThreshold();
			final float overrideMatch = thresholds.getMatchThreshold();

			// Constraints enforced by UI and Thresholds class
			assert overrideDiffer >= 0.0f;
			assert overrideDiffer <= 1.0f;
			assert overrideMatch >= 0.0f;
			assert overrideMatch <= 1.0f;
			assert overrideDiffer <= overrideMatch;

			// Additional constraints
			String msg;
			final String threshErr2 = "Invalid %s threshold %4.3f: %s";

			if (overrideDiffer < baseTP.getLowThreshold()) {
				msg = String.format(threshErr2, "differ", overrideDiffer,
						"less than original differ threshold "
								+ baseTP.getLowThreshold());
				errors.add(msg);
			}

			if (overrideMatch < baseTP.getLowThreshold()) {
				msg = String.format(threshErr2, "match", overrideMatch,
						"less than original differ threshold "
								+ baseTP.getLowThreshold());
				errors.add(msg);
			}

			// String overrideGraph = retVal.getGraphProperty().getName();
			// String requestedTA = request.getParameter("overrideGraph");
			// if (requestedTA != null) {
			// requestedTA = requestedTA.trim();
			// if (!requestedTA.isEmpty()) {
			// overrideGraph = requestedTA;
			// }
			// }

			if (extId != null) {
				extId = extId.trim();
			}

			retVal.setLowThreshold(overrideDiffer);
			retVal.setHighThreshold(overrideMatch);
			retVal.setGraph(graphPropertyName);
		}

		return retVal;
	}

	public ServerConfiguration findServerConfiguration(BatchJob oabaJob) {
		Precondition.assertNonNullArgument("OABA job must be non-null",
				oabaJob);

		long serverId = oabaJob.getServerId();
		ServerConfiguration retVal =
			configQueries.findServerConfiguration(serverId);
		assert retVal != null;

		return retVal;
	}

	public OabaSettings findOabaSettings(BatchJob oabaJob) {
		Precondition.assertNonNullArgument("OABA job must be non-null",
				oabaJob);

		long oabaSettingsId = oabaJob.getSettingsId();
		OabaSettings retVal = configQueries.findOabaSettings(oabaSettingsId);
		assert retVal != null;

		return retVal;
	}

	public void submit() throws NamingException, ServerConfigurationException,
			URISyntaxException {
		BatchJob oabaJob = lookupOabaJob(oabaJobId);
		if (oabaJob != null) {

			List<String> errors = new ArrayList<>();
			TransitivityParameters tp =
				computeTransitivityParameters(oabaJob, getThresholds(), errors);
			OabaSettings oabaSettings = findOabaSettings(oabaJob);
			ServerConfiguration serverConfig = findServerConfiguration(oabaJob);

			String status;
			if (errors.size() == 0) {
				long newJobId = bma.startTransitivity(extId, tp, oabaJob,
						oabaSettings, serverConfig);
				setJobId(newJobId);
				status =
					"Transitivity analysis started with job ID = " + newJobId;
				logger.info(status);
			} else {
				for (String error : errors) {
					logger.severe(error);
				}
				status =
					"Transitivity analysis did not start because of errors";
				logger.severe(status);
			}
		}
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public Long getOabaJobId() {
		return oabaJobId;
	}

	public void setOabaJobId(/* @NotNull */ Long oabaJobId) {
		this.oabaJobId = oabaJobId;
	}

	public String getGraphPropertyName() {
		return graphPropertyName;
	}

	public void setGraphPropertyName(String gpn) {
		this.graphPropertyName = gpn;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Thresholds getThresholds() {
		return thresholds;
	}

	public void setThresholds(Thresholds thresholds) {
		this.thresholds = thresholds;
	}

	public List<SelectItem> getOabaJobs() {
		return oabaJobs;
	}

	public void setOabaJobs(List<SelectItem> oabaJobs) {
		this.oabaJobs = oabaJobs;
	}

	public List<SelectItem> getGraphProperties() {
		return graphProperties;
	}

	public void setGraphProperties(List<SelectItem> graphProperties) {
		this.graphProperties = graphProperties;
	}

}
