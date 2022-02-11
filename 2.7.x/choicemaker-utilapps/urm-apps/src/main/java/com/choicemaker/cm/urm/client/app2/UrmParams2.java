/*******************************************************************************
 * Copyright (c) 2016, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.urm.client.app2;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR;
import com.choicemaker.util.Precondition;

public class UrmParams2 {

	/**
	 * Prefix for the name of properties that override transitivity parameters
	 */
	public static final String PREFIX_TRANSITIVITY_PARAMS = "tp";

	/** Length of {@link #PREFIX_TRANSITIVITY_PARAMS} */
	protected static final int LENGTH_TRANSITIVITY_PARAMS =
		PREFIX_TRANSITIVITY_PARAMS.length();

	/** Prefix for the name of properties that override OABA settings */
	public static final String PREFIX_OABA_SETTINGS = "oaba";

	/** Length of {@link #PREFIX_OABA_SETTINGS} */
	protected static final int LENGTH_OABA_SETTINGS =
		PREFIX_OABA_SETTINGS.length();

	/**
	 * Prefix for the name of properties that override server configuration
	 * settings
	 */
	public static final String PREFIX_SERVER_CONFIGS = "sc";

	/** Length of {@link #PREFIX_SERVER_CONFIGS} */
	protected static final int LENGTH_SERVER_CONFIGS =
		PREFIX_SERVER_CONFIGS.length();

	private static final Logger logger =
		Logger.getLogger(UrmParams2.class.getName());

	final APP_SERVER_VENDOR appServer;
	final String appName;
	final long jobId;
	final List<String> errors;

	/** Properties that override existing transitivity parameters */
	final Properties tpOverrides;

	/** Properties that override existing OABA settings */
	final Properties oabaOverrides;

	/** Properties that override existing server configuration settings */
	final Properties scOverrides;

	public UrmParams2(APP_SERVER_VENDOR appServer, String appName, long jobId,
			Properties overrides, List<String> errors) {
		if (errors == null || errors.isEmpty()) {
			Precondition.assertNonEmptyString(
					"appName must not be null or blank", appName);
			Precondition.assertBoolean("Job id must be non-zero", jobId != 0);
		}

		this.appServer =
			appServer == null ? APP_SERVER_VENDOR.JBOSS : appServer;
		this.appName = appName;
		this.jobId = jobId;
		this.errors = errors == null ? Collections.emptyList() : errors;

		this.tpOverrides = new Properties();
		this.oabaOverrides = new Properties();
		this.scOverrides = new Properties();

		if (overrides != null) {
			for (Object o : overrides.keySet()) {
				String s = (String) o;
				if (s == null) {
					logger.warning("Unexpected null property name");
				} else if (s.startsWith(PREFIX_TRANSITIVITY_PARAMS)) {
					String pn = s.substring(LENGTH_TRANSITIVITY_PARAMS);
					String pv = overrides.getProperty(s);
					logOverride("TransitivityParameter", pn, pv);
					tpOverrides.setProperty(pn, pv);
				} else if (s.startsWith(PREFIX_OABA_SETTINGS)) {
					String pn = s.substring(LENGTH_OABA_SETTINGS);
					String pv = overrides.getProperty(s);
					logOverride("OabaSettings", pn, pv);
					oabaOverrides.setProperty(pn, pv);
				} else if (s.startsWith(PREFIX_SERVER_CONFIGS)) {
					String pn = s.substring(LENGTH_SERVER_CONFIGS);
					String pv = overrides.getProperty(s);
					logOverride("ServerConfiguration", pn, pv);
					scOverrides.setProperty(pn, pv);
				} else {
					String msg0 =
						"Unrecognized override: name '%s', value '%s'";
					String msg = String.format(msg0, s, overrides.get(s));
					logger.warning(msg);
				}
			}
		}

		logger.fine(this.toString());
	}

	protected static void logOverride(String overrideType, String overrideName,
			String overrideValue) {
		String msg0 = "[Override %s] '%s': '%s'";
		String msg =
			String.format(msg0, overrideType, overrideName, overrideValue);
		logger.fine(msg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result =
			prime * result + ((appServer == null) ? 0 : appServer.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + (int) (jobId ^ (jobId >>> 32));
		result = prime * result
				+ ((oabaOverrides == null) ? 0 : oabaOverrides.hashCode());
		result = prime * result
				+ ((scOverrides == null) ? 0 : scOverrides.hashCode());
		result = prime * result
				+ ((tpOverrides == null) ? 0 : tpOverrides.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UrmParams2 other = (UrmParams2) obj;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (appServer != other.appServer)
			return false;
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (jobId != other.jobId)
			return false;
		if (oabaOverrides == null) {
			if (other.oabaOverrides != null)
				return false;
		} else if (!oabaOverrides.equals(other.oabaOverrides))
			return false;
		if (scOverrides == null) {
			if (other.scOverrides != null)
				return false;
		} else if (!scOverrides.equals(other.scOverrides))
			return false;
		if (tpOverrides == null) {
			if (other.tpOverrides != null)
				return false;
		} else if (!tpOverrides.equals(other.tpOverrides))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UrmParams2 [appServer=" + appServer + ", appName=" + appName
				+ ", jobId=" + jobId + ", errors:" + errors.size()
				+ ", tpOverrides:" + tpOverrides.size() + ", oabaOverrides:"
				+ oabaOverrides.size() + ", scOverrides:" + scOverrides.size()
				+ "]";
	}

}
