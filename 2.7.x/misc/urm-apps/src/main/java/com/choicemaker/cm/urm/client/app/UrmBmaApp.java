/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.client.app;

import static com.choicemaker.cm.urm.api.RESULT_FILE_TYPE.MATCH;
import static com.choicemaker.cm.urm.api.RESULT_FILE_TYPE.TRANSGROUP;
import static com.choicemaker.cm.urm.api.RESULT_FILE_TYPE.TRANSMATCH;
import static com.choicemaker.cm.urm.client.app.UrmCommandLine.COMMAND_LINE;
import static com.choicemaker.cm.urm.client.app.UrmUtil.getBatchMatchAnalyzer;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.choicemaker.cm.urm.api.BatchMatchAnalyzer;
import com.choicemaker.cm.urm.api.RESULT_FILE_TYPE;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

public class UrmBmaApp {

	private static final Logger logger =
		Logger.getLogger(UrmBmaApp.class.getName());

	public static List<String> getResultFileNames(BatchMatchAnalyzer bma,
			long jobId, URM_BMA_APP_COMMAND cmd) throws CmRuntimeException {
		Precondition.assertNonNullArgument(
				"Batch-match analyzer must be non-null", bma);
		Precondition.assertNonNullArgument(
				"Application command must be non-null", cmd);

		RESULT_FILE_TYPE rft = getResultFileType(cmd);
		List<String> retVal = bma.getResultFileNames(jobId, rft);

		return retVal;
	}

	public static RESULT_FILE_TYPE getResultFileType(URM_BMA_APP_COMMAND cmd) {
		Precondition.assertNonNullArgument(
				"Application command must be non-null", cmd);

		RESULT_FILE_TYPE retVal = null;
		switch (cmd) {
		case MATCH_PAIR_FILENAMES:
			retVal = MATCH;
			break;
		case TRANSMATCH_PAIR_FILENAMES:
			retVal = TRANSMATCH;
		case TRANSMATCH_GROUP_FILENAMES:
			retVal = TRANSGROUP;
		default:
			String msg = "Unexpected execution path";
			throw new Error(msg);
		}
		assert retVal != null;

		return retVal;
	}

	/**
	 * Prints a list of result file names. Takes one required argument, two
	 * optional arguments, and a list of one or more job ids
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
	 * <li><code>-appCommand</code> followed by an application command:</li>
	 * <ul>
	 * <li>MATCH_PAIR_FILENAMES (default if <code>-appCommand</code> is not
	 * specified)</li>
	 * <li>TRANSMATCH_PAIR_FILENAMES</li>
	 * <li>TRANSMATCH_GROUP_FILENAMES</li>
	 * </ul>
	 * </ul>
	 * Examples:
	 * <ul>
	 * <li>
	 * <code>java com.choicemaker.cm.urm.client.app.UrmBmaApp -appName cm-server 1</code>
	 * <br>
	 * Invokes <code>UrmBmaApp</code> assuming JBoss as the application server
	 * and <code>MATCH_PAIR_FILENAMES</code> as the application command. Prints
	 * a list of the OABA pair-wise result file names (if the files exist) for
	 * the batch job with id <code>1</code>.</li> <br/>
	 * <li>
	 * <code>java com.choicemaker.cm.urm.client.app.UrmBmaApp -appName cm-server 1 2 3</code>
	 * <br>
	 * As above, but prints a list of the OABA pair-wise result file names (if
	 * the files exist) for the batch jobs with ids <code>1</code>,
	 * <code>2</code> and <code>3</code></li> <br/>
	 * <li>
	 * <code>java com.choicemaker.cm.urm.client.app.UrmBmaApp -appName cm-server -appServer JBoss 1 2 3</code>
	 * <br>
	 * Same as above.</li><br/>
	 * <li>
	 * <code>java com.choicemaker.cm.urm.client.app.UrmBmaApp -appName cm-server -appCommand TRANSMATCH_GROUP_FILENAMES 1</code>
	 * <br>
	 * Invokes <code>UrmBmaApp</code> assuming JBoss as the application server
	 * and <code>TRANSMATCH_GROUP_FILENAMES</code> as the application command.
	 * Prints a list of the Transitivity group-wise (a.k.a. <code>H3L</code>)
	 * result file names (if the files exist).</li>
	 * </ul>
	 * 
	 * @param args
	 *            See above for details. If no args are specified, then a help
	 *            message is printed.
	 */
	public static void main(String[] args) {
		logger.fine("Entering main");
		final UrmBmaApp app = new UrmBmaApp();
		try {
			PrintWriter stdout = new PrintWriter(System.out);
			PrintWriter stderr = new PrintWriter(System.out);
			if (args == null || args.length == 0) {
				app.printHelp(stderr);
			} else {
				UrmParams up = UrmCommandLine.parseCommandLine(args);
				assert up.errors != null;
				if (up.errors.size() > 0) {
					app.printErrors(stderr, up.errors);
				} else {
					for (long jobId : up.jobIds) {
						app.printFileNames(stdout, up.appServer, up.appName,
								jobId, up.appCommand);
					}
				}
			}
		} catch (Exception e) {
			logger.severe(e.toString());
			e.printStackTrace();
		}
		logger.fine("Exiting main");
	}

