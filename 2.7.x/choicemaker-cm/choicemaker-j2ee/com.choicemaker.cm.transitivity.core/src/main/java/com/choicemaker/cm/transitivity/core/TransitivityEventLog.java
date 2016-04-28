/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

public interface TransitivityEventLog {

	/** This methods gets the most recent processing event */
	TransitivityEvent getCurrentTransitivityEvent();

	/** This methods gets the id of the most recent processing event */
	int getCurrentTransitivityEventId();

	/**
	 * This method sets the current processing event with null additional info.
	 */
	void setCurrentTransitivityEvent(TransitivityEvent event);

	/**
	 * This method sets the current processing event with additional info.
	 */
	void setCurrentTransitivityEvent(TransitivityEvent event, String info);

	/**
	 * This method gets the additional info associated with the most recent
	 * processing event.
	 */
	String getCurrentTransitivityEventInfo();

}