package com.choicemaker.cm.oaba.utils;

//import static org.junit.Assert.*;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FS0;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FS1;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.FS2;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logConnectionAcquisition;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logRecordIdCount;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logTransferRate;

import java.util.logging.Logger;

import org.junit.Test;

public class RecordTransferLoggingTest {

	private static final Logger log =
		Logger.getLogger(RecordTransferLoggingTest.class.getName());

	private static final String SOURCE =
		RecordTransferLoggingTest.class.getSimpleName();
	private static final String METHOD = "aMethod:";
	private static final String TAG = SOURCE + "." + METHOD + ": ";

	@Test
	public void testLogRecordIdCount() {
		logRecordIdCount(log, FS1, TAG, "aUUID", 3);
	}

	@Test
	public void testLogConnectionAcquisition() {
		logConnectionAcquisition(log, FS0, TAG, 397);
	}

	@Test
	public void testLogRecordTransferRate() {
		logTransferRate(log, FS2, TAG, 5, 99);
	}

}
