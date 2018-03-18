package com.choicemaker.cms.api;

import java.net.URL;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.batch.api.WorkflowMonitor;

public interface UrmBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ UrmJobManager, ProcessController {
}
