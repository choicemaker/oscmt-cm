package com.choicemaker.cm.report.pairs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.choicemaker.util.Precondition;

public class ReportPairsParameters {

	public static final String PN_MODEL_PLUGIN = "reportFile.modelPlugin";
	public static final String PN_DIFFER_THRESHOLD =
		"reportFile.differThreshold";
	public static final String PN_MATCH_THREHOLD = "reportFile.matchThreshold";

	private static final String[] requiredProperties = new String[] {
			PN_MODEL_PLUGIN, PN_DIFFER_THRESHOLD, PN_MATCH_THREHOLD };
	
	public static final String[] getRequiredProperties() {
		String[] retVal = new String[requiredProperties.length];
		System.arraycopy(requiredProperties, 0, retVal, 0, retVal.length);
		return retVal;
	}

	public static boolean isValidFilePath(String filePath) {
		boolean retVal = false;
		if (filePath != null && filePath.trim().length() > 0) {
			File f = new File(filePath);
			if (f.exists() && f.isFile() && f.canRead()) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static Map<String, String> loadProperties(File f)
			throws IOException {
		Precondition.assertBoolean(f.exists() && f.isFile() && f.canRead());
		Properties p = new Properties();
		FileReader fr = new FileReader(f);
		p.load(fr);
		Map<String, String> retVal = new HashMap<>();
		for (Entry<Object, Object> e : p.entrySet()) {
			if (e.getKey() instanceof String
					&& e.getValue() instanceof String) {
				String k = (String) e.getKey();
				String v = (String) e.getValue();
				retVal.put(k, v);
			}
		}
		return retVal;
	}

	public static List<String> validateProperties(
			Map<String, String> properties) {
		String problem;
		List<String> problems = new ArrayList<>();
		if (properties == null) {
			problem = "Null property map";
			problems.add(problem);
		}
		for (String k : requiredProperties) {
			String v = properties.get(k);
			if (v == null) {
				problem = "missing property '" + k + "'";
				problems.add(problem);
			}
		}
		return problems;
	}

	public static List<String> validateReportFile(File reportFile) {
		// No easy way to validate completely, so just check the basics
		String problem;
		List<String> problems = new ArrayList<>();
		if (reportFile == null) {
			problem = "Null report file";
			problems.add(problem);
		} else {
			String path = reportFile.getAbsolutePath();
			if (!reportFile.exists()) {
				problem = "File does not exist: " + path;
			}
			if (!reportFile.isFile()) {
				problem = "Not a file: " + path;
			}
			if (!reportFile.canRead()) {
				problem = "Can not read file: " + path;
			}
		}
		return problems;
	}

	private final boolean isHelpRequest;
	private final List<String> errors;
	private final File propertyFile;
	private final Map<String, String> properties;
	private final List<File> reportFiles;

	public ReportPairsParameters(final boolean isHelpRequested,
			final String propertyFilePath, final String[] reportFilePaths) {

		List<String> problems = new ArrayList<>();
		final boolean isValidPropertyFilePath =
			isValidFilePath(propertyFilePath);
		if (!isValidPropertyFilePath) {
			String problem =
				"not a valid property file path: '" + propertyFilePath + "'";
			problems.add(problem);
		}

		final boolean isNonNullArray = reportFilePaths != null;
		if (!isNonNullArray) {
			String problem = "null array of report file paths";
			problems.add(problem);
		}

		final boolean isNonEmptyArray =
			isNonNullArray && reportFilePaths.length > 0;
		if (!isNonEmptyArray) {
			String problem = "no report files specified";
			problems.add(problem);
		}

		// Create and validate properties
		File propFile = null;
		Map<String, String> props = Collections.emptyMap();
		if (isValidPropertyFilePath) {
			propFile = new File(propertyFilePath);
			try {
				props = loadProperties(propFile);
				List<String> propertyProblems =
					validateProperties(props);
				if (!propertyProblems.isEmpty()) {
					problems.add("invalid property file: '" + propertyFilePath
							+ "': " + propertyProblems);
				}
			} catch (Exception x) {
				assert props.isEmpty();
				problems.add(x.toString());
			}
		}

		// Create and validate report files
		List<File> rFiles = new ArrayList<>();
		if (isNonEmptyArray) {
			for (String reportFilePath : reportFilePaths) {
				boolean isValidReportFilePath = isValidFilePath(reportFilePath);
				if (!isValidReportFilePath) {
					String problem =
						"not a valid report file path: '" + reportFilePath + "'";
					problems.add(problem);
				}
				File reportFile = new File(reportFilePath);
				List<String> reportProblems = validateReportFile(reportFile);
				if (reportProblems.isEmpty()) {
					rFiles.add(reportFile);
				} else {
					problems.add("invalid report file: '" + reportFilePath
							+ "': " + reportProblems);
				}
			}
		}

		// Finish construction
		this.isHelpRequest = isHelpRequested;
		this.errors = Collections.unmodifiableList(problems);
		this.propertyFile = propFile;
		this.properties = Collections.unmodifiableMap(props);
		this.reportFiles = Collections.unmodifiableList(rFiles);
		invariant();
	}

	public void invariant() {
		if (!isConsistent()) {
			throw new IllegalStateException("instance is not valid");
		}
	}

	public boolean isConsistent() {
		boolean isValidPropertyCount = this.properties.size() > 0;
		boolean isValidReportCount = this.reportFiles.size() > 0;
		boolean retVal = isHelpRequest || hasErrors()
				|| (isValidPropertyCount && isValidReportCount);
		return retVal;
	}

	public File getPropertyFile() {
		return this.propertyFile;
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public List<File> getReportFiles() {
		return this.reportFiles;
	}

	public List<String> getErrors() {
		return this.errors;
	}

	public boolean hasErrors() {
		return this.errors.size() > 0;
	}

	public boolean isHelp() {
		return isHelpRequest;
	}

}
