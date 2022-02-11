package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerFileFormat.*;
import static com.choicemaker.cm.logfrequencypartitioner.app.TestUtils.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.choicemaker.util.LogFrequencyPartitioner.ValueCount;
import com.choicemaker.util.LogFrequencyPartitioner.ValueRank;

public class LogPartitionerAppTest {

	private static final Logger logger = Logger
			.getLogger(LogPartitionerAppTest.class.getName());

	private static final char[] fieldDelimiters = new char[] {
			',', '|' };

	private static final String[] lineDelimiters = new String[] {
			"\r\n", "\n", "\r" };

	private static List<ValueCount> valueCounts;
	private static List<ValueRank> expected;
	private static int numPartitions;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		numPartitions = 4;

		final double sqrt10 = Math.pow(10., 0.5); // 3.162...
		List<ValueCount> counts = new ArrayList<>();
		counts.add(new ValueCount("value 1", 1));
		counts.add(new ValueCount("value 3", (int) sqrt10));
		counts.add(new ValueCount("value 4", 1 + (int) sqrt10));
		counts.add(new ValueCount("value 10", 10));
		counts.add(new ValueCount("value 31", (int) (10. * sqrt10)));
		counts.add(new ValueCount("value 32", 1 + (int) (10. * sqrt10)));
		counts.add(new ValueCount("value 100", 100));
		counts.add(new ValueCount("value 101", 100));
		counts.add(new ValueCount("value 1000", 100));
		valueCounts = Collections.unmodifiableList(counts);

		List<ValueRank> ranks = new ArrayList<>();
		ranks.add(new ValueRank("value 1", 0));
		ranks.add(new ValueRank("value 3", 0));
		ranks.add(new ValueRank("value 4", 0));
		ranks.add(new ValueRank("value 10", 1));
		ranks.add(new ValueRank("value 31", 1));
		ranks.add(new ValueRank("value 32", 2));
		ranks.add(new ValueRank("value 100", 3));
		ranks.add(new ValueRank("value 101", 3));
		ranks.add(new ValueRank("value 1000", 3));
		expected = Collections.unmodifiableList(ranks);
	}

	void testCreatePartitions(LogPartitionerFileFormat inputFormat,
			char inputFieldDelimiter, String inputLineDelimiter,
			LogPartitionerFileFormat outputFormat, char outputFieldDelimiter,
			String outputLineDelimiter) {

		final boolean isHelp = false;
		final List<String> errors = null;
		File inputFile = null;
		File outputFile = null;
		String inputFileName = null;
		String outputFileName = null;
		try {
			inputFile = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
			inputFileName = inputFile.getAbsolutePath();
			inputFile.delete();
			inputFile = null;

			outputFile = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
			outputFileName = outputFile.getAbsolutePath();
			outputFile.delete();
			outputFile = null;

			LogPartitionerApp.writeOutput(valueCounts, inputFileName,
					inputFormat, inputFieldDelimiter, inputLineDelimiter);
			LogPartitionerParams appParams =
				new LogPartitionerParams(isHelp, errors, inputFileName,
						inputFormat, inputFieldDelimiter, inputLineDelimiter,
						outputFileName, outputFormat, outputFieldDelimiter,
						outputLineDelimiter, numPartitions);
			final LogPartitionerApp app = new LogPartitionerApp(appParams);
			final int count = app.createPartitions();
			assertTrue(count == valueCounts.size());

			List<ValueRank> computed;
			if (outputFormat == ALT_LINES) {
				computed =
					readFileInternal(outputFileName, null, outputLineDelimiter);
			} else {
				computed =
					readFileInternal(outputFileName, outputFieldDelimiter,
							outputLineDelimiter);
			}

			assertTrue(expected.equals(computed));

		} catch (Exception e) {
			fail(e.toString());
		} finally {
			if (inputFile != null) {
				inputFile.delete();
				inputFile = null;
			}
			if (outputFile != null) {
				outputFile.delete();
				outputFile = null;
			}
			inputFile = new File(inputFileName);
			if (inputFile.exists()){
				inputFile.delete();
				inputFile = null;
				inputFileName = null;
			}
			outputFile = new File(outputFileName);
			if (outputFile.exists()){
				outputFile.delete();
				outputFile = null;
				outputFileName = null;
			}
		}
	}

	@Test
	public void testCreatePartitions() {
		for (LogPartitionerFileFormat inputFormat : LogPartitionerFileFormat
				.values()) {
			for (char inputFieldDelimiter : fieldDelimiters) {
				for (String inputLineDelimiter : lineDelimiters) {
					for (LogPartitionerFileFormat outputFormat : LogPartitionerFileFormat
							.values()) {
						for (char outputFieldDelimiter : fieldDelimiters) {
							for (String outputLineDelimiter : lineDelimiters) {
								testCreatePartitions(inputFormat,
										inputFieldDelimiter,
										inputLineDelimiter, outputFormat,
										outputFieldDelimiter,
										outputLineDelimiter);
							}
						}
					}
				}
			}
		}
	}

	void testWriteReadValueCounts(LogPartitionerFileFormat format,
			char fieldDelimiter, String lineDelimiter) {
		File f = null;
		FileWriter fw = null;
		try {
			f = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
			String fileName = f.getAbsolutePath();
			f.delete();
			f = null;

			LogPartitionerApp.writeOutput(valueCounts, fileName, format,
					fieldDelimiter, lineDelimiter);
			List<ValueCount> computed =
				LogPartitionerApp.readInput(fileName, format, fieldDelimiter,
						lineDelimiter);
			assertTrue(valueCounts.equals(computed));

		} catch (Exception e) {
			fail(e.toString());
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.warning(e.toString());
				}
				fw = null;
			}
			if (f != null) {
				f.delete();
				f = null;
			}
		}
	}

	void testWriteReadValueRanks(LogPartitionerFileFormat format,
			char fieldDelimiter, String lineDelimiter) {
		File f = null;
		FileWriter fw = null;
		try {
			f = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
			String fileName = f.getAbsolutePath();
			f.delete();
			f = null;

			LogPartitionerApp.writeOutput(expected, fileName, format,
					fieldDelimiter, lineDelimiter);
			List<ValueRank> computed;
			if (format == ALT_LINES) {
				computed = readFileInternal(fileName, null, lineDelimiter);
			} else {
				computed =
					readFileInternal(fileName, fieldDelimiter, lineDelimiter);
			}
			assertTrue(expected.equals(computed));

		} catch (Exception e) {
			fail(e.toString());
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.warning(e.toString());
				}
				fw = null;
			}
			if (f != null) {
				f.delete();
				f = null;
			}
		}
	}

	@Test
	public void testWriteReadValueCounts() {

		for (LogPartitionerFileFormat format : LogPartitionerFileFormat
				.values()) {
			for (char fieldDelimiter : fieldDelimiters) {
				for (String lineDelimiter : lineDelimiters) {
					testWriteReadValueCounts(format, fieldDelimiter,
							lineDelimiter);
				}
			}
		}
	}

	@Test
	public void testWriteReadValueRanks() {

		for (LogPartitionerFileFormat format : LogPartitionerFileFormat
				.values()) {
			for (char fieldDelimiter : fieldDelimiters) {
				for (String lineDelimiter : lineDelimiters) {
					testWriteReadValueCounts(format, fieldDelimiter,
							lineDelimiter);
				}
			}
		}
	}

}
