/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_GROUPS_FILE;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_PAIRS_FILE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.ejb.BatchExportUtils;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class TransitivityResultsManagerBean implements BatchResultsManager {

	@EJB
	TransitivityJobManager transitivityJobManager;

	@EJB
	private OperationalPropertyController propController;

	@Override
	public void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException {
		Precondition.assertBoolean("not a Transitivity Job entity",
				batchJob instanceof TransitivityJobEntity);

		BatchExportUtils.exportResults(batchJob, container, propController,
				PN_TRANSITIVITY_CACHED_PAIRS_FILE);
		BatchExportUtils.exportResults(batchJob, container, propController,
				PN_TRANSITIVITY_CACHED_GROUPS_FILE);
	}

}
