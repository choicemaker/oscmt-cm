package com.choicemaker.cm.transitivity.core;


public interface TransitivityProcessingLog {

	/** This methods gets the most recent processing event */
	TransitivityEvent getCurrentTransitivityEvent();

//	/** This methods gets the id of the most recent processing event */
//	//	@Deprecated
//	int getCurrentTransitivityEventId();

	/**
	 * This method sets the current processing event with null additional info.
	 */
	void setCurrentTransitivityEvent(TransitivityEvent event);

	/**
	 * This method sets the current processing event with additional info.
	 */
	void setCurrentTransitivityEvent(TransitivityEvent stat, String info);

	/**
	 * This method gets the additional info associated with the most recent
	 * processing event.
	 */
	String getCurrentTransitivityEventInfo();

}