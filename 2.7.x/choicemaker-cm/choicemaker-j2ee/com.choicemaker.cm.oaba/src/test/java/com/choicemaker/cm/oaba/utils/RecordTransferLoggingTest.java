/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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
