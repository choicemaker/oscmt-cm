package com.choicemaker.cm.report.pairs;

import static com.choicemaker.cm.report.pairs.ReportPairsParameters.PN_DIFFER_THRESHOLD;
import static com.choicemaker.cm.report.pairs.ReportPairsParameters.PN_MATCH_THREHOLD;
import static com.choicemaker.cm.report.pairs.ReportPairsParameters.PN_MODEL_PLUGIN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReportPairsParametersTest {

	public static final String PROPERTY_FILE_PREFIX =
		"ReportPairsParametersTest_FakeProperties_";
	public static final String PROPERTY_FILE_SUFFIX = ".properties";

	public static final String REPORT_FILE_PREFIX =
		"ReportPairsParametersTest_FakeReport_";
	public static final String REPORT_FILE_SUFFIX = ".xml";

	private static Map<String, String> _completeProperties = new HashMap<>();
	static {
		_completeProperties.put(PN_MODEL_PLUGIN, "FakeModel");
		_completeProperties.put(PN_DIFFER_THRESHOLD, "0.20");
		_completeProperties.put(PN_MATCH_THREHOLD, "0.80");
	}
	public static Map<String, String> completeProperties =
		Collections.unmodifiableMap(_completeProperties);

	public static List<Map<String, String>> incompletePropertyExamples() {
		List<Map<String, String>> retVal = new ArrayList<>();
		final int count = completeProperties.size();
		final String[] keys =
			completeProperties.keySet().toArray(new String[count]);
		for (int i = 0; i < count; i++) {
			Map<String, String> m = new HashMap<>();
			m.putAll(completeProperties);
			m.remove(keys[i]);
			retVal.add(m);
		}
		return retVal;
	}

	private static File createPropertyFile(Map<String, String> p)
			throws IOException {
		assertTrue(p != null);
		File retVal =
			File.createTempFile(PROPERTY_FILE_PREFIX, PROPERTY_FILE_SUFFIX);
//		retVal.deleteOnExit();
		FileWriter fw = new FileWriter(retVal);
		Properties props = new Properties();
		for (Entry<String, String> e : p.entrySet()) {
			props.setProperty(e.getKey(), e.getValue());
		}
		props.store(fw, null);
		fw.close();
		return retVal;
	}

	private static File createReportFile() throws IOException {
		File retVal =
			File.createTempFile(PROPERTY_FILE_PREFIX, PROPERTY_FILE_SUFFIX);
		retVal.deleteOnExit();
		return retVal;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		fakePropertyFile = createPropertyFile(completeProperties);
		fakePropertyPath = fakePropertyFile.getAbsolutePath();
		File fakeReportFile = createReportFile();
		fakeReportFiles = new File[] {
				fakeReportFile };
		final int count = fakeReportFiles.length;
		fakeReportPaths = new String[count];
		for (int i=0; i<count; i++) {
			fakeReportPaths[i] = fakeReportFiles[i].getAbsolutePath();
		}
	}

	@After
	public void tearDown() throws Exception {
		fakePropertyFile.delete();
		for (File f : fakeReportFiles) {
			f.delete();
		}
	}

	private File fakePropertyFile;
	private String fakePropertyPath;
	private File[] fakeReportFiles;
	private String[] fakeReportPaths;

	@Test
	public void testFakeParameters() throws Exception {
		boolean isHelpRequested = true;
		ReportPairsParameters rpp = new ReportPairsParameters(isHelpRequested,
				fakePropertyPath, fakeReportPaths);
		assertTrue(rpp.isHelp());
		assertTrue(!rpp.hasErrors());

		isHelpRequested = false;
		rpp = new ReportPairsParameters(isHelpRequested, fakePropertyPath,
				fakeReportPaths);
		assertFalse(rpp.isHelp());
		assertTrue(!rpp.hasErrors());
	}

	@Test
	public void testHelp() {
		boolean isHelpRequested = true;
		ReportPairsParameters rpp =
			new ReportPairsParameters(isHelpRequested, null, null);
		assertTrue(rpp.isHelp());

		isHelpRequested = false;
		rpp = new ReportPairsParameters(isHelpRequested, null, null);
		assertFalse(rpp.isHelp());
	}

	public void testNullProperties(boolean isHelpRequested) {
		ReportPairsParameters rpp =
			new ReportPairsParameters(isHelpRequested, null, fakeReportPaths);
		assertTrue(rpp.hasErrors());
	}

	@Test
	public void testNullProperties() {
		boolean isHelpRequested = true;
		testNullProperties(isHelpRequested);
		isHelpRequested = false;
		testNullProperties(isHelpRequested);
	}

	public void testMissingProperties(boolean isHelpRequested,
			String propertyPath) {
		ReportPairsParameters rpp = new ReportPairsParameters(isHelpRequested,
				propertyPath, fakeReportPaths);
		assertTrue(rpp.hasErrors());
	}

	@Test
	public void testMissingProperties() throws IOException {
		boolean isHelpRequested = true;
		for (Map<String, String> m : incompletePropertyExamples()) {
			File propertyFile = createPropertyFile(m);
			String propertyPath = propertyFile.getAbsolutePath();
			testMissingProperties(isHelpRequested, propertyPath);
			propertyFile.delete();
		}
		isHelpRequested = false;
		for (Map<String, String> m : incompletePropertyExamples()) {
			File propertyFile = createPropertyFile(m);
			String propertyPath = propertyFile.getAbsolutePath();
			testMissingProperties(isHelpRequested, propertyPath);
			propertyFile.delete();
		}
	}

	public void testNullReports(boolean isHelpRequested) {
		ReportPairsParameters rpp =
			new ReportPairsParameters(isHelpRequested, fakePropertyPath, null);
		assertTrue(rpp.hasErrors());
	}

	@Test
	public void testNullReports() {
		boolean isHelpRequested = true;
		testNullReports(isHelpRequested);
		isHelpRequested = false;
		testNullReports(isHelpRequested);
	}

	public void testMissingReports(boolean isHelpRequested) {
		String[] missingReports = new String[0];
		ReportPairsParameters rpp = new ReportPairsParameters(isHelpRequested,
				fakePropertyPath, missingReports);
		assertTrue(rpp.hasErrors());
	}

	@Test
	public void testMissingReports() throws IOException {
		boolean isHelpRequested = true;
		testMissingReports(isHelpRequested);
		isHelpRequested = false;
		testMissingReports(isHelpRequested);
	}

}
