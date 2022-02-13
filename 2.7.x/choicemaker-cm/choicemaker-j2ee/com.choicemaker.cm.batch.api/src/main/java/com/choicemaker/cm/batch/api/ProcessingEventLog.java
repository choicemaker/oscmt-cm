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

import com.choicemaker.cm.args.ProcessingEvent;

public interface ProcessingEventLog {

	/** This methods gets the most recent processing event */
	ProcessingEvent getCurrentProcessingEvent();

	/** This methods gets the id of the most recent processing event */
	int getCurrentProcessingEventId();

	/**
	 * This method sets the current processing event with null additional info.
	 */
	void setCurrentProcessingEvent(ProcessingEvent event);

	/**
	 * This method sets the current processing event with additional info.
	 */
	void setCurrentProcessingEvent(ProcessingEvent event, String info);

	/**
	 * This method gets the additional info associated with the most recent
	 * processing event.
	 */
	String getCurrentProcessingEventInfo();

}