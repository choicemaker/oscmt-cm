package com.choicemaker.cms.api;

import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;

public interface UrmBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ UrmJobManager, ProcessController {
}
