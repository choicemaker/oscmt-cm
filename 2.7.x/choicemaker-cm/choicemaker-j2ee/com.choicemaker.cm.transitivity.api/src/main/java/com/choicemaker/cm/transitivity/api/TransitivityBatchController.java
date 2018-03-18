package com.choicemaker.cm.transitivity.api;

import java.net.URL;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.batch.api.WorkflowMonitor;

public interface TransitivityBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ TransitivityJobManager, ProcessController {
}
