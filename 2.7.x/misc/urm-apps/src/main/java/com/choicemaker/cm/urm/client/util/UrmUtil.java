/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.client.util;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.choicemaker.cm.urm.api.BatchMatchAnalyzer;
import com.choicemaker.util.Precondition;

public class UrmUtil {

	public static final String URM_MODULE_NAME = "com.choicemaker.cm.urm.ejb";

	public static final APP_SERVER_VENDOR DEFAULT_APP_SERVER = APP_SERVER_VENDOR.JBOSS;

	public static final String DEFAULT_DISTINCT_NAME = "";

	public static final String JBOSS_INITIAL_CONTEXT_FACTORY =
		"org.jboss.naming.remote.client.InitialContextFactory";

	public static final String JBOSS_URL_PKG_PREFIXES =
		"org.jboss.ejb.client.naming";

	private static final Logger logger =
		Logger.getLogger(UrmUtil.class.getName());

	public static BatchMatchAnalyzer getBatchMatchAnalyzer(APP_SERVER_VENDOR appServer,
			final String appName)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonNullArgument("appServer must be non-null",
				appServer);
		Precondition.assertNonEmptyString("appName must be non-empty", appName);

		final String beanName = "BatchMatchAnalyzerBean";
		final String viewClassName = BatchMatchAnalyzer.class.getName();
		Object o = getUrmBean(appServer, appName, beanName, viewClassName);
		BatchMatchAnalyzer retVal = (BatchMatchAnalyzer) o;
		logger.finest("BatchMatchAnalyzer: " + retVal == null ? "null"
				: retVal.toString());

		assert retVal != null;
		return retVal;
	}

	public static Object getUrmBean(APP_SERVER_VENDOR appServer, final String appName,
			final String beanName, final String viewClassName)
			throws NamingException, RemoteException, CreateException {
		Precondition.assertNonEmptyString("appName must be non-empty", appName);
		Precondition.assertNonEmptyString("beanName must be non-empty",
				beanName);
		Precondition.assertNonEmptyString("viewClassName must be non-empty",
				viewClassName);

		Context context = getInitialContext(appServer);

		final String moduleName = URM_MODULE_NAME;
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
