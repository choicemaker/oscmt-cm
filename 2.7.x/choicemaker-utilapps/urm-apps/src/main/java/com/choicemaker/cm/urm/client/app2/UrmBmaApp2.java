/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.client.app2;

import static com.choicemaker.cm.urm.client.app.UrmCommandLine.COMMAND_HEADER;
import static com.choicemaker.cm.urm.client.app.UrmCommandLine.COMMAND_LINE;
import static com.choicemaker.cm.urm.client.util.UrmUtil.getBatchMatching;
import static com.choicemaker.cm.urm.client.util.UrmUtil.getOabaBatchJob;
import static com.choicemaker.cm.urm.client.util.UrmUtil.getOabaSettings;
import static com.choicemaker.cm.urm.client.util.UrmUtil.getServerConfiguration;
import static com.choicemaker.cm.urm.client.util.UrmUtil.getTransitivityParameters;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.transitivity.pojo.TransitivityParametersBean;
import com.choicemaker.cms.api.BatchMatching;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

public class UrmBmaApp2 {

	private static final Logger logger =
		Logger.getLogger(UrmBmaApp2.class.getName());

	/**
	 * Rerun transitivity analysis of a specified OABA job using the specified
	 * graph criterion. Takes one required argument, two optional arguments, and
	 * an OABA job identifier.
	 * <ul>
	 * <li><code>-appName</code> followed by the JEE application name (a.k.a.
	 * the runtime name of the EAR deployment) that contains the
	 * <code>BatchMatchAnalyzer</code> Enterprise Java Bean (EJB)</li><br/>
	 * <li><code>-appServer</code> followed by the standardized name of a
	 * application-server vendor:</li>
	 * <ul>
	 * <li>JBoss (default if <code>-appServer</code> is not specified)</li>
	 * <li>Weblogic</li>
	 * </ul>
	 * <li><code>-tp.graphProperty</code> followed by the name of a transitivity
	 * graph property:</li>
	 * <ul>
	 * <li>CM or SCM for Simply Connected by Match relationships (default if
	 * <code>-appCommand</code> is not specified)</li>
	 * <li>BCM for Bi-Connected by Match relationships</li>
	 * <li>FCM for Fully Connected by Match relationships</li>
	 * </ul>
	 * </ul>
	 * Examples:
	 * <ul>
	 * <li><code>java com.choicemaker.cm.urm.client.app.UrmBmaApp
	 * -tp.GraphProperty SCM -appName epi-cm-server
	 * -appServer JBOSS 7654</code> <br>
	 * Invokes <code>UrmBmaApp</code> with JBoss as the application server and
	 * <code>SCM</code> as the transitivity graph property. Prints the job ID
	 * for the URM job that is started to run the transitivity analysis.</li>
	 * <br/>
	 * <li><code>java com.choicemaker.cm.urm.client.app.UrmBmaApp
	 * -appName cm-server 7654</code> <br>
	 * Same as above, using the default values for transitivity criterion and
	 * application server.<br/>
	 * <li><code>java com.choicemaker.cm.urm.client.app.UrmBmaApp
	 * -tp.GraphProperty BCM -appName epi-cm-server 7654</code> <br>
	 * Invokes <code>BCM</code> transitivity analysis.</li> <br/>
	 * </ul>
	 * 
	 * @param args
	 *            See above for details. If no arguments are specified, a help
	 *            message is printed.
	 */
	public static void main(String[] args) {
		logger.fine("Entering main");
		final UrmBmaApp2 app = new UrmBmaApp2();
		final PrintWriter stdout =
			new PrintWriter(new OutputStreamWriter(System.out));
		final PrintWriter stderr =
			new PrintWriter(new OutputStreamWriter(System.out));
		try {
			if (args == null || args.length == 0) {
				app.printHelp(stderr);
			} else {
				UrmParams2 up = UrmCommandLine2.parseCommandLine(args);
				assert up.errors != null;
				if (up.errors.size() > 0) {
					app.printErrors(stderr, up.errors);
					app.printHelp(stderr);
				} else {
					long jobId = app.recomputeTransitivity(up);
					String msg0 =
						"Started URM jobId %d to recompute "
						+ "transitivity for OABA jobId %d";
					String msg = String.format(msg0, jobId, up.jobId);
					System.out.println(msg);
				}
			}
		} catch (Exception e) {
			logger.severe(e.toString());
			e.printStackTrace();
		}
		stdout.flush();
		stderr.flush();
		logger.fine("Exiting main");
	}

