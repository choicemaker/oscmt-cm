package com.choicemaker.cm.transitivity.api;

import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;

/**
 * Aggregates interfaces for creating, finding, aborting and restarting
 * transitivity jobs, and for exporting the results of these jobs.
 */
public interface TransitivityBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ TransitivityJobManager, ProcessController {
}
