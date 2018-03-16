/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.batch.api.ProcessingEventLog;

/**
 * TransitivityProcessingLog restricts a logging context to a specific
 * Transitivity job by attaching an Transitivity job identifier to each
 * TransitivityEvent instance that it records or retrieves.
 *
 * @author rphall
 */
public class TransitivityProcessingLog implements ProcessingEventLog {

	private static final Logger logger = Logger
			.getLogger(TransitivityProcessingLog.class.getName());

	private static final String LOG_SOURCE = 
			TransitivityProcessingLog.class.getSimpleName();

	private final EntityManager em;
	private final BatchJob batchJob;

	public TransitivityProcessingLog(EntityManager em, BatchJob job) {
		if (em == null) {
			throw new IllegalArgumentException("null EntityManager");
		}
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid OABA job: " + job);
		}
		this.em = em;
		this.batchJob = job;
	}

	protected BatchProcessingEvent getCurrentTransitivityProcessingEvent() {
		return TransitivityEventManager
				.getCurrentBatchProcessingEvent(em, batchJob);
	}

	@Override
	public ProcessingEvent getCurrentProcessingEvent() {
		BatchProcessingEvent ope =
			getCurrentTransitivityProcessingEvent();
		ProcessingEvent retVal = ope.getProcessingEvent();
		return retVal;
	}

	@Override
	public int getCurrentProcessingEventId() {
		return getCurrentProcessingEvent().getEventId();
	}

	@Override
	public String getCurrentProcessingEventInfo() {
		BatchProcessingEvent ope =
			getCurrentTransitivityProcessingEvent();
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
		TransitivityEventManager.updateStatus(em, batchJob, event,
				new Date(), info);
	}

	@Override
	public String toString() {
		return "TransitivityProcessingLog [jobId=" + batchJob.getId() + "]";
	}

}
