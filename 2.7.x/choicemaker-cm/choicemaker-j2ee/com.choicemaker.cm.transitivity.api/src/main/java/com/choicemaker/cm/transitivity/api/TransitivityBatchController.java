package com.choicemaker.cm.transitivity.api;

import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;

public interface TransitivityBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ TransitivityJobManager, ProcessController {
}
