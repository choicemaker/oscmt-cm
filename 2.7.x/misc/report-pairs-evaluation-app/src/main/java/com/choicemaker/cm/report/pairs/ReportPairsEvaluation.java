package com.choicemaker.cm.report.pairs;

import static com.choicemaker.cm.report.pairs.ReportPairsCommandLine.COMMAND_LINE;
import static com.choicemaker.e2.platform.InstallablePlatform.INSTALLABLE_PLATFORM;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.SystemPropertyUtils;

public class ReportPairsEvaluation {

	static final Logger logger = Logger.getLogger(ReportPairsEvaluation.class.getName());

	public static final int STATUS_OK = 0;
	public static final int ERROR_BAD_INPUT = 1;

	public ReportPairsEvaluation(Map<String, String> configuration) {
		this(configuration,true);
	}

	ReportPairsEvaluation(Map<String, String> configuration, boolean doValidation) {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {

		logger.fine("LogPartitionerApp (main) args: " + Arrays.toString(args));

		SystemPropertyUtils.logSystemProperties(logger);

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);

		int exitCode = STATUS_OK;
		PrintWriter console = null;
		try {
			console = new PrintWriter(new OutputStreamWriter(System.out));

			final ReportPairsParameters appParms =
				ReportPairsCommandLine.parseCommandLine(args);
			assert appParms != null;
			final Map<String, String> configuration =
				appParms.getProperties();
			final String[] reportFiles = appParms.getReportFilePaths();

			if (appParms.isHelp() && !appParms.hasErrors()) {
				printHelp(console);
				exitCode = STATUS_OK;
			} else if (appParms.isHelp() && appParms.hasErrors()) {
				printErrors(console, appParms.getErrors());
				printHelp(console);
				exitCode = ERROR_BAD_INPUT;
			} else if (appParms.hasErrors()) {
				assert !appParms.isHelp();
				printErrors(console, appParms.getErrors());
				printUsage(console);
				exitCode = ERROR_BAD_INPUT;
			} else {
				assert !appParms.isHelp();
				assert !appParms.hasErrors();
				final boolean doValidation = false;
				final ReportPairsEvaluation app = new ReportPairsEvaluation(configuration, doValidation);
				for (String reportFile : reportFiles) {
					app.processReportFile(reportFile);
				}
				exitCode = STATUS_OK;
			}
			console.flush();

		} catch (Exception x) {
			x.printStackTrace(console);

		} finally {
			if (console != null) {
				console.close();
			}
		}

		System.exit(exitCode);
	}

	public void processReportFile(String reportFile) {
		// TODO Auto-generated method stub

	}

	public static void printErrors(PrintWriter pw, List<String> errors)
			throws IOException {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		if (errors != null && !errors.isEmpty()) {
			pw.println();
			pw.println("Errors:");
			for (String error : errors) {
				pw.println(error);
			}
		}
	}

	public static void printHelp(PrintWriter pw) {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		Options options = ReportPairsCommandLine.createOptions();
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

	public static void printUsage(PrintWriter pw) {
		if (pw == null) {
			throw new IllegalArgumentException("null writer");
		}
		Options options = ReportPairsCommandLine.createOptions();
		HelpFormatter formatter = new HelpFormatter();
		pw.println();
		formatter.printUsage(pw, formatter.getWidth(), COMMAND_LINE, options);
		pw.println();
	}

}
