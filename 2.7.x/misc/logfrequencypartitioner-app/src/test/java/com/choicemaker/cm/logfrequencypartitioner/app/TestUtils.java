package com.choicemaker.cm.logfrequencypartitioner.app;

class TestUtils {

	static final String TEMP_PREFIX = "LogPartitionerTest_";
	static final String TEMP_SUFFIX = ".csv";

	static String booleanArg(boolean isSet, String arg) {
		assert arg != null;
		assert !arg.contains(LogPartitionerCommandLine.MAGIC_DIVIDER);
		String retVal = "";
		if (isSet) {
			retVal = LogPartitionerCommandLine.OPTION_FLAG + arg + LogPartitionerCommandLine.MAGIC_DIVIDER;
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
						"value contains the magic divider ('" + LogPartitionerCommandLine.MAGIC_DIVIDER
								+ "'): " + value);
			}
			retVal = LogPartitionerCommandLine.OPTION_FLAG + arg + LogPartitionerCommandLine.MAGIC_DIVIDER + value + LogPartitionerCommandLine.MAGIC_DIVIDER;
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
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_INPUT_FILE, p.getInputFileName()));
		if (p.getInputFormat() != null) {
			sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_INPUT_FORMAT, p.getInputFormat()
					.name()));
		}
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_INPUT_FIELD_SEP,
				String.valueOf(p.getOutputFieldSeparator())));
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_INPUT_LINE_SEP,
				p.getInputLineSeparator()));
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_OUTPUT_FILE, p.getOutputFileName()));
		if (p.getOutputFormat() != null) {
			sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_OUTPUT_FORMAT, p.getOutputFormat()
					.name()));
		}
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_OUTPUT_FIELD_SEP,
				String.valueOf(p.getOutputFieldSeparator())));
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_OUTPUT_LINE_SEP,
				p.getOutputLineSeparator()));
		sb.append(argWithQuotedValue(LogPartitionerCommandLine.ARG_PARTITION_COUNT,
				String.valueOf(p.getPartitionCount())));
	
		String s = sb.toString();
		String[] retVal = s.split(LogPartitionerCommandLine.MAGIC_DIVIDER);
		return retVal;
	}

	private TestUtils() {
	}

}
