/*******************************************************************************
 * Copyright (c) 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.urm.client.app2;

import static com.choicemaker.cm.urm.client.app2.UrmParams2.PREFIX_TRANSITIVITY_PARAMS;
import static com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR.JBOSS;
import static com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR.WEBLOGIC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR;
import com.choicemaker.util.StringUtils;

public class UrmCommandLine2 {

	public static final String ARG_APP_NAME = "appName";
	public static final String DESC_APP_NAME =
		"[REQUIRED] Name of a JEE application (the runtime name of an EAR file "
				+ "without the '.ear' extenson)";

	public static final String ARG_APP_SERVER_VENDOR = "appServer";

	private static final String DESC_APP_SERVER_VENDOR0 =
		"[OPTIONAL] Name of the app server vendor, either %s or %s; "
				+ "if no app server is specified, %s is the default";
	public static final String DESC_APP_SERVER_VENDOR =
		String.format(DESC_APP_SERVER_VENDOR0, JBOSS, WEBLOGIC, JBOSS);

	private static final String COMMAND_LINE0 = "java %s [options] <jobId>";
	public static final String COMMAND_LINE = String.format(COMMAND_LINE0,
			com.choicemaker.cm.urm.client.app.UrmBmaApp.class.getName());

	public static final String COMMAND_HEADER = "where [options] are:";

	public static Set<String> createTPOptions() {
		Set<String> retVal = new HashSet<>();
		retVal.add(PREFIX_TRANSITIVITY_PARAMS + "GraphProperty");
		retVal.add(PREFIX_TRANSITIVITY_PARAMS + "LowThreshold");
		retVal.add(PREFIX_TRANSITIVITY_PARAMS + "HighThreshold");
		return retVal;
	}

	public static Set<String> createOabaOptions() {
		return Collections.emptySet();
	}

	public static Set<String> createSCOptions() {
		return Collections.emptySet();
	}

	public static Options createOptions() {
		final boolean hasArg = true;
		Options retVal = new Options();
		Option opt;

		opt = new Option(ARG_APP_NAME, hasArg, DESC_APP_NAME);
		opt.setRequired(true);
		retVal.addOption(opt);

		opt = new Option(ARG_APP_SERVER_VENDOR, hasArg, DESC_APP_SERVER_VENDOR);
		opt.setRequired(false);
		retVal.addOption(opt);

		addOptions(retVal, "TranstivityParameters", createTPOptions());
		addOptions(retVal, "OabaSettings", createOabaOptions());
		addOptions(retVal, "ServerConfiguration", createSCOptions());

		return retVal;
	}

	protected static void addOptions(Options options, String tag,
			Set<String> optNames) {
		assert options != null;
		assert StringUtils.nonEmptyString(tag);
		assert optNames != null;
		final boolean hasArg = true;
		final String desc0 = "%s: %s";
		for (String s : optNames) {
			String desc = String.format(desc0, tag, s);
			Option opt = new Option(s, hasArg, desc);
			opt.setRequired(false);
			options.addOption(opt);
		}
	}

	/**
	 * Parse the command line arguments into parameters for the
	 * LogPartitionerApp.
	 *
	 * @param args
	 *            non-null array of command-line arguments
	 * @return LogPartitioner parameters, possibly with errors
	 * @throws ParseException
	 * @throws IOException
	 */
	public static UrmParams2 parseCommandLine(String[] args)
			throws ParseException, IOException {

		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cl = parser.parse(options, args);

		List<String> errors = new ArrayList<>();

		// Required
		String appName = cl.getOptionValue(ARG_APP_NAME);
		if (appName != null) {
			appName = appName.trim();
		}
		if (appName == null || appName.isEmpty()) {
			errors.add(missingArgument(ARG_APP_NAME));
		}

		// Optional
		APP_SERVER_VENDOR appServer = APP_SERVER_VENDOR.JBOSS;
		String sAppServer = cl.getOptionValue(ARG_APP_SERVER_VENDOR);
		if (sAppServer != null) {
			sAppServer = sAppServer.trim();
			if (!sAppServer.isEmpty()) {
				try {
					appServer = APP_SERVER_VENDOR.valueOf(sAppServer);
				} catch (IllegalArgumentException x) {
					errors.add(
							invalidArgument(ARG_APP_SERVER_VENDOR, sAppServer));
				}
			}
		}

		// Optional
		Properties p = new Properties();
		getOptions(cl, p, createTPOptions());
		getOptions(cl, p, createOabaOptions());
		getOptions(cl, p, createSCOptions());

		// Remaining argument should be jobId
		int count = 0;
		long jobId = 0;
		List<String> remainingArgs = cl.getArgList();
		for (String remainingArg : remainingArgs) {
			++count;
			if (count > 1) {
				errors.add(invalidArgument("JobId", remainingArg));
				continue;
			}
			try {
				jobId = Long.valueOf(remainingArg);
			} catch (Exception x) {
				errors.add(invalidArgument("JobId", remainingArg));
			}
		}
		if (count == 0) {
			errors.add("No job ids specified");
		}
		if (jobId == 0) {
			errors.add("Invalid job id (zero) specified");
		}

		UrmParams2 retVal =
			new UrmParams2(appServer, appName, jobId, p, errors);
		return retVal;
	}

	protected static void getOptions(CommandLine cl, Properties p,
			Set<String> optNames) {
		assert cl != null;
		assert p != null;
		assert optNames != null;
		for (String pn : optNames) {
			String pv = cl.getOptionValue(pn);
			if (pv != null) {
				pv = pv.trim();
				if (!pv.isEmpty()) {
					p.setProperty(pn, pv);
				}
			}
		}
	}

	public static String invalidArgument(String argName, String argValue) {
		String msg0 = "Invalid value ('%s') for the '%s' argument";
		String retVal = String.format(msg0, argValue, argName);
		return retVal;
	}

	public static String missingArgument(String argName) {
		String msg0 = "Missing the required '%s' argument";
		String retVal = String.format(msg0, argName);
		return retVal;
	}

	private UrmCommandLine2() {
	}

}
