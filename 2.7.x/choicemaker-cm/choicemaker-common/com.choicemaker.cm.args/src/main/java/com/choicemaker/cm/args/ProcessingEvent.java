/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.args;

import java.io.Serializable;

/**
 * Processing events are simple, in-memory, read-only notifications. They are
 * essentially non-persistent versions of BatchProcessingEvents, which can be
 * persistent.
 */
public interface ProcessingEvent extends Serializable {

	/** @return the event name for this entry */
	String getEventName();

	int getEventId();

	float getFractionComplete();

}
