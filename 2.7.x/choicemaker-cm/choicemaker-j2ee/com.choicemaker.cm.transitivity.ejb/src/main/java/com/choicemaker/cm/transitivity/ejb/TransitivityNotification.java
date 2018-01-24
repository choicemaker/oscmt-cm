/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.util.Date;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchProcessingNotification;
import com.choicemaker.cm.transitivity.api.TransitivityBatchProcessingEvent;

/**
 * This is the data object that gets passed to the UpdateStatusMDB message bean.
 * 
 * @author pcheung (original version)
 * @rphall rewrote as subclass of BatchProcessingNotification
 *
 */
public class TransitivityNotification extends BatchProcessingNotification {

	static final long serialVersionUID = 271;

	public TransitivityNotification(BatchJob job, ProcessingEvent event,
			Date timestamp) {
		this(job, event, timestamp, null);
	}

	public TransitivityNotification(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		super(job.getId(), TransitivityJobJPA.DISCRIMINATOR_VALUE, event
				.getPercentComplete(), event.getEventId(),
				TransitivityProcessingEventJPA.DISCRIMINATOR_VALUE, event
						.getEventName(), timestamp, info);
	}

	public TransitivityNotification(TransitivityBatchProcessingEvent ope) {
		super(ope.getJobId(), TransitivityJobJPA.DISCRIMINATOR_VALUE, ope
				.getFractionComplete(), ope.getEventSequenceNumber(),
				TransitivityProcessingEventJPA.DISCRIMINATOR_VALUE, ope
						.getEventName(), ope.getEventTimestamp(), ope
						.getEventInfo());
	}

}
