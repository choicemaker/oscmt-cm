/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cmit.io.db.sqlserver;

public class DownloadStats {
	private final String app;
	private final long acquireMsecs;
	private final int recordCount;
	private final long downloadMsecs;

	public DownloadStats(String _app, long _acqMsecs, int _count,
			long _dlMsecs) {
		this.app = _app;
		this.acquireMsecs = _acqMsecs;
		this.recordCount = _count;
		this.downloadMsecs = _dlMsecs;
	}

	public String toString() {
		String retVal = String.format(
				"App: %s,  " + "Connection acquisition %d (sec), "
						+ "Records: %d, " + "Download duration: %d (sec), "
						+ "Download rate: %f (recs/sec), "
						+ "Total duration: %d (sec)",
				getApp(), getAcquisitionMsecs() / 1000, getRecordCount(),
				getDownloadMsecs() / 1000, getRecordsPerSecond(),
				getTotalMsecs() / 1000);
		return retVal;
	}

	public String toCSV() {
		String retVal = String.format("%s, %d, %d, %d, %f, %d", getApp(),
				getAcquisitionMsecs() / 1000, getRecordCount(),
				getDownloadMsecs() / 1000, getRecordsPerSecond(),
				getTotalMsecs() / 1000);
		return retVal;
	}

	public String getApp() {
		return app;
	}

	public long getAcquisitionMsecs() {
		return acquireMsecs;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public long getDownloadMsecs() {
		return downloadMsecs;
	}

	public float getRecordsPerSecond() {
		final long msecs = getDownloadMsecs();
		float retVal = msecs == 0 ? 0f : (1000f * getRecordCount()) / msecs;
		return retVal;
	}

	public long getTotalMsecs() {
		return getAcquisitionMsecs() + getDownloadMsecs();
	}

}