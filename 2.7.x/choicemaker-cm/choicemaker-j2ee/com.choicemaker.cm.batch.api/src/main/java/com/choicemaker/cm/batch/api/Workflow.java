package com.choicemaker.cm.batch.api;

import java.util.List;

/**
 * An object that describes the plan for processing a BatchJob. The workflow for
 * a batch job can change as the job progresses. For example, if a user aborts a
 * batch job, the remaining steps of the workflow will be truncated.
 */
public interface Workflow {
	String getWorkflowName();

	String getWorkflowDescription();

	List<WorkflowStep> getWorkflowSteps();
}
