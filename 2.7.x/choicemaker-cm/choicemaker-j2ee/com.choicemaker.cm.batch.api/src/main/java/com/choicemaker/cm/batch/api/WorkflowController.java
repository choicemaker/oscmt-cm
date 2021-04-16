package com.choicemaker.cm.batch.api;

/**
 * A service that controls the progress of a batch job.
 */
public interface WorkflowController extends WorkflowMonitor {
	void upateWorkflow(BatchProcessingNotification bpn);
}
