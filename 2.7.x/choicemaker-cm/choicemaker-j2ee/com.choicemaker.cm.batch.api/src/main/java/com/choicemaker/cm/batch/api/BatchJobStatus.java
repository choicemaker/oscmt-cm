package com.choicemaker.cm.batch.api;

public enum BatchJobStatus {
	NEW, QUEUED, PROCESSING, COMPLETED, FAILED, ABORT_REQUESTED, ABORTED
}