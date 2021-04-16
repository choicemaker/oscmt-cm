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
