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
