package com.choicemaker.cm.batch.api;

/**
 * A step in the workflow that describes how a batch job is processed.
 * A step may consist of a series of substeps, which will be listed in the
 * workflow for the step itself. Conversely, a workflow step may be part of the
 * workflow for some parent step. If a step is part of a top-level workflow,
 * the parent step and parent workflow will be null.
 */
public interface WorkflowStep {
	String getStepName();
	String getStepDescription();
	Workflow getStepWorkflow();
	WorkflowStep getParentStep();
	Workflow getParentWorkflow();
}
