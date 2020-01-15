package com.choicemaker.cms.webapp.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.validation.constraints.NotNull;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigConversion;
import com.choicemaker.cms.webapp.model.NamedConfigurationBean;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.ReflectionUtils;

/** @deprecated moved to cm-server-web4 module*/
@Deprecated
@Named
@RequestScoped
public class BatchMatch {

	private static final Logger logger =
		Logger.getLogger(BatchMatch.class.getName());

	protected static final Map<Method, Method> settableNCGetters =
		Collections.unmodifiableMap(ReflectionUtils.settableGetters(
				NamedConfigurationBean.class, NamedConfiguration.class));

	private Long configurationId;
	private String extId = null;
	private NamedConfigurationBean override;
	private Boolean doTransitivityAnalysis;
	private Long jobId = null;
	private List<SelectItem> configurations;

	@Inject
	private NamedConfigurationController ncController;

	@Inject
	private BatchMatching bma;

	@PostConstruct
	public void init() {
		this.configurations = new ArrayList<>();
		List<NamedConfiguration> ncs =
			ncController.findAllNamedConfigurations();
		for (NamedConfiguration nc : ncs) {
			SelectItem si =
				new SelectItem(nc.getId(), nc.getConfigurationName());
			this.configurations.add(si);
		}

		this.extId = (new Date()).toString();
		this.override = new NamedConfigurationBean();
		this.doTransitivityAnalysis = true;
	}

	public NamedConfiguration override(NamedConfiguration baseConfiguration,
			NamedConfiguration configurationUpdate) {
		Precondition.assertNonNullArgument("baseConfiguration must be non-null",
				baseConfiguration);
		Precondition.assertNonNullArgument(
				"configurationUpdate must be non-null", configurationUpdate);

		final String msg0 =
			"Failed to invoke '%s' on NamedConfiguration '%s': %s";
		final String msg1 = "Ignoring override value of '%s' for method '%s'";
		final String msg2 =
			"Failed to invoke '%s' with value '%' on NamedConfigurationBean '%s': %s";

		NamedConfigurationBean retVal =
			new NamedConfigurationBean(baseConfiguration);
		for (Map.Entry<Method, Method> entry : settableNCGetters.entrySet()) {
			Method getter = entry.getKey();
			Method setter = entry.getValue();
			Object override = null;
			try {
				override = getter.invoke(configurationUpdate, (Object[]) null);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				String msg =
					String.format(msg0, getter.getName(), configurationUpdate);
				logger.warning(msg);
			}

			// Ignore nulls, empty Strings, and
			// 0 for int's, longs, floats and doubles
			if (override == null) {
				String msg = String.format(msg1, override, getter.getName());
				logger.fine(msg);
				continue;
			} else if (getter.getReturnType().equals(String.class)
					&& ((String) override).trim().isEmpty()) {
				String msg =
					String.format(msg1, override.toString(), getter.getName());
				logger.fine(msg);
				continue;
			} else if (getter.getReturnType().equals(int.class)
					&& 0 == ((Integer) override).intValue()) {
				String msg =
					String.format(msg1, override.toString(), getter.getName());
				logger.fine(msg);
				continue;
			} else if (getter.getReturnType().equals(long.class)
					&& 0 == ((Long) override).longValue()) {
				String msg =
					String.format(msg1, override.toString(), getter.getName());
				logger.fine(msg);
				continue;
			} else if (getter.getReturnType().equals(float.class)
					&& 0.0f == ((Float) override).floatValue()) {
				String msg =
					String.format(msg1, override.toString(), getter.getName());
				logger.fine(msg);
				continue;
			} else if (getter.getReturnType().equals(double.class)
					&& 0.0d == ((Long) override).doubleValue()) {
				String msg =
					String.format(msg1, override.toString(), getter.getName());
				logger.fine(msg);
				continue;
			}

			// Update the entity based on the interface value
			try {
				setter.invoke(retVal, new Object[] {
						override });
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				String msg =
					String.format(msg2, setter.getName(), override, retVal);
				logger.warning(msg);
			}
		}

		return retVal;
	}

	public void submit() throws NamingException, ServerConfigurationException,
			URISyntaxException {
		NamedConfiguration configuration = null;
		if (configurationId != null) {
			configuration =
				ncController.findNamedConfiguration(configurationId);
		}
		if (configuration != null) {

			NamedConfiguration updated = override(configuration, override);

			OabaSettings oabaSettings =
				NamedConfigConversion.createOabaSettings(updated);
			ServerConfiguration serverConfig =
				NamedConfigConversion.createServerConfiguration(updated);

			boolean isLinkage = false;
			OabaLinkageType linkageType =
				OabaLinkageType.valueOf(updated.getTask());
			if (linkageType != null) {
				isLinkage = OabaLinkageType.isLinkage(linkageType);
			}

			if (doTransitivityAnalysis) {
				TransitivityParameters tp = NamedConfigConversion
						.createTransitivityParameters(updated, isLinkage);
				if (isLinkage) {
					this.jobId = bma.startLinkageAndAnalysis(extId, tp,
							oabaSettings, serverConfig);
				} else {
					this.jobId = bma.startDeduplicationAndAnalysis(extId, tp,
							oabaSettings, serverConfig);
				}
			} else {
				OabaParameters op = NamedConfigConversion
						.createOabaParameters(updated, isLinkage);
				if (isLinkage) {
					this.jobId =
						bma.startLinkage(extId, op, oabaSettings, serverConfig);
				} else {
					this.jobId = bma.startDeduplication(extId, op, oabaSettings,
							serverConfig);
				}
			}

		}
	}

	public Boolean getDoTransitivityAnalysis() {
		return doTransitivityAnalysis;
	}

	public void setDoTransitivityAnalysis(Boolean dotransitivityAnalysis) {
		this.doTransitivityAnalysis = dotransitivityAnalysis;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public NamedConfigurationBean getOverride() {
		return override;
	}

	public void setOverride(NamedConfigurationBean override) {
		this.override = override;
	}

	public Long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(@NotNull Long configurationId) {
		this.configurationId = configurationId;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public List<SelectItem> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<SelectItem> configurations) {
		this.configurations = configurations;
	}

}
