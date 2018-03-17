package com.choicemaker.cm.oaba.api;

import java.net.URL;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.batch.api.WorkflowMonitor;

public interface OabaBatchController
		extends BatchResultsManager, WorkflowMonitor, OabaJobManager, ProcessController {
}
