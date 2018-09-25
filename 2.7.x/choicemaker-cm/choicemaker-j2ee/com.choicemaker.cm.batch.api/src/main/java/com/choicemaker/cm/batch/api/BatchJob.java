package com.choicemaker.cm.batch.api;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import com.choicemaker.cm.args.PersistentObject;

public interface BatchJob extends PersistentObject, Serializable {

	/** Minimum valid value for fractionComplete (inclusive) */
	int MIN_PERCENTAGE_COMPLETED = 0;

	/** Maximum valid value for fractionComplete (inclusive) */
	int MAX_PERCENTAGE_COMPLETED = 100;

	BatchJobRigor DEFAULT_RIGOR = BatchJobRigor.COMPUTED;

	long getBatchParentId();

	long getUrmId();

	long getTransactionId();

	String getExternalId();

	/**
	 * Indicates whether the results of a batch job have been (in the case of a
	 * completed job) or will be (in the case of a running job)
	 * {@link BatchJobRigor#ESTIMATED estimated} or
	 * {@link BatchJobRigor#COMPUTED computed}.
	 */
	BatchJobRigor getBatchJobRigor();

	String getDescription();

	long getParametersId();

	long getServerId();

	long getSettingsId();

	/**
	 * Returns the working directory of this job. This directory should be
	 * accessible from the server(s) on which this job is processed and in the
	 * case of multiple servers, the directory should represent the same
	 * physical location across all the servers.
	 */
	File getWorkingDirectory();

	BatchJobStatus getStatus();

	Date getTimeStamp(BatchJobStatus status);

	Date getRequested();

	Date getQueued();

	Date getStarted();

	Date getCompleted();

	Date getFailed();

	Date getAbortRequested();

	Date getAborted();

	void setDescription(String description);

	void markAsQueued();

	void markAsStarted();

	/**
	 * This method is called when the job is restarted. This method doesn't
	 * check if the status is currently queued.
	 */
	void markAsReStarted();

	void markAsCompleted();

	void markAsFailed();

	void markAsAbortRequested();

	void markAsAborted();

	boolean stopProcessing();

}
