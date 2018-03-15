package com.choicemaker.cm.args;

import java.io.Serializable;

public interface ProcessingEvent extends Serializable {

	/** Returns the event name for this entry */
	String getEventName();

	int getEventId();

	float getPercentComplete();

}