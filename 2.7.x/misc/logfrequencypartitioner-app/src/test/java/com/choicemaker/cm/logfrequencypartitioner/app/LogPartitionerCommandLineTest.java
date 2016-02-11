package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.CommandLineMaker.Params;
import static com.choicemaker.cm.logfrequencypartitioner.app.CommandLineMaker.inputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.CommandLineMaker.outputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerCommandLine.toCommandLine;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.natpryce.makeiteasy.Maker;

public class LogPartitionerCommandLineTest {

	public static final String TEMP_PREFIX = "LogPartitionerTest_";
	public static final String TEMP_SUFFIX = ".csv";

	public static final String VALID_VALUE_INPUT_FORMAT = "AnInputFormat";
	public static final String VALID_VALUE_INPUT_FIELD_SEP = "AnInputFieldSep";
	public static final String VALID_VALUE_INPUT_LINE_SEP = "AnInputLineSep";

	public static final String VALID_VALUE_OUTPUT_FILE = "AnOutputFile";
	public static final String VALID_VALUE_OUTPUT_FORMAT = "AnOutputFormat";
	public static final String VALID_VALUE_OUTPUT_FIELD_SEP =
		"AnOutputFieldSep";
	public static final String VALID_VALUE_OUTPUT_LINE_SEP = "AnOutputLineSep";
	public static final String VALID_VALUE_PARTITION_COUNT = "7";

	private File inputFile;
	private Maker<LogPartitionerParams> invalidParams;
	private Maker<LogPartitionerParams> validParams;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		this.inputFile = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);

		// Input file must exist (but may be empty)
		assertTrue(inputFile.exists());

		// Output file should be a valid path but must not exist
		final Path outputPath = Files.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
		outputPath.toFile().delete();
		assertTrue(!outputPath.toFile().exists());

		final String validInputFileName = inputFile.getAbsolutePath();
		final String validOutputFileName = outputPath.toFile().getAbsolutePath();

		invalidParams = a(Params);

		validParams =
			invalidParams.but(with(inputFileName, validInputFileName)).but(
					with(outputFileName, validOutputFileName));
	}

	@After
	public void tearDown() throws Exception {
		this.inputFile.delete();
		this.inputFile = null;
		this.invalidParams = null;
		this.validParams = null;
	}

	@Test
	public void testParseValidCommandLine() throws Exception {
		LogPartitionerParams lpp0 = make(validParams);
		String[] args = toCommandLine(lpp0);
		LogPartitionerParams lpp =
			LogPartitionerCommandLine.parseCommandLine(args);
		assertTrue(lpp != null);
		assertTrue(lpp.equals(lpp0));

	}

}
