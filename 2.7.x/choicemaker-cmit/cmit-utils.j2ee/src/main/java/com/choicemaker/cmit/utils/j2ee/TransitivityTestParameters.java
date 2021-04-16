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
