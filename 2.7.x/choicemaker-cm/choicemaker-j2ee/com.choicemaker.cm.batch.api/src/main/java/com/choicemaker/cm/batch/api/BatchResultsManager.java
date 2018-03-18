package com.choicemaker.cm.batch.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface BatchResultsManager {

	/**
	 * Exports the results of a completed batch job to the specified container
	 * location, such as a file directory or an ftp site. The results are
	 * written to delimited-field file.
	 * 
	 * @param oabaJob
	 *            a non-null OABA job that has completed successfully.
	 * @param container
	 *            a location in which the results file should be created.
	 * @throws IOException
	 *             if file can not be read or written.
	 * @throws URISyntaxException 
	 */
	void exportResults(BatchJob batchJob, URI container) throws IOException, URISyntaxException;

}
