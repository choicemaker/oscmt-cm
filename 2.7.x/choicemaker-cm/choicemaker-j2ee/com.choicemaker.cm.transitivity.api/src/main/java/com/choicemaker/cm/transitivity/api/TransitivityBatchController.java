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
package com.choicemaker.cm.transitivity.api;

import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.ProcessController;

/**
 * Aggregates interfaces for creating, finding, aborting and restarting
 * transitivity jobs, and for exporting the results of these jobs.
 */
public interface TransitivityBatchController extends BatchResultsManager,
		/* FIXME WorkflowMonitor, */ TransitivityJobManager, ProcessController {
}
