package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.BaseParams;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputFormat;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputLineSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputFormat;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputLineSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.partitionCount;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.natpryce.makeiteasy.Maker;

@SuppressWarnings("unchecked")
public class LogPartitionerCommandLineTest {

	private File inputFile;
	private Maker<UncheckedParams> baseParams;
	private Maker<UncheckedParams> validParams;

	@Before
	public void setUp() throws Exception {

		this.inputFile =
			File.createTempFile(TestUtils.TEMP_PREFIX, TestUtils.TEMP_SUFFIX);

		// Input file must exist (but may be empty)
		assertTrue(inputFile.exists());

		// Output file should be a valid path but must not exist
		final Path outputPath =
			Files.createTempFile(TestUtils.TEMP_PREFIX, TestUtils.TEMP_SUFFIX);
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
	public void testParseValidCommandLine() {

		final UncheckedParams p = make(validParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 0);
	}

	@Test
	public void testMissingInputFile() {

		// Missing input file name
		Maker<UncheckedParams> invalidParams =
			validParams.but(with(inputFileName, (String) null));
		final UncheckedParams p = make(invalidParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 1);
	}

	@Test
	public void testMissingInputFormat() {

		// Missing input format
		Maker<UncheckedParams> invalidParams =
			validParams.but(with(inputFormat, (LogPartitionerFileFormat) null));
		final UncheckedParams p = make(invalidParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 1);
	}

	@Test
	public void testOptionalInputLineSeparator() {

		// Missing input line separator
		Maker<UncheckedParams> invalidParams =
			validParams.but(with(inputLineSeparator, (String) null));
		final UncheckedParams p = make(invalidParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 0);
	}

	@Test
	public void testMissingOutputFile() {

		// Missing output file name
		Maker<UncheckedParams> invalidParams =
			validParams.but(with(outputFileName, (String) null));
		final UncheckedParams p = make(invalidParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 1);
	}

	@Test
	public void testMissingOutputFormat() {

		// Missing output format
		Maker<UncheckedParams> invalidParams =
			validParams
					.but(with(outputFormat, (LogPartitionerFileFormat) null));
		final UncheckedParams p = make(invalidParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 1);
	}

	@Test
	public void testOptionalOutputLineSeparator() {

		// Missing output line separator
		Maker<UncheckedParams> invalidParams =
			validParams.but(with(outputLineSeparator, (String) null));
		final UncheckedParams p = make(invalidParams);
		assertTrue(p != null);
		assertTrue(p.getErrors().size() == 0);

		String[] args = TestUtils.toCommandLine(p);
		assertTrue(args != null);
		assertTrue(args.length > 0);

		LogPartitionerParams lpp = null;
		try {
			lpp = LogPartitionerCommandLine.parseCommandLine(args);
		} catch (ParseException | IOException e) {
			fail("Unexpected: " + e.toString());
		}
		assertTrue(lpp != null);
		assertTrue(lpp.getErrors().size() == 0);
	}

	@Test
	public void testInvalidPartitionCount() {

		// Missing count
		final int[] invalidParitionCounts = new int[] {
				0, -1, -2, Integer.MIN_VALUE };
		for (int invalidParitionCount : invalidParitionCounts) {
			Maker<UncheckedParams> invalidParams =
				validParams.but(with(partitionCount,
						(Integer) invalidParitionCount));
			final UncheckedParams p = make(invalidParams);
			assertTrue(p != null);
			assertTrue(p.getErrors().size() == 0);

			String[] args = TestUtils.toCommandLine(p);
			assertTrue(args != null);
			assertTrue(args.length > 0);

			LogPartitionerParams lpp = null;
			try {
				lpp = LogPartitionerCommandLine.parseCommandLine(args);
			} catch (ParseException | IOException e) {
				fail("Unexpected: " + e.toString());
			}
			assertTrue(lpp != null);
			assertTrue(lpp.getErrors().size() == 1);
		}

	}

}
