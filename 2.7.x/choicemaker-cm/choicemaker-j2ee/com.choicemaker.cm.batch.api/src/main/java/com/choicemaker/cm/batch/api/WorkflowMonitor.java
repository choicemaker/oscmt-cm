package com.choicemaker.cm.batch.api;

/**
 * A service that monitors the progress of a batch job.
 */
public interface WorkflowMonitor extends WorkflowListener {
	Workflow getWorkflow(BatchJob batchJob);

	WorkflowStep getCurrentWorkflowStep(BatchJob batchJob);
}
