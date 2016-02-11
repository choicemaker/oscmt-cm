package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.BaseParams;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.inputFieldSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.inputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.inputFormat;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.inputLineSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.outputFieldSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.outputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.outputFormat;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.outputLineSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.ParameterMaker.partitionCount;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.natpryce.makeiteasy.Maker;

public class LogPartitionerParametersTest {

	private static final Logger logger = Logger
			.getLogger(LogPartitionerParametersTest.class.getName());

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
	private Maker<LogPartitionerParams> baseParams;
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
		final String validOutputFileName =
			outputPath.toFile().getAbsolutePath();

		baseParams = a(BaseParams);

		validParams =
			baseParams.but(with(inputFileName, validInputFileName)).but(
					with(outputFileName, validOutputFileName));
	}

	@After
	public void tearDown() throws Exception {
		this.inputFile.delete();
		this.inputFile = null;
		this.baseParams = null;
		this.validParams = null;
	}

	@Test
	public void testConstructorValidParams() throws Exception {

		final LogPartitionerParams lpp = make(validParams);
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 0);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidInputFile() throws Exception {

		// Missing inputFile
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(inputFileName, (String) null));
		try {
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidInputFormat() throws Exception {

		// Missing inputFormat
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(inputFormat,
					(LogPartitionerFileFormat) null));
		try {
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidInputFieldSeparator() throws Exception {

		// Missing inputFieldSeparator
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(inputFieldSeparator, (Character) null));
		try {
			make(invalidParams);
			fail("Expected NullPointerException (unboxing error)");
		} catch (NullPointerException x) {
			logger.fine("Expected (unboxing error): " + x.toString());
		}

		try {
			final LogPartitionerParams lpp = make(validParams);
			LogPartitionerFileFormat invalidFormat = null;
			new LogPartitionerParams(lpp.isHelp(), lpp.getErrors(),
					lpp.getInputFileName(), invalidFormat,
					lpp.getInputFieldSeparator(), lpp.getInputLineSeparator(),
					lpp.getOutputFileName(), lpp.getOutputFormat(),
					lpp.getOutputFieldSeparator(),
					lpp.getOutputLineSeparator(), lpp.getPartitionCount());
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidInputLineSeparator() throws Exception {

		// Missing inputLineSeparator
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(inputLineSeparator, (String) null));
		try {
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidOutputFile() throws Exception {

		// Missing outputFile
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(outputFileName, (String) null));
		try {
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidOutputFormat() throws Exception {

		// Missing outputFormat
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(outputFormat,
					(LogPartitionerFileFormat) null));
		try {
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidOutputFieldSeparator() throws Exception {

		// Missing outputFieldSeparator
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(outputFieldSeparator, (Character) null));
		try {
			make(invalidParams);
			fail("Expected NullPointerException (unboxing error)");
		} catch (NullPointerException x) {
			logger.fine("Expected (unboxing error): " + x.toString());
		}

		try {
			final LogPartitionerParams lpp = make(validParams);
			LogPartitionerFileFormat invalidFormat = null;
			new LogPartitionerParams(lpp.isHelp(), lpp.getErrors(),
					lpp.getInputFileName(), lpp.getInputFormat(),
					lpp.getInputFieldSeparator(), lpp.getInputLineSeparator(),
					lpp.getOutputFileName(), invalidFormat,
					lpp.getOutputFieldSeparator(),
					lpp.getOutputLineSeparator(), lpp.getPartitionCount());
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidOutputLineSeparator() throws Exception {

		// Missing outputLineSeparator
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(outputLineSeparator, (String) null));
		try {
			make(invalidParams);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException x) {
			logger.fine("Expected: " + x.toString());
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructorInvalidPartitionCount() throws Exception {

		// Missing partitionCount
		Maker<LogPartitionerParams> invalidParams =
			validParams.but(with(partitionCount, (Integer) null));
		try {
			make(invalidParams);
			fail("Expected NullPointerException (unboxing error)");
		} catch (NullPointerException x) {
			logger.fine("Expected (unboxing error): " + x.toString());
		}

		final LogPartitionerParams lpp = make(validParams);
		final int[] invalidParitionCounts = new int[] {
				0, -1, -2, Integer.MIN_VALUE };
		for (int invalidParitionCount : invalidParitionCounts) {
			try {
				new LogPartitionerParams(lpp.isHelp(), lpp.getErrors(),
						lpp.getInputFileName(), lpp.getInputFormat(),
						lpp.getInputFieldSeparator(),
						lpp.getInputLineSeparator(), lpp.getOutputFileName(),
						lpp.getOutputFormat(), lpp.getOutputFieldSeparator(),
						lpp.getOutputLineSeparator(), invalidParitionCount);
				make(invalidParams);
				fail("Expected IllegalArgumentException");
			} catch (IllegalArgumentException x) {
				logger.fine("Expected: " + x.toString());
			}
		}

	}

}
