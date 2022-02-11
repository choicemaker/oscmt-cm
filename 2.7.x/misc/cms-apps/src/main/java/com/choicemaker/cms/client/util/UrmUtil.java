/*******************************************************************************
 * Copyright (c) 2015, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cms.client.util;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cms.api.BatchJobQueries;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.cms.api.ConfigurationQueries;
import com.choicemaker.cms.api.remote.BatchJobQueriesRemote;
import com.choicemaker.cms.api.remote.BatchMatchingRemote;
import com.choicemaker.cms.api.remote.ConfigurationQueriesRemote;
import com.choicemaker.util.Precondition;

public class UrmUtil {

	public static final String URM_MODULE_NAME = "com.choicemaker.cms.ejb";

	public static final String CMS_MODULE_NAME = "com.choicemaker.cms.ejb";

	public static final APP_SERVER_VENDOR DEFAULT_APP_SERVER =
		APP_SERVER_VENDOR.JBOSS;

	public static final String DEFAULT_DISTINCT_NAME = "";

	public static final String JBOSS_INITIAL_CONTEXT_FACTORY =
		"org.jboss.naming.remote.client.InitialContextFactory";

	public static final String JBOSS_URL_PKG_PREFIXES =
		"org.jboss.ejb.client.naming";

	private static final Logger logger =
		Logger.getLogger(UrmUtil.class.getName());

	public static BatchMatching getBatchMatching(APP_SERVER_VENDOR appServer,
			final String appName)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		final String beanName = "BatchMatchingBean";
		final String viewClassName = BatchMatchingRemote.class.getName();
		Object o = getModuleBean(appServer, appName, CMS_MODULE_NAME, beanName,
				viewClassName);
		BatchMatching retVal = (BatchMatching) o;
		logger.finest("BatchMatching: " + retVal == null ? "null"
				: retVal.toString());

		assert retVal != null;
		return retVal;
	}

	public static BatchJob getOabaBatchJob(APP_SERVER_VENDOR appServer,
			final String appName, long jobId)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		final BatchJobQueries bjq = getBatchJobQueriesBean(appServer, appName);

		BatchJob retVal = bjq.findOabaJob(jobId);
		if (retVal == null) {
			String msg0 = "Unable to find OabaBatchJob for jobId %d";
			String msg = String.format(msg0, jobId);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		assert retVal != null;
		return retVal;
	}

	public static TransitivityParameters getTransitivityParameters(
			APP_SERVER_VENDOR appServer, final String appName, long jobId)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		ConfigurationQueries cq =
			getConfigurationQueriesBean(appServer, appName);
		BatchJob oabaJob = getOabaBatchJob(appServer, appName, jobId);

		TransitivityParameters retVal =
			cq.findTransitivityParameters(oabaJob.getParametersId());
		if (retVal == null) {
			String msg0 =
				"Unable to find transitivity parameters for OABA jobId %d";
			String msg = String.format(msg0, jobId);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		return retVal;
	}

	public static OabaSettings getOabaSettings(APP_SERVER_VENDOR appServer,
			final String appName, long jobId)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		ConfigurationQueries cq =
			getConfigurationQueriesBean(appServer, appName);
		BatchJob oabaJob = getOabaBatchJob(appServer, appName, jobId);

		OabaSettings retVal = cq.findOabaSettings(oabaJob.getSettingsId());
		if (retVal == null) {
			String msg0 = "Unable to find OABA settings for OABA jobId %d";
			String msg = String.format(msg0, jobId);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		return retVal;
	}

	public static ServerConfiguration getServerConfiguration(
			APP_SERVER_VENDOR appServer, final String appName, long jobId)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		ConfigurationQueries cq =
			getConfigurationQueriesBean(appServer, appName);
		BatchJob oabaJob = getOabaBatchJob(appServer, appName, jobId);

		ServerConfiguration retVal =
			cq.findServerConfiguration(oabaJob.getServerId());
		if (retVal == null) {
			String msg0 =
				"Unable to find server configuration for OABA jobId %d";
			String msg = String.format(msg0, jobId);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		return retVal;
	}

	public static BatchJobQueries getBatchJobQueriesBean(
			APP_SERVER_VENDOR appServer, final String appName)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		final String beanName = "BatchJobQueriesBean";
		final String viewClassName = BatchJobQueriesRemote.class.getName();
		Object o = getModuleBean(appServer, appName, CMS_MODULE_NAME, beanName,
				viewClassName);
		BatchJobQueries retVal = (BatchJobQueries) o;
		logger.finest("BatchJobQueries: " + retVal == null ? "null"
				: retVal.toString());
		assert retVal != null;

		return retVal;
	}

	public static ConfigurationQueries getConfigurationQueriesBean(
			APP_SERVER_VENDOR appServer, final String appName)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		final String beanName = "ConfigurationQueriesBean";
		final String viewClassName = ConfigurationQueriesRemote.class.getName();
		Object o = getModuleBean(appServer, appName, CMS_MODULE_NAME, beanName,
				viewClassName);
		ConfigurationQueries retVal = (ConfigurationQueries) o;
		logger.finest("ConfigurationQueries: " + retVal == null ? "null"
				: retVal.toString());
		assert retVal != null;

		return retVal;
	}

	public static Object getModuleBean(APP_SERVER_VENDOR appServer,
			final String appName, final String moduleName, String beanName,
			final String viewClassName)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonEmptyString("appName must be non-empty", appName);
		Precondition.assertNonEmptyString("module must be non-empty",
				moduleName);
		Precondition.assertNonEmptyString("beanName must be non-empty",
				moduleName);
		Precondition.assertNonEmptyString("viewClassName must be non-empty",
				viewClassName);

		Context context = getInitialContext(appServer);

		final String distinctName = DEFAULT_DISTINCT_NAME;
		String jndiTemplate = "ejb:%s/%s/%s/%s!%s";
		String jndiName = String.format(jndiTemplate, appName, moduleName,
				distinctName, beanName, viewClassName);
		logger.fine("JNDI lookup: " + jndiName);
		// System.out.printf("JNDI lookup: %s\n", jndiName);

		Object retVal = context.lookup(jndiName);
		logger.finest(
				"JNDI object: " + retVal == null ? "null" : retVal.toString());
		context.close();

		return retVal;
	}

	public static Context getInitialContext(APP_SERVER_VENDOR appServer)
			throws NamingException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Context retVal = null;
		switch (appServer) {
		case JBOSS:
			retVal = getJBossInitialContext();
			break;
		case WEBLOGIC:
			retVal = getWeblogicInitialContext();
			break;
		default:
			throw new Error("unexpected appServer: " + appServer);
		}
		return retVal;
	}

	public static Context getJBossInitialContext() throws NamingException {
		Properties prop = new Properties();
		prop.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jboss.naming.remote.client.InitialContextFactory");
		prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		Context retVal = new InitialContext(prop);
		return retVal;
	}

	public static Context getWeblogicInitialContext() throws NamingException {
		throw new Error("not yet implemented");
	}

	private UrmUtil() {
	}

}
