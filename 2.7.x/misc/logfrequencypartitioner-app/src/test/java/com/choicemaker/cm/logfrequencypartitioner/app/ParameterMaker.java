package com.choicemaker.cm.logfrequencypartitioner.app;

import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerFileFormat.ALT_LINES;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.COMMA;
import static com.choicemaker.cm.logfrequencypartitioner.app.LogPartitionerParams.EOL;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.help;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputFieldSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputFormat;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.inputLineSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputFieldSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputFileName;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputFormat;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.outputLineSeparator;
import static com.choicemaker.cm.logfrequencypartitioner.app.UncheckedParameterMaker.partitionCount;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;

public class ParameterMaker {

	public static final Instantiator<LogPartitionerParams> BaseParams =
		new Instantiator<LogPartitionerParams>() {
			@Override
			public LogPartitionerParams instantiate(
					PropertyLookup<LogPartitionerParams> lookup) {
				LogPartitionerParams retVal =
					new LogPartitionerParams(lookup.valueOf(help, false), null,
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
