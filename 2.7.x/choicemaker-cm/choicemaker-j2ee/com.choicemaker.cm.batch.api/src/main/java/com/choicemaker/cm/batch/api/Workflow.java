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
package com.choicemaker.cm.batch.api;

import java.util.List;

/**
 * An object that describes the plan for processing a BatchJob. The workflow for
 * a batch job can change as the job progresses. For example, if a user aborts a
 * batch job, the remaining steps of the workflow will be truncated.
 */
public interface Workflow {
	String getWorkflowName();

	String getWorkflowDescription();

	List<WorkflowStep> getWorkflowSteps();
}
