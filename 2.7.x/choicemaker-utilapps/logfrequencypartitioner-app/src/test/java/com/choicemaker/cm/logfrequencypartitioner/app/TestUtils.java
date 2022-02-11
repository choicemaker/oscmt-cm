package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.util.LogFrequencyPartitioner.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.choicemaker.util.LogFrequencyPartitioner.ValueRank;

class TestUtils {

	static final String TEMP_PREFIX = "LogPartitionerTest_";
	static final String TEMP_SUFFIX = ".csv";

	static String booleanArg(boolean isSet, String arg) {
		assert arg != null;
		assert !arg.contains(LogPartitionerCommandLine.MAGIC_DIVIDER);
		String retVal = "";
		if (isSet) {
			retVal =
				LogPartitionerCommandLine.OPTION_FLAG + arg
						+ LogPartitionerCommandLine.MAGIC_DIVIDER;
		}
		return retVal;
	}

	static String argWithQuotedValue(String arg, String value) {
		assert arg != null;
		assert !arg.contains(LogPartitionerCommandLine.MAGIC_DIVIDER);
		String retVal = "";
		if (value != null) {
			if (value.contains(LogPartitionerCommandLine.MAGIC_DIVIDER)) {
				throw new IllegalArgumentException(
						"value contains the magic divider ('"
								+ LogPartitionerCommandLine.MAGIC_DIVIDER
								+ "'): " + value);
			}
			retVal =
				LogPartitionerCommandLine.OPTION_FLAG + arg
						+ LogPartitionerCommandLine.MAGIC_DIVIDER + value
						+ LogPartitionerCommandLine.MAGIC_DIVIDER;
		}
		return retVal;
	}

	static String[] toCommandLine(UncheckedParams p) {
		if (p == null) {
			throw new IllegalArgumentException(
					"null log partitioner parameters");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(booleanArg(p.isHelp(), LogPartitionerCommandLine.ARG_HELP));
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_INPUT_FILE,
				p.getInputFileName()));
		if (p.getInputFormat() != null) {
			sb.append(argWithQuotedValue(
					LogPartitionerCommandLine.ARG_INPUT_FORMAT, p
							.getInputFormat().name()));
		}
		sb.append(argWithQuotedValue(
				LogPartitionerCommandLine.ARG_INPUT_FIELD_SEP,
				String.valueOf(p.getOutputFieldSeparator())));
		sb.append(argWithQuotedValue(
				LogPartitionerCommandLine.ARG_INPUT_LINE_SEP,
				p.getInputLineSeparator()));
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_OUTPUT_FILE,
				p.getOutputFileName()));
		if (p.getOutputFormat() != null) {
			sb.append(argWithQuotedValue(
					LogPartitionerCommandLine.ARG_OUTPUT_FORMAT, p
							.getOutputFormat().name()));
		}
		sb.append(argWithQuotedValue(
				LogPartitionerCommandLine.ARG_OUTPUT_FIELD_SEP,
				String.valueOf(p.getOutputFieldSeparator())));
		sb.append(argWithQuotedValue(
				LogPartitionerCommandLine.ARG_OUTPUT_LINE_SEP,
				p.getOutputLineSeparator()));
		sb.append(argWithQuotedValue(
				LogPartitionerCommandLine.ARG_PARTITION_COUNT,
				String.valueOf(p.getPartitionCount())));

		String s = sb.toString();
		String[] retVal = s.split(LogPartitionerCommandLine.MAGIC_DIVIDER);
		return retVal;
	}

	/**
	 * A sad example of code copying, because the LogFrequencyPartitioner
	 * readFile(..) method is not written for reuse (or testing). Until the
	 * LogFrequencyPartitioner is rewritten, this nearly duplicated code is
	 * needed.
	 */
	static List<ValueRank> readFileInternal(String fileName,
			Character elementSep, String lineSep) throws IOException {
		if (fileName == null) {
			throw new IllegalArgumentException("null file name");
		}
		if (lineSep == null) {
			lineSep = EOL;
		}

		Pattern p = null;
		if (elementSep != null) {
			String sElementSep = String.valueOf(elementSep);
			String literal = Pattern.quote(sElementSep);
			p = Pattern.compile(literal);
		}

		List<ValueRank> retVal = new ArrayList<>();
		BufferedReader in = null;
		try {
			FileReader fr =
				new FileReader(new File(fileName).getAbsoluteFile());
			in = new BufferedReader(fr);
			String line = in.readLine();
			while (line != null) {
				String value = null;
				String sCount = null;

				if (p != null) {
					// See caveat in LogFrequencyPartitioner.readFile...
					String[] tokens = p.split(line, -1);
					if (tokens.length != 2) {
						String msg = "Invalid line: '" + line + "'";
						throw new IllegalArgumentException(msg);
					}
					value = tokens[0].trim();
					sCount = tokens[1].trim();

				} else {
					value = line.trim();
					sCount = in.readLine();
					if (sCount == null) {
						String msg = "Missing count for value '" + value + "'";
						throw new IllegalArgumentException(msg);
					} else {
						sCount = sCount.trim();
					}
				}

				int rank = 0;
				try {
					rank = Integer.parseInt(sCount);
				} catch (NumberFormatException x) {
					String msg =
						"Invalid count (" + sCount + ") for value '" + value
								+ "'";
					throw new IllegalArgumentException(msg);
				}
				if (rank < MIN_RANK) {
					String msg =
						"Negative rank (" + rank + ") for value '" + value
								+ "'";
					throw new IllegalArgumentException(msg);
				}
				ValueRank vc = new ValueRank(value, rank);
				retVal.add(vc);

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		return retVal;
	}

	private TestUtils() {
	}

}
