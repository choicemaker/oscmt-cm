package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerFileFormat.ALT_LINES;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.COMMA;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.EOL;
import static com.natpryce.makeiteasy.Property.newProperty;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;

public class UncheckedParameterMaker {

	public static final Property<UncheckedParams, Boolean> help = newProperty();
	public static final Property<UncheckedParams, String> inputFileName =
		newProperty();
	public static final Property<UncheckedParams, LogPartitionerFileFormat> inputFormat =
		newProperty();
	public static final Property<UncheckedParams, Character> inputFieldSeparator =
		newProperty();
	public static final Property<UncheckedParams, String> inputLineSeparator =
		newProperty();
	public static final Property<UncheckedParams, String> outputFileName =
		newProperty();
	public static final Property<UncheckedParams, LogPartitionerFileFormat> outputFormat =
		newProperty();
	public static final Property<UncheckedParams, Character> outputFieldSeparator =
		newProperty();
	public static final Property<UncheckedParams, String> outputLineSeparator =
		newProperty();
	public static final Property<UncheckedParams, Integer> partitionCount =
		newProperty();

	public static final Instantiator<UncheckedParams> BaseParams =
		new Instantiator<UncheckedParams>() {
			@Override
			public UncheckedParams instantiate(
					PropertyLookup<UncheckedParams> lookup) {
				UncheckedParams retVal =
					new UncheckedParams(lookup.valueOf(help, false), null,
							lookup.valueOf(inputFileName, (String) null),
							lookup.valueOf(inputFormat,
									LogPartitionerFileFormat.DELIMITED),
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
