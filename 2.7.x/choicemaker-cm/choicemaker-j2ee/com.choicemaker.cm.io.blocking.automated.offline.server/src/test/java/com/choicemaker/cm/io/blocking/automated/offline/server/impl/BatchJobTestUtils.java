package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.BatchJobRigor;
import com.choicemaker.cm.batch.BatchJobStatus;

public class BatchJobTestUtils {

	private BatchJobTestUtils() {
	}

	public static BatchJob createBatchJobStub() {
		return new BatchJobStub();
	}

}

class BatchJobStub implements BatchJob {

	private static final long serialVersionUID = 271L;

	public static final String TEMP_DIR_PREFIX = "BatchJobTestUtils_";

	@Override
	public long getId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public String getUUID() {
		return UUID.randomUUID().toString();
	}

	@Override
	public int getOptLock() {
		return 0;
	}

	@Override
	public boolean isPersistent() {
		return false;
	}

	@Override
	public long getBatchParentId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public long getUrmId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public long getTransactionId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public String getExternalId() {
		return null;
	}

	@Override
	public BatchJobRigor getBatchJobRigor() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public long getParametersId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public long getServerId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public long getSettingsId() {
		return NONPERSISTENT_ID;
	}

	@Override
	public File getWorkingDirectory() {
		File retVal = null;
		try {
			retVal = Files.createTempDirectory(TEMP_DIR_PREFIX).toFile();
		} catch (IOException e) {
			fail(e.toString());
		}
		return retVal;
	}

	@Override
	public BatchJobStatus getStatus() {
		return BatchJobStatus.NEW;
	}

	@Override
	public Date getTimeStamp(BatchJobStatus status) {
		return new Date();
	}

	@Override
	public Date getRequested() {
		return new Date();
	}

	@Override
	public Date getQueued() {
		return new Date();
	}

	@Override
	public Date getStarted() {
		return new Date();
	}

	@Override
	public Date getCompleted() {
		return new Date();
	}

	@Override
	public Date getFailed() {
		return new Date();
	}

	@Override
	public Date getAbortRequested() {
		return new Date();
	}

	@Override
	public Date getAborted() {
		return new Date();
	}

	@Override
	public void setDescription(String description) {

	}

	@Override
	public void markAsQueued() {

	}

	@Override
	public void markAsStarted() {

	}

	@Override
	public void markAsReStarted() {

	}

	@Override
	public void markAsCompleted() {

	}

	@Override
	public void markAsFailed() {

	}

	@Override
	public void markAsAbortRequested() {

	}

	@Override
	public void markAsAborted() {

	}

	@Override
	public boolean shouldStop() {
		return false;
	}

}