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

import java.util.Date;
import java.util.List;

//import javax.ejb.Local;

import com.choicemaker.cm.args.ProcessingEvent;

public interface EventPersistenceManager {

	/**
	 * The name of a system property that can be set to "true" to turn on
	 * redundant order-by checking.
	 */
	public static final String PN_PROCESSINGEVENT_ORDERBY_DEBUGGING =
		"ProcessingEventOrderByDebugging";

	List<BatchProcessingEvent> findAllProcessingEvents();

	List<BatchProcessingEvent> findProcessingEventsByJobId(long id);

	/** Returns a count of the number of events deleted */
	int deleteProcessingEventsByJobId(long id);

	ProcessingEventLog getProcessingLog(BatchJob job);

	void updateStatusWithNotification(BatchJob job, ProcessingEvent event,
			Date timestamp, String info);

	ProcessingEvent getCurrentProcessingEvent(BatchJob batchJob);

}
