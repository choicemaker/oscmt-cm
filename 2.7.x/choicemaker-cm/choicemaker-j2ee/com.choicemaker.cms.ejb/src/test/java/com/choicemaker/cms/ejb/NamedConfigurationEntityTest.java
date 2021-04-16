package com.choicemaker.cms.ejb;

import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_ABALIMITPERBLOCKINGSET;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_ABALIMITSINGLEBLOCKINGSET;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAINTERVAL;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXBLOCKSIZE;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXCHUNKSIZE;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXMATCHES;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXOVERSIZED;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXSINGLE;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMINFIELDS;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_SERVERMAXFILEENTRIES;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_SERVERMAXFILESCOUNT;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_SERVERMAXTHREADS;
import static com.choicemaker.util.ReflectionUtils.randomBoolean;
import static com.choicemaker.util.ReflectionUtils.randomFloat;
import static com.choicemaker.util.ReflectionUtils.randomInt;
import static com.choicemaker.util.ReflectionUtils.randomString;
import static com.choicemaker.util.ReflectionUtils.setProperty;
import static com.choicemaker.util.ReflectionUtils.testProperty;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NamedConfigurationEntityTest {

	// private Random random = new Random();

	@Test
	public void testNamedConfigurationEntityNamedConfiguration() {

		// Create a default configuration
		NamedConfigurationEntity nc0 = new NamedConfigurationEntity();

		// Set non-default values
		setProperty(nc0, String.class, "ConfigurationName", randomString());
		setProperty(nc0, String.class, "ModelName", randomString());
		setProperty(nc0, float.class, "LowThreshold", randomFloat());
		setProperty(nc0, float.class, "HighThreshold", randomFloat());

		// FIXME Fails randomly because new value == exiting value
		// int count = OabaLinkageType.values().length;
		// int idx = random.nextInt(count);
		// String s = OabaLinkageType.values()[idx].name();
		// setProperty(nc0, String.class, "Task", s);

		// FIXME Fails randomly because new value == exiting value
		// count = BatchJobRigor.values().length;
		// idx = random.nextInt(count);
		// s = BatchJobRigor.values()[idx].name();
		// setProperty(nc0, String.class, "Rigor", s);

		// setProperty(nc0, String.class, "RecordSourceType", randomString());
		setProperty(nc0, String.class, "DataSource", randomString());
		setProperty(nc0, String.class, "JdbcDriverClassName", randomString());
		setProperty(nc0, String.class, "BlockingConfiguration", randomString());
		setProperty(nc0, String.class, "QuerySelection", randomString());
		setProperty(nc0, String.class, "QueryDatabaseConfiguration",
				randomString());
		// FIXME Fails randomly because new value == exiting value
		// setProperty(nc0, boolean.class, "QueryDeduplicated",
		// randomBoolean());
		// END FIXME
		setProperty(nc0, String.class, "ReferenceSelection", randomString());
		setProperty(nc0, String.class, "ReferenceDatabaseConfiguration",
				randomString());

		// FIXME Fails randomly because new value == exiting value
		// count = AnalysisResultFormat.values().length;
		// idx = random.nextInt(count);
		// s = AnalysisResultFormat.values()[idx].name();
		// setProperty(nc0, String.class, "TransitivityFormat", s);

		setProperty(nc0, String.class, "TransitivityGraph", randomString());
		setProperty(nc0, int.class, "AbaLimitPerBlockingSet",
				randomInt(DEFAULT_ABALIMITPERBLOCKINGSET));
		setProperty(nc0, int.class, "AbaLimitSingleBlockingSet",
				randomInt(DEFAULT_ABALIMITSINGLEBLOCKINGSET));
		setProperty(nc0, int.class, "AbaSingleTableBlockingSetGraceLimit",
				randomInt(DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT));
		setProperty(nc0, int.class, "OabaMaxSingle",
				randomInt(DEFAULT_OABAMAXSINGLE));
		setProperty(nc0, int.class, "OabaMaxBlockSize",
				randomInt(DEFAULT_OABAMAXBLOCKSIZE));
		setProperty(nc0, int.class, "OabaMaxChunkSize",
				randomInt(DEFAULT_OABAMAXCHUNKSIZE));
		setProperty(nc0, int.class, "OabaMaxOversized",
				randomInt(DEFAULT_OABAMAXOVERSIZED));
		setProperty(nc0, int.class, "OabaMaxMatches",
				randomInt(DEFAULT_OABAMAXMATCHES));
		setProperty(nc0, int.class, "OabaMinFields",
				randomInt(DEFAULT_OABAMINFIELDS));
		setProperty(nc0, int.class, "OabaInterval",
				randomInt(DEFAULT_OABAINTERVAL));
		setProperty(nc0, int.class, "ServerMaxThreads",
				randomInt(DEFAULT_SERVERMAXTHREADS));
		setProperty(nc0, int.class, "ServerMaxFileEntries",
				randomInt(DEFAULT_SERVERMAXFILEENTRIES));
		setProperty(nc0, int.class, "ServerMaxFilesCount",
				randomInt(DEFAULT_SERVERMAXFILESCOUNT));
		setProperty(nc0, String.class, "ServerFileURI", randomString());

		// Create a copy of the modified configuration
		NamedConfigurationEntity nc1 = new NamedConfigurationEntity(nc0);

		// Verify that the two configurations are equal but not identical
		assertTrue(nc1 != nc0);
		assertTrue(nc1.equalsIgnoreIdentityFields(nc0));
	}

	@Test
	public void testConfigurationName() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"ConfigurationName", randomString());
	}

	@Test
	public void testModelName() {
		testProperty(new NamedConfigurationEntity(), String.class, "ModelName",
				randomString());
	}

	@Test
	public void testLowThreshold() {
		testProperty(new NamedConfigurationEntity(), float.class,
				"LowThreshold", randomFloat());
	}

	@Test
	public void testHighThreshold() {
		testProperty(new NamedConfigurationEntity(), float.class,
				"HighThreshold", randomFloat());
	}

	@Test
	public void testTask() {
		// int count = OabaLinkageType.values().length;
		// int idx = random.nextInt(count);
		// String s = OabaLinkageType.values()[idx].name();
		// testProperty(new NamedConfigurationEntity(), String.class, "Task",
		// s);
		testProperty(new NamedConfigurationEntity(), String.class, "Task",
				randomString());
	}

	// @Test
	public void testRigor() {
		// FIXME Fails randomly because new value == exiting value
		// int count = BatchJobRigor.values().length;
		// int idx = random.nextInt(count);
		// String s = BatchJobRigor.values()[idx].name();
		// testProperty(new NamedConfigurationEntity(), String.class, "Rigor",
		// s);
		testProperty(new NamedConfigurationEntity(), String.class, "Rigor",
				randomString());
	}

	@Test
	public void testRecordSourceType() {
		// No test: final value is always 'SQL'
		// testProperty(new NamedConfigurationEntity(), String.class,
		// "RecordSourceType", randomString());
	}

	@Test
	public void testDataSource() {
		testProperty(new NamedConfigurationEntity(), String.class, "DataSource",
				randomString());
	}

	@Test
	public void testJdbcDriverClassName() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"JdbcDriverClassName", randomString());
	}

	@Test
	public void testBlockingConfiguration() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"BlockingConfiguration", randomString());
	}

	@Test
	public void testQuerySelection() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"QuerySelection", randomString());
	}

	@Test
	public void testQueryDatabaseConfiguration() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"QueryDatabaseConfiguration", randomString());
	}

	@Test
	public void testQueryDeduplicated() {
		// FIXME Fails randomly because new value == exiting value
		testProperty(new NamedConfigurationEntity(), boolean.class,
				"QueryDeduplicated", randomBoolean());
	}

	@Test
	public void testReferenceSelection() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"ReferenceSelection", randomString());
	}

	@Test
	public void testReferenceDatabaseConfiguration() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"ReferenceDatabaseConfiguration", randomString());
	}

	@Test
	public void testTransitivityFormat() {
		// FIXME Fails randomly because new value == exiting value
		// int count = AnalysisResultFormat.values().length;
		// int idx = random.nextInt(count);
		// String s = AnalysisResultFormat.values()[idx].name();
		// testProperty(new NamedConfigurationEntity(), String.class,
		// "TransitivityFormat", s);
		testProperty(new NamedConfigurationEntity(), String.class,
				"TransitivityFormat", randomString());
	}

	@Test
	public void testTransitivityGraph() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"TransitivityGraph", randomString());
	}

	@Test
	public void testAbaLimitPerBlockingSet() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"AbaLimitPerBlockingSet",
				randomInt(DEFAULT_ABALIMITPERBLOCKINGSET));
	}

	@Test
	public void testAbaLimitSingleBlockingSet() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"AbaLimitSingleBlockingSet",
				randomInt(DEFAULT_ABALIMITSINGLEBLOCKINGSET));
	}

	@Test
	public void testAbaSingleTableBlockingSetGraceLimit() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"AbaSingleTableBlockingSetGraceLimit",
				randomInt(DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT));
	}

	@Test
	public void testOabaMaxSingle() {
		testProperty(new NamedConfigurationEntity(), int.class, "OabaMaxSingle",
				randomInt(DEFAULT_OABAMAXSINGLE));
	}

	@Test
	public void testOabaMaxBlockSize() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"OabaMaxBlockSize", randomInt(DEFAULT_OABAMAXBLOCKSIZE));
	}

	@Test
	public void testOabaMaxChunkSize() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"OabaMaxChunkSize", randomInt(DEFAULT_OABAMAXCHUNKSIZE));
	}

	@Test
	public void testOabaMaxOversized() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"OabaMaxOversized", randomInt(DEFAULT_OABAMAXOVERSIZED));
	}

	@Test
	public void testOabaMaxMatches() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"OabaMaxMatches", randomInt(DEFAULT_OABAMAXMATCHES));
	}

	@Test
	public void testOabaMinFields() {
		testProperty(new NamedConfigurationEntity(), int.class, "OabaMinFields",
				randomInt(DEFAULT_OABAMINFIELDS));
	}

	@Test
	public void testOabaInterval() {
		testProperty(new NamedConfigurationEntity(), int.class, "OabaInterval",
				randomInt(DEFAULT_OABAINTERVAL));
	}

	@Test
	public void testServerMaxThreads() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"ServerMaxThreads", randomInt(DEFAULT_SERVERMAXTHREADS));
	}

	@Test
	public void testServerMaxChunkSize() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"ServerMaxFileEntries", randomInt(DEFAULT_SERVERMAXFILEENTRIES));
	}

	@Test
	public void testServerMaxChunkCount() {
		testProperty(new NamedConfigurationEntity(), int.class,
				"ServerMaxFilesCount", randomInt(DEFAULT_SERVERMAXFILESCOUNT));
	}

	@Test
	public void testServerFileURI() {
		testProperty(new NamedConfigurationEntity(), String.class,
				"ServerFileURI", randomString());
	}

}
