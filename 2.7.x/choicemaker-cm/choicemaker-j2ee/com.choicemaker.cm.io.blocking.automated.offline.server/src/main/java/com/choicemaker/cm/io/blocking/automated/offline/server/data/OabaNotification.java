/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.data;

import java.util.Date;

import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.BatchJobProcessingEvent;
import com.choicemaker.cm.batch.BatchProcessingNotification;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessingEvent;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaJobJPA;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaProcessingEventJPA;

/**
 * This is the data object that gets passed to the UpdateStatusMDB message bean.
 * 
 * @author pcheung (original version)
 * @rphall rewrote as subclass of BatchProcessingNotification
 *
 */
public class OabaNotification extends BatchProcessingNotification {

	static final long serialVersionUID = 271;

	public OabaNotification(BatchJob job, OabaProcessingEvent event, Date timestamp) {
		this(job, event, timestamp, null);
	}

	public OabaNotification(BatchJob job, OabaProcessingEvent event, Date timestamp,
			String info) {
		super(job.getId(), OabaJobJPA.DISCRIMINATOR_VALUE,
				event.getPercentComplete(), event.getEventId(),
				OabaProcessingEventJPA.DISCRIMINATOR_VALUE, event.getEventName(),
				timestamp, info);
	}

	public OabaNotification(BatchJobProcessingEvent ope) {
		super(ope.getJobId(), OabaJobJPA.DISCRIMINATOR_VALUE, ope
				.getFractionComplete(), ope.getEventSequenceNumber(),
				OabaProcessingEventJPA.DISCRIMINATOR_VALUE, ope.getEventName(),
				ope.getEventTimestamp(), ope.getEventInfo());
	}

}