	public void printCommandJobId(PrintWriter pw, URM_BMA_APP_COMMAND cmd,
			long jobId) {
		Precondition.assertNonNullArgument("Print writer must be non-null", pw);

		String fmt = "\nJob %d:\n";
		if (cmd != null) {
			switch (cmd) {
			case MATCH_PAIR_FILENAMES:
				fmt = "\nJob %d match-pair files:\n";
				break;
			case TRANSMATCH_PAIR_FILENAMES:
				fmt = "\nJob %d transMatch-pair files:\n";
			case TRANSMATCH_GROUP_FILENAMES:
				fmt = "\nJob %d H3L files:\n";
			default:
				String msg = "Unexpected execution path";
				throw new Error(msg);
			}
		}
		assert StringUtils.nonEmptyString(fmt);

		String sJobId = String.format(fmt, jobId);
		pw.print(sJobId);
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
	}

	public void printFileList(PrintWriter pw, List<String> fileList) {
		Precondition.assertNonNullArgument("Print writer must be non-null", pw);
		Precondition.assertNonNullArgument("List must be non-null", fileList);

		for (String s : fileList) {
			pw.println(s);
		}
		pw.println("");
	}

	public void printFileNames(PrintWriter pw, APP_SERVER_VENDOR appServer,
			String appName, long jobId, URM_BMA_APP_COMMAND cmd)
			throws CmRuntimeException, RemoteException, NamingException,
			CreateException {
		Precondition.assertNonNullArgument("Print writer must be non-null", pw);
		Precondition.assertNonNullArgument(
				"Application server must be non-null", appServer);
		Precondition.assertNonNullArgument(
				"Application command must be non-null", cmd);
		Precondition.assertNonEmptyString("Application name must be non-empty",
				appName);

		BatchMatchAnalyzer bma = getBatchMatchAnalyzer(appServer, appName);
		List<String> fileNames = getResultFileNames(bma, jobId, cmd);
		printCommandJobId(pw, cmd, jobId);
		printFileList(pw, fileNames);
	}

	public void printHelp(PrintWriter pw) {
		Precondition.assertNonNullArgument("Print writer must be non-null", pw);

		Options options = UrmCommandLine.createOptions();
		HelpFormatter formatter = new HelpFormatter();
		pw.println();
		final String header = null;
		final String footer = null;
		boolean autoUsage = true;
		formatter.printHelp(pw, formatter.getWidth(), COMMAND_LINE, header,
				options, formatter.getLeftPadding(), formatter.getDescPadding(),
				footer, autoUsage);
		;
		pw.println();
	}

}
