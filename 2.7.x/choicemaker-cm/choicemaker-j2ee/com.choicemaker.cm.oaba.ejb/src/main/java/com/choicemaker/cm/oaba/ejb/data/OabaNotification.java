/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb.data;

import java.util.Date;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.batch.api.BatchProcessingNotification;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.ejb.OabaJobJPA;
import com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA;

/**
 * This is the data object that gets passed to the UpdateStatusMDB message bean.
 * 
 * @author pcheung (original version)
 * @rphall rewrote as subclass of BatchProcessingNotification
 *
 */
public class OabaNotification extends BatchProcessingNotification {

	static final long serialVersionUID = 271;

	public OabaNotification(BatchJob job, OabaEventBean event, Date timestamp) {
		this(job, event, timestamp, null);
	}

	public OabaNotification(BatchJob job, OabaEventBean event, Date timestamp,
			String info) {
		super(job.getId(), OabaJobJPA.DISCRIMINATOR_VALUE,
				event.getFractionComplete(), event.getEventId(),
				OabaProcessingEventJPA.DISCRIMINATOR_VALUE, event.getEventName(),
				timestamp, info);
	}

	public OabaNotification(BatchProcessingEvent ope) {
		super(ope.getJobId(), OabaJobJPA.DISCRIMINATOR_VALUE, ope
				.getFractionComplete(), ope.getEventSequenceNumber(),
				OabaProcessingEventJPA.DISCRIMINATOR_VALUE, ope.getEventName(),
				ope.getEventTimestamp(), ope.getEventInfo());
	}

}
