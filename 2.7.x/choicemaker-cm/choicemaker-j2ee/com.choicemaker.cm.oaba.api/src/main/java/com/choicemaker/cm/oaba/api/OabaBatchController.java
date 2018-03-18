package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;

public interface OabaBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ OabaJobManager, ProcessController {
}
