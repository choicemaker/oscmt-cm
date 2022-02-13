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
package com.choicemaker.cmit.utils.j2ee;

import javax.jms.JMSConsumer;
import javax.jms.Queue;

import com.choicemaker.cm.batch.api.EventPersistenceManager;

public interface TransitivityTestParameters extends OabaTestParameters {

	EventPersistenceManager getTransitivityProcessingController();

	JMSConsumer getTransitivityStatusConsumer();

	Queue getTransMatchSchedulerQueue();

	Queue getTransMatchDedupQueue();

	Queue getTransSerializationQueue();

//	AnalysisResultFormat getAnalysisResultFormat();
//
//	String getGraphPropertyName();

}
