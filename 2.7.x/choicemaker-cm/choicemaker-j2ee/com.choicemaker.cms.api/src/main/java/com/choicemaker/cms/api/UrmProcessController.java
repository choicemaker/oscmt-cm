package com.choicemaker.cms.api;

import java.net.URL;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessController;
import com.choicemaker.cm.batch.api.WorkflowMonitor;

public interface UrmProcessController
		extends WorkflowMonitor, UrmJobManager, ProcessController {

	/**
	 * Exports the results of a completed URM batch job to the
	 * specified container location, such as a file directory or an ftp site.
	 * The results are written to delimited-field file.
	 * 
	 * @param urmJob
	 *            a non-null URM job that has completed successfully.
	 * @param container
	 *            a location in which the results file should be created.
	 */
	void exportUrmResults(BatchJob urmJob, URL container);

}
