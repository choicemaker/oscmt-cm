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
package com.choicemaker.cm.urm.client.app;

import static com.choicemaker.cm.urm.client.app.URM_BMA_APP_COMMAND.*;
import static com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR;

public class UrmCommandLine {

	public static final String ARG_APP_NAME = "appName";
	public static final String DESC_APP_NAME =
		"[REQUIRED] Name of a JEE application (the runtime name of an EAR file "
				+ "without the '.ear' extenson)";

	public static final String ARG_APP_COMMAND = "appCommand";

	private static final String DESC_APP_COMMAND0 =
		"[REQUIRED] Type of file names to print: %s, %s or %s";
	public static final String DESC_APP_COMMAND =
		String.format(DESC_APP_COMMAND0, MATCH_PAIR_FILENAMES,
				TRANSMATCH_PAIR_FILENAMES, TRANSMATCH_GROUP_FILENAMES);

	public static final String ARG_APP_SERVER_VENDOR = "appServer";

	private static final String DESC_APP_SERVER_VENDOR0 =
		"[OPTIONAL] Name of the app server vendor, either %s or %s; "
				+ "if no app server is specified, %s is the default";
	public static final String DESC_APP_SERVER_VENDOR =
		String.format(DESC_APP_SERVER_VENDOR0, JBOSS, WEBLOGIC, JBOSS);

	private static final String COMMAND_LINE0 =
		"java %s [options] <jobId 1> <jobId 2> ...";
	public static final String COMMAND_LINE = String.format(COMMAND_LINE0,
			com.choicemaker.cm.urm.client.app.UrmBmaApp.class.getName());

	public static final String COMMAND_HEADER = "where [options] are:";

	public static Options createOptions() {
		final boolean hasArg = true;
		Options retVal = new Options();
		Option opt;

		opt = new Option(ARG_APP_NAME, hasArg, DESC_APP_NAME);
		opt.setRequired(true);
		retVal.addOption(opt);

		opt = new Option(ARG_APP_COMMAND, hasArg, DESC_APP_COMMAND);
		opt.setRequired(true);
		retVal.addOption(opt);

		opt = new Option(ARG_APP_SERVER_VENDOR, hasArg, DESC_APP_SERVER_VENDOR);
		opt.setRequired(false);
		retVal.addOption(opt);

		return retVal;
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
	public static UrmParams parseCommandLine(String[] args)
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

		URM_BMA_APP_COMMAND appCommand = null;
		String sAppCommand = cl.getOptionValue(ARG_APP_COMMAND);
		if (sAppCommand == null || sAppCommand.isEmpty()) {
			errors.add(missingArgument(ARG_APP_COMMAND));
		} else {
			try {
				appCommand = URM_BMA_APP_COMMAND.valueOf(sAppCommand);
			} catch (Exception x) {
				assert appCommand == null;
				errors.add(invalidArgument(ARG_APP_COMMAND, sAppCommand));
			}
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

		// Remaining arguments should be jobIds
		List<Long> jobIds = new ArrayList<>();
		List<String> remainingArgs = cl.getArgList();
		for (String remainingArg : remainingArgs) {
			try {
				Long jobId = Long.valueOf(remainingArg);
				jobIds.add(jobId);
			} catch (Exception x) {
				errors.add(invalidArgument("JobId", remainingArg));
			}
		}
		if (jobIds.isEmpty()) {
			errors.add("No job ids specified");
		}

		UrmParams retVal =
			new UrmParams(appServer, appName, appCommand, jobIds, errors);
		return retVal;
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

	private UrmCommandLine() {
	}

}
