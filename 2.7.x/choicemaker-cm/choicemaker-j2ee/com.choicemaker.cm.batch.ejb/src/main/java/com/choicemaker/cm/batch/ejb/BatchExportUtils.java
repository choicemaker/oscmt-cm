package com.choicemaker.cm.batch.ejb;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

public class BatchExportUtils {

	private static final Logger logger =
			Logger.getLogger(BatchExportUtils.class.getName());

	public static final String FILE_PROTOCOL = "file";

	public static final String URL_PATH_SEPARATOR = "/";

	public static void exportResults(BatchJob batchJob, URI container,
			OperationalPropertyController propController, String propertyName)
			throws IOException, URISyntaxException {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertNonNullArgument("null url", container);
		Precondition.assertBoolean(
				"not a local file URL: " + container.toString(),
				isLocalFileURL(container));
		Precondition.assertNonNullArgument("null properties controller",
				propController);
		Precondition.assertNonEmptyString("null or blank property name",
				propertyName);

		BatchJobStatus status = batchJob.getStatus();
		if (BatchJobStatus.COMPLETED != status) {
			String msg =
				"Batch job not completed. Current jobs status: " + status;
			throw new IllegalStateException(msg);
		}

		// Use cached results for now.
		// FIXME Use results from DB
		// FIXME This method falls apart if there are several result files
		final String resultFileName = propController.getJobProperty(batchJob,
				propertyName);
		logger.finer("OABA result file: " + resultFileName);
		if (!StringUtils.nonEmptyString(resultFileName)) {
			String msg = "Invalid result file name: '" + resultFileName + "'";
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}
		File resultFile = new File(resultFileName);
		if (!resultFile.exists()) {
			String msg = "Result file does not exist: " + resultFileName;
			logger.severe(msg);
			throw new IllegalStateException(msg);
		} else if (!resultFile.canRead()) {
			String msg = "Result file is not readable: " + resultFileName;
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}

		// Create output file of the same name
		final String strContainerPath = container.toURL().toExternalForm();
		final String baseName = resultFile.getName();
		String strOutputPath;
		if (strContainerPath.endsWith(URL_PATH_SEPARATOR)) {
			strOutputPath = strContainerPath + baseName;
		} else {
			strOutputPath = strContainerPath + URL_PATH_SEPARATOR + baseName;
		}
		logger.finer("Output path: " + strOutputPath);

		URI outputPathURI = new URI(strOutputPath);
		Path outputPath = Paths.get(outputPathURI);
		Path inputPath = resultFile.toPath();
		Files.copy(inputPath, outputPath);
	}

	public static boolean isLocalFileURL(URI uri) throws MalformedURLException {
		boolean retVal = false;
		if (uri == null) {
			logger.finer("null uri");
			assert retVal == false;
		} else {
			URL url = uri.toURL();
			String protocol = url.getProtocol();
			if (!StringUtils.nonEmptyString(protocol)) {
				logger.finer("empty protocol: " + url.toString());
				retVal = true;
			} else if (FILE_PROTOCOL.equals(protocol)) {
				logger.finer("file protocol: " + url.toString());
				retVal = true;
			} else {
				logger.finer("not a local file protocol: " + url.toString());
				assert retVal == false;
			}
		}
		return retVal;
	}

	private BatchExportUtils() {
	}

}
