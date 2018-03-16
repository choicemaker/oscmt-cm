package com.choicemaker.cm.oaba.api;

import java.net.URL;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.batch.api.WorkflowMonitor;

public interface OabaProcessController
		extends WorkflowMonitor, OabaJobManager, ProcessController {

	/**
	 * Exports the results of a completed OABA batch job to the specified
	 * container location, such as a file directory or an ftp site. The results
	 * are written to delimited-field file.
	 * 
	 * @param oabaJob
	 *            a non-null OABA job that has completed successfully.
	 * @param container
	 *            a location in which the results file should be created.
	 */
	void exportOabaResults(BatchJob oabaJob, URL container);

}
