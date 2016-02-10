package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.COMMA;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.EOL;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT.ALT_LINES;
import static com.natpryce.makeiteasy.Property.newProperty;

import com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.LOG_PARTITIONER_FILE_FORMAT;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;

public class CommandLineMaker {

	public static final Property<LogPartitionerParams, Boolean> help =
		newProperty();
	public static final Property<LogPartitionerParams, String> inputFileName =
		newProperty();
	public static final Property<LogPartitionerParams, LOG_PARTITIONER_FILE_FORMAT> inputFormat =
		newProperty();
	public static final Property<LogPartitionerParams, Character> inputFieldSeparator =
		newProperty();
	public static final Property<LogPartitionerParams, String> inputLineSeparator =
		newProperty();
	public static final Property<LogPartitionerParams, String> outputFileName =
		newProperty();
	public static final Property<LogPartitionerParams, LOG_PARTITIONER_FILE_FORMAT> outputFormat =
		newProperty();
	public static final Property<LogPartitionerParams, Character> outputFieldSeparator =
		newProperty();
	public static final Property<LogPartitionerParams, String> outputLineSeparator =
		newProperty();
	public static final Property<LogPartitionerParams, Integer> partitionCount =
		newProperty();

	public static final Instantiator<LogPartitionerParams> Params =
		new Instantiator<LogPartitionerParams>() {
			@Override
			public LogPartitionerParams instantiate(
					PropertyLookup<LogPartitionerParams> lookup) {
				LogPartitionerParams retVal =
					new LogPartitionerParams(lookup.valueOf(help, false), null,
							lookup.valueOf(inputFileName, (String) null),
							lookup.valueOf(inputFormat,
									LOG_PARTITIONER_FILE_FORMAT.DELIMITED),
							lookup.valueOf(inputFieldSeparator, COMMA),
							lookup.valueOf(inputLineSeparator, EOL),
							lookup.valueOf(outputFileName, (String) null),
							lookup.valueOf(outputFormat, ALT_LINES),
							lookup.valueOf(outputFieldSeparator, COMMA),
							lookup.valueOf(outputLineSeparator, EOL),
							lookup.valueOf(partitionCount, 10));
				return retVal;
			}
		};

}
