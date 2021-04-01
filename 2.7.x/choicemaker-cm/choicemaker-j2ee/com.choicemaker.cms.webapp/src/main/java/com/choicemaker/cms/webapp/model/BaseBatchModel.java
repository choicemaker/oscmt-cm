package com.choicemaker.cms.webapp.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;

/**
 * A non-persistent, but serializable, representation of a BatchJob.
 * 
 * @author rphall
 *
 */
public class BaseBatchModel implements Serializable {

	private static final long serialVersionUID = 271L;

	private long id;
	// private String type;
	private String externalId;
	private String description;
	private String status;
	private Instant creationTimestamp;
	private Instant statusTimestamp;

	public BaseBatchModel() {
	}

	public BaseBatchModel(BatchJob batchJob) {
		if (batchJob != null) {
			this.id = batchJob.getId();
			this.externalId = batchJob.getExternalId();
			this.description = batchJob.getDescription();
			final BatchJobStatus batchJobStatus = batchJob.getStatus();
			this.status = batchJobStatus == null ? null : batchJobStatus.name();
			this.creationTimestamp = batchJob.getRequested() == null ? null
					: batchJob.getRequested().toInstant();
			final Date statusDate = batchJobStatus == null ? null : batchJob.getTimeStamp(batchJobStatus);
			this.statusTimestamp = statusDate == null ? null : statusDate.toInstant();
			
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// public String getType() {
	// return type;
	// }
	// public void setType(String type) {
	// this.type = type;
	// }
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Instant creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Instant getStatusTimestamp() {
		return statusTimestamp;
	}

	public void setStatusTimestamp(Instant statusTimestamp) {
		this.statusTimestamp = statusTimestamp;
	}

	public Duration getDuration() {
		Duration retVal = Duration.ZERO;
		Instant start = getCreationTimestamp();
		if (start != null) {
			Instant end;
			if (isTerminated()) {
				end = getStatusTimestamp();
			} else {
				end = Instant.now();
			}
			if (end != null && start.compareTo(end) < 0) {
				retVal = Duration.between(start, end);
			}
		}
		return retVal;
	}

	public boolean isTerminated() {
		boolean retVal = BatchLifeCycle.isTerminalStatus(getStatus());
		return retVal;
	}

}
