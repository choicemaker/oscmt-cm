/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.batch.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Exports results of a specified batch job to a location specifed by a Uniform
 * Resource Identifier (URI).
 */
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
	void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException;

}
