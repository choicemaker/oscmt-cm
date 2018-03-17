package com.choicemaker.cm.batch.api;

import java.net.URL;

public interface BatchResultsManager {

	/**
	 * Exports the results of a completed batch job to the specified
	 * container location, such as a file directory or an ftp site. The results
	 * are written to delimited-field file.
	 * 
	 * @param oabaJob
	 *            a non-null OABA job that has completed successfully.
	 * @param container
	 *            a location in which the results file should be created.
	 */
	void exportResults(BatchJob batchJob, URL container);

}
