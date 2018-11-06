package com.choicemaker.cm.oaba.services;

import static com.choicemaker.cm.oaba.services.ServiceMonitoring.*;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

public class ServiceMonitoringTest {

	private static final Logger log =
		Logger.getLogger(ServiceMonitoringTest.class.getName());

	private static final String SOURCE =
		ServiceMonitoringTest.class.getSimpleName();
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
