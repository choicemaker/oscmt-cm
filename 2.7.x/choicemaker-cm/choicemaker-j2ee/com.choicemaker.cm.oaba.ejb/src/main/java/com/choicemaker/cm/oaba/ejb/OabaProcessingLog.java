/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobProcessingEvent;
import com.choicemaker.cm.batch.api.ProcessingEventLog;

/**
 * OabaProcessingLog restricts a logging context to a specific OABA job by
 * attaching an OABA job identifier to each OabaEvent instance that it records
 * or retrieves.
 *
 * @author rphall
 */
public class OabaProcessingLog implements ProcessingEventLog {

	private static final Logger logger = Logger
			.getLogger(OabaProcessingLog.class.getName());

	private static final String LOG_SOURCE = 
			OabaProcessingLog.class.getSimpleName();

	private final EntityManager em;
	private final BatchJob batchJob;

	public OabaProcessingLog(EntityManager em, BatchJob job) {
		if (em == null) {
			throw new IllegalArgumentException("null EntityManager");
		}
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid OABA job: " + job);
		}
		this.em = em;
		this.batchJob = job;
	}

	protected BatchJobProcessingEvent getCurrentOabaProcessingEvent() {
		return OabaProcessingControllerBean.getCurrentBatchProcessingEvent(em,
				batchJob);
	}

	@Override
	public ProcessingEvent getCurrentProcessingEvent() {
		BatchJobProcessingEvent ope = getCurrentOabaProcessingEvent();
		ProcessingEvent retVal = ope.getProcessingEvent();
		return retVal;
	}

	@Override
	public int getCurrentProcessingEventId() {
		return getCurrentProcessingEvent().getEventId();
	}

	@Override
	public String getCurrentProcessingEventInfo() {
		BatchJobProcessingEvent ope = getCurrentOabaProcessingEvent();
		String retVal = ope.getEventInfo();
		return retVal;
	}

	@Override
	public void setCurrentProcessingEvent(ProcessingEvent event) {
		setCurrentProcessingEvent(event, null);
	}

	@Override
	public void setCurrentProcessingEvent(ProcessingEvent event, String info) {
		logger.info(LOG_SOURCE + ".setCurrentProcessingEvent: " + event + " (job "
				+ this.batchJob.getId() + ")");
		OabaProcessingControllerBean.updateStatus(em, batchJob, event,
				new Date(), info);
	}

	@Override
	public String toString() {
		return "OabaProcessingLog [jobId=" + batchJob.getId() + "]";
	}

}