	public long recomputeTransitivity(UrmParams2 up) throws RemoteException,
			NamingException, CreateException, ServerConfigurationException {
		Precondition.assertNonNullArgument("URM params must not be null", up);

		BatchJob oabaJob = getOabaBatchJob(up.appServer, up.appName, up.jobId);

		TransitivityParameters defaultTP =
			getTransitivityParameters(up.appServer, up.appName, up.jobId);
		TransitivityParameters tp =
			getTransitivityParametersOverride(defaultTP, up);

		OabaSettings defaultOS =
			getOabaSettings(up.appServer, up.appName, up.jobId);
		OabaSettings os = getOabaSettingsOverride(defaultOS, up);

		ServerConfiguration defaultSC =
			getServerConfiguration(up.appServer, up.appName, up.jobId);
		ServerConfiguration sc = getServerConfigurationOverride(defaultSC, up);

		BatchMatching bMatch = getBatchMatching(up.appServer, up.appName);

		long retVal = bMatch.startTransitivity("", tp, oabaJob, os, sc);

		return retVal;
	}

	public static TransitivityParameters getTransitivityParametersOverride(
			TransitivityParameters defaultTp, UrmParams2 up)
			throws RemoteException, NamingException, CreateException {
		Precondition.assertNonNullArgument(
				"Transitivity parameters must be non-null", defaultTp);
		Precondition.assertNonNullArgument("URM parameters must be non-null",
				up);

		final String defaultGraph = defaultTp.getGraphProperty().getName();
		final String graph =
			getTransitivityGraphOverride(defaultGraph, up, "tp.GraphProperty");
		final float lowThreshold = getThresholdOverride(
				defaultTp.getLowThreshold(), up, "LowThreshold");
		final float highThreshold = getThresholdOverride(
				defaultTp.getHighThreshold(), up, "HighThreshold");

		TransitivityParametersBean retVal =
			new TransitivityParametersBean(defaultTp);
		retVal.setGraph(graph);
		retVal.setLowThreshold(lowThreshold);
		retVal.setHighThreshold(highThreshold);

		assert retVal != null;
		return retVal;
	}

	public static OabaSettings getOabaSettingsOverride(OabaSettings defaultOS,
			UrmParams2 up) {
		Precondition.assertNonNullArgument("OABA settings must be non-null",
				defaultOS);
		Precondition.assertNonNullArgument("URM parameters must be non-null",
				up);

		// Stubbed for now
		return defaultOS;
	}

	public static ServerConfiguration getServerConfigurationOverride(
			ServerConfiguration defaultSC, UrmParams2 up) {
		Precondition.assertNonNullArgument(
				"Server configuration must be non-null", defaultSC);
		Precondition.assertNonNullArgument("URM parameters must be non-null",
				up);

		// Stubbed for now
		return defaultSC;
	}

	protected static float getThresholdOverride(float defaultThreshold,
			UrmParams2 up, String pn) {
		assert defaultThreshold >= 0.0f;
		assert defaultThreshold <= 1.0f;
		assert up != null;
		assert StringUtils.nonEmptyString(pn);
		String pv = up.tpOverrides.getProperty(pn);
		float retVal;
		if (StringUtils.nonEmptyString(pv)) {
			retVal = Float.valueOf(pv.trim());
			if (retVal < 0.0f || retVal > 1.0f) {
				throw new IllegalArgumentException(
						"Invalid threshold: " + retVal);
			}
		} else {
			retVal = defaultThreshold;
		}
		assert retVal >= 0.0f;
		assert retVal <= 1.0f;
		return retVal;
	}

	protected static String getTransitivityGraphOverride(String defaultGraph,
			UrmParams2 up, String pn) {
		assert StringUtils.nonEmptyString(defaultGraph);
		assert up != null;
		assert StringUtils.nonEmptyString(pn);
		String retVal;
		String pv = up.tpOverrides.getProperty(pn);
		if (StringUtils.nonEmptyString(pv)) {
			retVal = pv.trim();
		} else {
			retVal = defaultGraph;
		}
		assert StringUtils.nonEmptyString(retVal);
		return retVal;
	}

	public void printErrors(PrintWriter pw, List<String> errors) {
		Precondition.assertNonNullArgument("Print writer must be non-null", pw);

		if (errors != null && !errors.isEmpty()) {
			pw.println();
			pw.println("Errors:");
			for (String error : errors) {
				pw.println(error);
			}
		}
		pw.flush();
	}

	public void printHelp(PrintWriter pw) {
		Precondition.assertNonNullArgument("Print writer must be non-null", pw);

		Options options = UrmCommandLine2.createOptions();
		HelpFormatter formatter = new HelpFormatter();
		pw.println();
		final String footer = null;
		boolean autoUsage = false;
		formatter.printHelp(pw, formatter.getWidth(), COMMAND_LINE,
				COMMAND_HEADER, options, formatter.getLeftPadding(),
				formatter.getDescPadding(), footer, autoUsage);
		;
		pw.println();
		pw.flush();
	}

}
